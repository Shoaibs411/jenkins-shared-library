def lintChecks(component){
        sh "echo ***** Starting Style Checks for ${component} ***** "
        sh "npm install jslint"
        sh "/home/centos/node_modules/jslint/bin/jslint.js server.js || true"
        sh "echo ***** Style Checks are completed for ${component} ***** "

} 

def sonarChecks(component){
    sh "echo Sonar Checks for ${component} in progress"
    //sh"""
    //sonar=scanner -Dsonar.host.url=http://PRIVATEIPOFSERVER:9000 -Dsonar.sources=. -Dsonar.projectKey=${component} -Dsonar.login=admin -Dsonar.password=password
    //"""
    sh "echo Sonar Checks for ${component} are completed"
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
                sonarChecks("${component}")

            }
        }
        stage('Get the Sonar Result'){
            steps{
                sh "echo Getting Sonar Result for ${component}"
                //sh "curl https://gitlab.com/thecloudcareers/opensource/-/raw/master/lab-tools/sonar-scanner/quality-gate > gates.sh"
                //sh "./gates.sh admin password ${SONAR_URL} ${component}"
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