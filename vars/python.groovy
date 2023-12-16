def lintChecks_python(component){
    sh "echo ***** Starting Style Checks for ${component} ***** "

    // Install pylint using pip
    sh "/full/path/to/pip install pylint"

    // Change directory to the directory containing the Python files
    sh "cd /home/centos/payment/"

    // Run pylint on all .py files in the specified directory
    sh "/full/path/to/pylint /home/centos/payment/*.py || true"

    sh "echo ***** Style Checks are completed for ${component} ***** "
}
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
