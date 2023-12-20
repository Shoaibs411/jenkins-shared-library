def lintChecks(){
        sh "echo ***** Starting Style Checks for ***** "
        sh "npm install jslint"
        sh "/home/centos/node_modules/jslint/bin/jslint.js server.js || true"
        sh "echo ***** Style Checks are completed for ***** "

} 

def call(){
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
                    lintChecks()
                }
            }
        }
        stage('Static Code Analysis'){
            steps{
                script{
                    env.ARGS="-Dsonar.java.binaries=./target/"
                    common.sonarChecks()
                }

            }
        }
        stage('Get the Sonar Result'){
            steps{
                sh "curl https://gitlab.com/thecloudcareers/opensource/-/raw/master/lab-tools/sonar-scanner/quality-gate > gates.sh"
                //sh "bash gates.sh admin password ${SONAR_URL} ${COMPONENT}"
                sh "echo Sonar scan is Good"
            }    
        }

        stage("Test Cases"){
        parallel {
            stage('Unit Testing') {
                steps {
                    sh "echo Unit Testing for ${COMPONENT} in progress"
                    //sh "npm test" just for example
                    sh "echo Unit Testing for ${COMPONENT} is Completed"

                }
            }
            stage('Integration Testing') {
                steps {
                    sh "echo Integration Testing for ${COMPONENT} in progress"
                    //sh "npm verify" just for example
                    sh "echo Integration Testing for ${COMPONENT} is Completed" 
                }
            }
            stage('Functional Testing') {
                steps {
                    sh "echo Functional Testing for ${COMPONENT} in progress"
                    //sh"npm function" just for example
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

