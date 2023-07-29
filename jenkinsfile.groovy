pipeline {
    agent any
    
    tools {
        maven 'mvn' //configure jenkins tools with maven
        
    }



    stages {
        
        stage('Build 41') {
            steps{
                slackSend channel: 'jenkins', color: '#2211d9', message: "STARTED ${env.JOB_NAME} at #${env.BUILD_NUMBER}:\n${env.BUILD_URL}"
                sh "mvn package"  //integrate slack with jenkins

            }

        }

            
        stage('Test') {
            steps {
               junit '**/target/surefire-reports/TEST-*.xml' 
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts 'target/*.jar'
            }
        }

        stage('Publish-Artifact'){
            steps{
                s3Upload consoleLogLevel: 'INFO', dontSetBuildResultOnFailure: false, dontWaitForConcurrentBuildCompletion: false, entries: [[bucket: 'olulegends3', excludedFile: '', flatten: false, gzipFiles: false, keepForever: false, managedArtifacts: false, noUploadOnFailure: true, selectedRegion: 'us-east-2', showDirectlyInBrowser: false, sourceFile: 'target/*.jar', storageClass: 'STANDARD', uploadFromSlave: false, useServerSideEncryption: false]], pluginFailureResultConstraint: 'FAILURE', profileName: 'jenkinss3', userMetadata: []
            }
        }

       stage('Deploy-Dev'){
            steps{
                echo 'deploying application updates....'
                withCredentials([[
                      $class: 'AmazonWebServicesCredentialsBinding',
                      credentialsId: "jenkinss3",
                      accessKeyVariable: 'AKIA6MBLBASOM7TGMMWR',
                      secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]){

                          
                          sh "aws ec2 reboot-instances --instance-ids i-0e1862772a5afb538 ${params.devserver} --region us-east-2"

                      }


            }
        }

        
    }
    post{
        success{
           slackSend channel: 'jenkins', color: '439FE0', message: "SUCCESS ${currentBuild.fullDisplayName} at ${currentBuild.durationString[0..-13]}" 
        }
        failure{
            slackSend channel: 'jenkins', color: '#fc0303', message: "FAILURE ${currentBuild.fullDisplayName} at ${currentBuild.durationString[0..-13]}"
        }
    }
}
