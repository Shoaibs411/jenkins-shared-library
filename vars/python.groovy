def lintChecks_python(component){
        sh "echo ***** Starting Style Checks for ${component} ***** "
        //sh "/home/centos/payment/pylint payment.py || true"
        //sh "/home/centos/payment/pylint rabbitmq.py || true"
        sh """
        pip install pylint
        cd /home/centos/payment/
        pylint *.py || true
        """
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
                    lintChecks_python("${component}")
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
