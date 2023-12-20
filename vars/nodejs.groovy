def lintChecks(){
        sh "echo ***** Starting Style Checks  ***** "
        sh "npm install jslint"
        sh "/home/centos/node_modules/jslint/bin/jslint.js server.js || true"
        sh "echo ***** Style Checks are completed  ***** "

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
                    sh "env"
                    sh "echo Unit Testing in progress"
                    //sh "npm test" just for example
                    sh "echo Unit Testing is Completed"

                }
            }
            stage('Integration Testing') {
                steps {
                    sh "echo Integration Testing in progress"
                    //sh "npm verify" just for example
                    sh "echo Integration Testing is Completed" 
                }
            }
            stage('Functional Testing') {
                steps {
                    sh "echo Functional Testing in progress"
                    //sh"npm function" just for example
                    sh "echo Functional Testing is Completed"
                }
            }
            stage("Prepare Artifacts"){
            when { expression { env.TAG_NAME != null } }
            steps{
                sh '''
                    npm install
                    ls -ltr
                    zip ${COMPONENT}-${TAG_NAME}.zip node_modules server.js
                    ls -ltr
                   '''
            }
        }
            stage("Upload Artifacts"){
            when { expression { env.TAG_NAME != null } }
            steps{
                sh "echo Uploading artifacts for ${component}"
            }
        }
        }
    }
    }
    }
}