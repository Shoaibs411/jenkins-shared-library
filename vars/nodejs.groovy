def lintChecks(component){
        sh "echo ***** Starting Style Checks ${component} ***** "
        sh "npm install jslint"
        sh "/home/centos/node_modules/jslint/bin/jslint.js server.js || true"
        sh "echo ***** Style Checks are completed ***** "

} 

def PipeLine(component){
    pipeline{
    agent {
        label "ws"
    }
    stages{
        stage('Lint Checks'){
            steps{
                script{
                    lintChecks()
                }
            }
        }
        stage('Static Code Analysis'){
            steps{
                sh "echo ***** Starting Static Code Analysis ***** "

            }
        }
    }
}
}