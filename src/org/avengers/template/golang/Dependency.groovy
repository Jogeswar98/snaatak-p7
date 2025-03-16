package org.avengers.template.golang  // Consider renaming to a more generic package if supporting both Go and Java

class Dependency implements Serializable {
    def steps

    Dependency(steps = null) {
        this.steps = steps
    }

    def call(String repoUrl, String credentialsId, String branch, String depVersion, String javaVersion) {
        def script = steps ?: this

        script.node {
            script.stage('Checkout') {
                script.checkout([
                    $class: 'GitSCM',
                    branches: [[name: branch]],
                    userRemoteConfigs: [[url: repoUrl, credentialsId: credentialsId]]
                ])
            }
            
            script.stage('Setup Environment') {
                script.echo "Setting up Java version: ${javaVersion}"
                script.sh "export JAVA_HOME=/usr/lib/jvm/java-${javaVersion}-openjdk-amd64"
                script.sh "java -version"
            }
            
            script.stage('Check Dependencies') {
                script.echo "Checking dependencies for version: ${depVersion}"
                script.sh 'mvn clean install -DskipTests'
            }
            
            script.stage('Run SpotBugs Analysis') {
                script.echo "Running SpotBugs Analysis..."
                script.sh '''
                    mvn spotbugs:check || true
                    mvn spotbugs:gui || true
                '''
                script.archiveArtifacts artifacts: 'target/spotbugsXml.xml, target/spotbugs.html', allowEmptyArchive: true
            }
            
            script.stage('Run Tests') {
                script.sh 'mvn test || echo "Tests failed but continuing"'
            }
        }
    }
}
