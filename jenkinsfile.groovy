pipeline {
    agent any
    
    tools {
        maven 'mvn'
        
    }



    stages {
        
        stage('Build 41') {
            steps{
                slackSend channel: 'jenkins', color: '#2211d9', message: "STARTED ${env.JOB_NAME} at #${env.BUILD_NUMBER}:\n${env.BUILD_URL}"
                sh "mvn package"

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

       stage('Deploy-Dev1'){
            steps{
                echo 'deploying application updates....'
                withCredentials([[
                      $class: 'AmazonWebServicesCredentialsBinding',
                      credentialsId: "jenkinss3",
                      accessKeyVariable: 'AKIA6MBLBASOM7TGMMWR',
                      secretKeyVariable: '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']]) {

                          
                          sh "aws ec2 reboot-instances --instance-ids i-09759a80862886d80 --region us-east-1"

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
