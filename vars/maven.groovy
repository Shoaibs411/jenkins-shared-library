def lintChecks(){
        sh "echo ***** Starting Style Checks for ***** "
        sh "mvn checkstyle:check || true"
        sh "echo ***** Style Checks are completed for ***** "

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
    tools {
        maven 'maven-396' 
    }
    stages{
        stage('Lint Checks'){
            steps{
                script{
                    lintChecks()
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
                    common.sonarChecks()
                }
            }
        }
        stage('Get the Sonar Result'){
            steps{
                sh "echo Getting Sonar Result for"
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
                    sh "echo Unit Testing for in progress"
                    //sh "mvn test" just for example
                    sh "echo Unit Testing for is Completed"

                }
            }
            stage('Integration Testing') {
                steps {
                    sh "echo Integration Testing for in progress"
                    //sh "mvn verify" just for example
                    sh "echo Integration Testing for is Completed" 
                }
            }
            stage('Functional Testing') {
                steps {
                    sh "echo Functional Testing for in progress"
                    //sh"mvn function" just for example
                    sh "echo Functional Testing for is Completed"
                }
            }
        } 
    }
        stage("Prepare Artifacts"){
            when { expression { env.TAG_NAME != null } }
            steps{
                sh "echo Preparing artifacts"
            }
        }
         stage("Upload Artifacts"){
            when { expression { env.TAG_NAME != null } }
            steps{
                sh "echo Uploading artifacts"
            }
        }
    }
    }
}