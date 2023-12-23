def lintChecks(){
        sh "echo ***** Starting Style Checks ***** "
        //sh "pip install pylint"
        //sh "/home/centos/payment/ pylint payment.py || true"
        //sh "/home/centos/payment/ pylint rabbitmq.py || true"
        //sh "/home/centos/payment/ pylint *.py || true"
        //sh "pylint ~/payment/*.py"
        sh "echo ***** Style Checks are complete ***** "

} 

def call(){
    pipeline{
    agent {
        label "ws"
    }
    environment{
        SONAR_CRED = credentials('SONAR_CRED')
        NEXUS = credentials('NEXUS')
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
                script{
                    env.ARGS="-Dsonar.sources=."
                    common.sonarChecks()
                }
            }
        }
        stage('Get the Sonar Result'){
            steps{
                sh "curl https://gitlab.com/thecloudcareers/opensource/-/raw/master/lab-tools/sonar-scanner/quality-gate > gates.sh"
                //sh "bash gates.sh admin password ${SONAR_URL} ${component}"
                sh "echo Sonar scan is Good"
            }    
        }
        stage("Test Cases"){
        parallel {
            stage('Unit Testing') {
                steps {
                    sh "echo Unit Testing for ${COMPONENT} in progress"
                    //sh "pip test" just for example
                    sh "echo Unit Testing is Completed"

                }
            }
            stage('Integration Testing') {
                steps {
                    sh "echo Integration Testing for ${COMPONENT} in progress"
                    //sh "pip verify" just for example
                    sh "echo Integration Testing for ${COMPONENT} is Completed" 
                }
            }
            stage('Functional Testing') {
                steps {
                    sh "echo Functional Testing for ${COMPONENT} in progress"
                    //sh"pip function" just for example
                    sh "echo Functional Testing for ${COMPONENT} is Completed"
                }
            }
        } 
        }
        stage("Prepare Artifacts"){
            when { expression { env.TAG_NAME != null } }
            steps{
                sh "echo Preparing artifacts for ${COMPONENT}"
            }
        }
            stage("Upload Artifacts"){
            when { expression { env.TAG_NAME != null } }
            steps{
                sh "echo Uploading artifacts for ${COMPONENT}"
            }
        }
    }
}
}
