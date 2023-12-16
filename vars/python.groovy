def lintChecks(component){
        sh "echo ***** Starting Style Checks for ${component} ***** "
        //sh "pip install pylint"
        //sh "/home/centos/payment/pylint payment.py || true"
        //sh "/home/centos/payment/pylint rabbitmq.py || true"
        //sh "/home/centos/payment/ pylint *.py || true"
        sh "pylint ~/payment/*.py"
        sh "echo ***** Style Checks are completed for ${component}  ***** "

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
                sh "echo ***** Starting Static Code Analysis ***** "

            }
        }
    }
}
}
