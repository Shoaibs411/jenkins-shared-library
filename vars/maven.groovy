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
                    env.ARGS="-Dsonar.sources=."
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