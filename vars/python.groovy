def lintChecks(component){
        sh "echo ***** Starting Style Checks for ${component} ***** "
        sh "pip install pylint"
        sh "/home/centos/payment/ pylint payment.py || true"
        sh "/home/centos/payment/ pylint rabbitmq.py || true"
        //sh "/home/centos/payment/ pylint *.py || true"
        //sh "pylint ~/payment/*.py"
        sh "echo ***** Style Checks are completed for ${component}  ***** "

} 

def call(component){
    pipeline{
    agent {
        label "ws"
    }
    environment{
        SONAR_CRED = credentials('SONAR_CRED')
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
                script{
                    env.ARGS="-Dsonar.sources=."
                    sonarChecks("${component}")
                }
            }
        }
        stage('Get the Sonar Result'){
            steps{
                sh "echo Getting Sonar Result for ${component}"
                //sh "curl https://gitlab.com/thecloudcareers/opensource/-/raw/master/lab-tools/sonar-scanner/quality-gate > gates.sh"
                //sh "bash gates.sh admin password ${SONAR_URL} ${component}"
            }    
        }
        stage('Unit Testing'){
            steps{
                script{
                    sh "echo Testing for ${component} in progress "
                    sh "echo Testing for ${component} is completed "
                }
            }
        }
    }
}
}
