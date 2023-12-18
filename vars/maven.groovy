def lintChecks(component){
        sh "echo ***** Starting Style Checks for ${component} ***** "
        sh "mvn checkstyle:check || true"
        sh "echo ***** Style Checks are completed for ${component} ***** "

} 

def call(component){
    pipeline{
    agent {
        label "ws"
    }
    environment{
        SONAR_CRED = credentials('SONAR_CRED')
    }
    tools {
        maven 'maven-396' 
    }
    stages{
        stage('Lint Checks'){
            steps{
                script{
                    lintChecks("${component}")
                }
            }
        }
        stage('Compiling Java Code'){
            steps{
                sh "mvn clean compile"
                sh "ls -ltr target/"
            }
        }
        stage('Static Code Analysis'){
            steps{
                script{
                    env.ARGS="-Dsonar.java.binaries=./target/"
                    common.sonarChecks("${component}")
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
        stage("Test Cases"){
        parallel {
            stage('Unit Testing') {
                steps {
                    sh "echo Unit Testing for ${component} in progress"
                    //sh "mvn test" just for example
                    sh "echo Unit Testing for ${component} is Completed"

                }
            }
            stage('Integration Testing') {
                steps {
                    sh "echo Integration Testing for ${component} in progress"
                    //sh "mvn verify" just for example
                    sh "echo Integration Testing for ${component} is Completed" 
                }
            }
            stage('Functional Testing') {
                steps {
                    sh "echo Functional Testing for ${component} in progress"
                    //sh"mvn function" just for example
                    sh "echo Functional Testing for ${component} is Completed"
                }
            }
        } 
    }
        stage("Prepare Artifacts"){
            steps{
                sh "echo Preparing artifacts for ${component}"
            }
        }
         stage("Upload Artifacts"){
            steps{
                sh "echo Uploading artifacts for ${component}"
            }
        }
    }
    }
}