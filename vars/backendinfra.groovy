
def call(){
    properties([
        parameters([
            choice(choices: 'dev\nprod', description: 'Select the environment', name: "ENV"),
            choice(choices: 'apply\ndestroy', description: 'Chose an action', name: "ACTION"),
        ]),
    ])
    node('ws') {
        ansiColor('xterm'){
            git branch: 'main', url: "https://github.com/Shoaibs411/${COMPONENT}.git"
            stage('Terraform Init'){
                sh 
                '''
                    cd mutable-infra
                    terrafile -f env-dev/Terrafile
                    terraform init -backend-config=env-${ENV}/${ENV}-backend.tfvars
                '''
            }
            stage('Terraform Plan'){
                sh 
                '''
                    cd mutable-infra
                    terraform plan -var-file=env-${ENV}/${ENV}.tfvars
                '''
            }
            stage('Terraform Action'){
                sh 
                '''
                    cd mutable-infra
                    terraform ${ACTION} -auto-approve -var-file=env-${ENV}/${ENV}.tfvars
                '''
            }
            
        }
    }

// pipeline {
//     agent{
//         label 'ws'
//     }
//     options {
//         ansiColor('xterm')
//     }
//     parameters {
//         choice(name: 'ENV', choices: ['dev', 'prod'], description: 'Select the environment')
//         choice(name: 'ACTION', choices: ['apply', 'destroy'], description: 'Select apply or destroy')
//     }
//     stages {
//         stage('Terraform Init') {
//             steps {
//                 sh "terrafile -f env-dev/Terrafile"
//                 sh "terraform init -backend-config=env-${ENV}/${ENV}-backend.tfvars"
//             }
//         }
//         stage('Terraform Plan') {
//             steps {
//                 sh "terraform plan -var-file=env-${ENV}/${ENV}.tfvars"
//             }
//         }
//         stage('Terraform Action') {
//             steps {
//                 sh "terraform ${ACTION} -auto-approve -var-file=env-${ENV}/${ENV}.tfvars"
//             }
//         }
//     }
// }