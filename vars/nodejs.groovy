def lintChecks(component){
        sh "echo ***** Starting Style Checks for ${component} ***** "
        sh "npm install jslint"
        sh "/home/centos/node_modules/jslint/bin/jslint.js server.js || true"
        sh "echo ***** Style Checks are completed for ${component} ***** "

} 

def sonarChecks(component){
    sh "echo Sonar Checks for ${component} in progress"
    //sh"""
    //sonar=scanner -Dsonar.host.url=http://PRIVATEIPOFSERVER:9000 -Dsonar.sources=. -Dsonar.projectKey=${component} -Dsonar.login=admin -Dsonar.password=password
    //"""
    sh "echo Sonar Checks for ${component} are completed"
}

def call(component){
    pipeline{
    agent {
        label "ws"
    }
    stages{
        stage('Lint Checks'){
            steps{
                script{
                    lintChecks("${component}")
                }
            }
        }
        stage('Static Code Analysis'){
            steps{
                sonarChecks("${component}")

            }
        }
        stage('Unit Testing'){
            steps{
                script{
                    sh "echo Testing in progress "
                }
            }
        }
    }
}
}