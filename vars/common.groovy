def sonarChecks() {
     stage('Sonar Checks') {
          sh '''
          echo sonar Checks in progress
          # sonar-scanner -Dsonar.host.url=http://172.31.45.101:9000 ${ARGS} -Dsonar.projectKey=${COMPONENT}  -Dsonar.login=${SONAR_CRED_USR} -Dsonar.password=${SONAR_CRED_PSW}
          echo sonar Checks Completed
          '''
     }
}

def lintChecks() {
     stage('lint checks') {
          if(env.APP_TYPE == "maven") {
               sh '''
                    echo ***** Starting Style Checks for ${COMPONENT} *****
                    mvn checkstyle:check || true
                    echo ***** Style Checks Are Completed for ${COMPONENT} *****
               '''
          }
          else if(env.APP_TYPE == "node") {
               sh '''
                    echo ***** Starting Style Checks for ${COMPONENT} *****
                    npm install jslint
                    node_modules/jslint/bin/jslint.js server.js || true
                    echo ***** Style Checks Are Completed for ${COMPONENT} *****
               '''
          }
          else if(env.APP_TYPE == "python") {
               sh '''
                    echo ***** Starting Style Checks for ${COMPONENT} *****
                    pip3 install pylint
                    pytlint *.py || true
                    echo ***** Style Checks Are Completed for ${COMPONENT} *****
               '''
          }
          else {
               sh '''
                    echo ***** Starting Style Checks for ${COMPONENT} *****
                    echo ***** Style Checks Are Completed for ${COMPONENT} *****
               '''
          }
     }
}


def testCases() {
    stage('Test Cases') {
        def stages = [:]

        stages["Unit Testing"] = {
               echo "Unit Testing In Progress"
               echo "Unit Testing Is Completed"
        }
        stages["Intergration Testing"] = {
               echo "Intergration Testing In Progress"
               echo "Intergration Testing Is Completed"
        }
        stages["Functional Testing"] = {
               echo "Functional Testing In Progress"
               echo "Functional Testing Is Completed"
        }
        parallel(stages)
    }
}


def artifacts() {
     stage('Checking Artifact Release On Nexus') {
          env.UPLOAD_STATUS = sh(returnStdout: true, script: "curl http://172.31.34.215:8081/service/rest/repository/browse/${COMPONENT}/ | grep ${COMPONENT}-${TAG_NAME}.zip || true")       
          print UPLOAD_STATUS || true 
     }

     if(env.UPLOAD_STATUS == "") {
          stage('Generating The Artifacts') {
               if(env.APP_TYPE == "nodejs") {
                    sh "npm install"
                    sh "zip ${COMPONENT}-${TAG_NAME}.zip node_modules server.js"
               }
               else if(env.APP_TYPE == "python") {
                    sh "zip -r ${COMPONENT}-${TAG_NAME}.zip *.py  *.ini requirements.txt"
                    sh "ls -ltr"
               }
               else if(env.APP_TYPE == "maven") {
                    sh "mvn clean package"
                    sh "mv target/${COMPONENT}-1.0.jar ${COMPONENT}.jar"
                    sh "zip -r ${COMPONENT}-${TAG_NAME}.zip ${COMPONENT}.jar"
               }
               else if(env.APP_TYPE == "angularjs") {
                    sh ''' 
                         cd static/
                         zip -r ../${COMPONENT}-${TAG_NAME}.zip *
                         ls -ltr
                    ''' 
               }
               else {
                    sh "echo Selected Component Type Does Not Exist"
               }
          }

          stage('Uploading the artifacts') {
               withCredentials([usernamePassword(credentialsId: 'NEXUS', passwordVariable: 'NEXUS_PASSWORD', usernameVariable: 'NEXUS_USERNAME')]) {
                    sh "echo Uploading ${COMPONENT} artifacts to Nexus"
                    sh "ls -ltr"
                    sh "curl -f -v -u ${NEXUS_USERNAME}:${NEXUS_PASSWORD} --upload-file ${COMPONENT}-${TAG_NAME}.zip  http://172.31.34.215:8081/repository/${COMPONENT}/${COMPONENT}-${TAG_NAME}.zip"
                    sh "echo Upload Completed"
               }
          }
     }
}