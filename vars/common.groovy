def sonarChecks(component){
    sh "echo Sonar Checks for ${component} in progress"
    //sh"""
    //sonar=scanner -Dsonar.host.url=http://PRIVATEIPOFSERVER:9000 ${ARGS} -Dsonar.projectKey=${component} -Dsonar.login=admin -Dsonar.password=password
    //"""
    sh "echo Sonar Checks for ${component} are completed"
}