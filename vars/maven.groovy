def lintChecks_maven(component){
        sh "echo ***** Starting Style Checks for ${component} ***** "
        //sh "mvn checkstyle:check || true"
        sh "echo ***** Style Checks are completed for ${component} ***** "

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
                    lintChecks_maven("${component}")
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