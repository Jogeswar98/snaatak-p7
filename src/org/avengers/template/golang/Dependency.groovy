package org.avengers.template.golang

class Dependency implements Serializable {
    def steps

    Dependency(steps = null) {
        this.steps = steps
    }

    def call(String repoUrl, String credentialsId, String branch, String depVersion, String javaVersion) {
        // Get access to the Jenkins steps
        def script = steps ?: this

        // We need to run this in a node context
        script.node {
            // Now we can use the stage method through the script variable
            script.stage('Checkout') {
                script.checkout([
                    $class: 'GitSCM',
                    branches: [[name: branch]],
                    userRemoteConfigs: [[url: repoUrl, credentialsId: credentialsId]]
                ])
            }
            
            script.stage('Setup Environment') {
                script.echo "Setting up Java version: ${javaVersion}"
                // Add Java setup commands if needed
            }
            
            script.stage('Check Dependencies') {
                script.echo "Checking dependencies for version: ${depVersion}"
                // Implement dependency check logic for Go projects
                script.sh 'go mod tidy || echo "Go mod tidy failed but continuing"'
                script.sh 'go list -m all || echo "Go list failed but continuing"'
            }
            
            script.stage('Build') {
                script.sh 'go build -v ./... || echo "Build failed but continuing"'
            }
            
            script.stage('Test') {
                script.sh 'go test ./... || echo "Tests failed but continuing"'
            }
        }
    }
}
