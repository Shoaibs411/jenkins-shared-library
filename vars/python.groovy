def call() {
    node {
        git branch: 'main', url: "https://github.com/Shoaibs411/${COMPONENT}.git"
        common.lintChecks()
        env.ARGS="-Dsonar.sources=."
        common.sonarChecks()
        common.testCases()
        if(env.TAG_NAME != null) {
            common.artifacts()
        }
    }
}



/* Uncomment this to run it over Declarative approach */

// def call() {
//     pipeline {
//         agent any
//         environment {                      
//             SONAR_CRED = credentials('SONAR_CRED')
//             NEXUS = credentials('NEXUS')
//             SONAR_URL = "172.31.45.101"
//             NEXUS_URL = "172.31.34.215"
//         }
//         stages {
//             stage('Lint Checks'){
//                 steps {
//                     script {
//                         common.lintChecks()
//                     }
//                 }
//             }
//             stage('Static Code Analysis') {
//                 steps {
//                     script {
//                         env.ARGS="-Dsonar.sources=."
//                         common.sonarChecks()
//                     }
//                 }
//             }
//             stage('Get the Sonar Result') {
//                 steps {
//                     sh "curl https://gitlab.com/thecloudcareers/opensource/-/raw/master/lab-tools/sonar-scanner/quality-gate > gates.sh"
//                     // sh "bash gates.sh admin password ${SONAR_URL} ${COMPONENT}"
//                     sh "echo Scan Is Good"
//                 }
//             }
//         stage('Test Cases') {
//             parallel {
//                 stage('Unit Testing') {
//                     steps {
//                         sh "echo Unit Testing In Progress"
//                         // sh "pip test"
//                         sh "echo Unit Testing In Completed"
//                     }
//                 }
//                 stage('Integration Testing') {
//                     steps {
//                         sh "echo Integration Testing In Progress"
//                         // sh "pip verify"
//                         sh "echo Integration Testing In Completed"
//                     }
//                 }
//                 stage('Functional Testing') {
//                     steps {
//                         sh "echo Functional Testing In Progress"
//                         // sh "pip function"
//                         sh "echo Functional Testing In Completed"
//                         }
//                     }
//                 }
//             }
//             stage('Checking Artifacts Avaiability On Nexus') {
//                 when { expression { env.TAG_NAME != null } }
//                 steps {
//                     script {
//                         env.UPLOAD_STATUS = sh(returnStdout: true, script: "curl http://${NEXUS_URL}:8081/service/rest/repository/browse/${COMPONENT}/ | grep ${COMPONENT}-${TAG_NAME}.zip || true")
//                         print UPLOAD_STATUS
//                     }
//                 }
//             }
//             stage('Prepare Artifacts') {
//                 when { 
//                     expression { env.TAG_NAME != null } 
//                     expression { env.UPLOAD_STATUS == "" } 
//                 }
//                 steps {
//                     sh "echo Preparing Artifacts"
//                 }
//             }
//             stage('Uploading Artifacts') {
//                 when { 
//                     expression { env.TAG_NAME != null } 
//                     expression { env.UPLOAD_STATUS == "" } 
//                 }
//                 steps {
//                     sh "echo Uploading Artifacts"
//                 }
//             }
//         }
//     }
// }