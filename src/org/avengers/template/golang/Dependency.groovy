package org.avengers.template.golang

class Dependency implements Serializable {
    def steps

    Dependency(steps = null) {
        this.steps = steps
    }

    def call(String repoUrl, String credentialsId, String branch, String depVersion, String javaVersion) {
        def script = steps ?: this
        
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
            script.sh 'go mod tidy'
            script.sh 'go list -m all'
        }
        
        script.stage('Build') {
            script.sh 'go build -v ./...'
        }
        
        script.stage('Test') {
            script.sh 'go test ./...'
        }
    }
}
