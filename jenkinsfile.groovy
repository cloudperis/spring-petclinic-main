pipeline {
    agent any
    
    tools {
        maven 'mvn'
        
    }



    stages {
        
        stage('Build') {
            steps{
                slackSend channel: 'cosmas-team', color: '#2211d9', message: "STARTED ${env.JOB_NAME} at #${env.BUILD_NUMBER}:\n${env.BUILD_URL}"
                sh "mvn package"

            }

        }

            
        stage('Test') {
            steps {
               junit '**/target/surefire-reports/TEST-*.xml'
               sh 'cd target & cd surefire-reports & ls ' 
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts 'target/*.jar'
            }
        }

        stage('Publish-Artifact'){
            steps{
                s3Upload consoleLogLevel: 'INFO', dontSetBuildResultOnFailure: false, dontWaitForConcurrentBuildCompletion: false, entries: [[bucket: 'jenkinsproject', excludedFile: '', flatten: false, gzipFiles: false, keepForever: false, managedArtifacts: false, noUploadOnFailure: true, selectedRegion: 'us-east-1', showDirectlyInBrowser: false, sourceFile: 'target/*.jar', storageClass: 'STANDARD', uploadFromSlave: false, useServerSideEncryption: false]], pluginFailureResultConstraint: 'FAILURE', profileName: 'jenkins-s3upload', userMetadata: []
            }
        }

       stage('Deploy-Dev'){
            steps{
                echo 'deploying application updates....'
                withCredentials([[
                      $class: 'AmazonWebServicesCredentialsBinding',
                      credentialsId: "jenkins-ec2-deploy",
                      accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                      secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {

                          
                          sh "aws ec2 reboot-instances --instance-ids i-02b1d1c1e3da99cc7 --region us-east-1"

                      }


            }
        }

        
    }
    post{
        success{
           slackSend channel: 'cosmas-team', color: '439FE0', message: "SUCCESS ${currentBuild.fullDisplayName} at ${currentBuild.durationString[0..-13]}" 
        }
        failure{
            slackSend channel: 'cosmas-team', color: '#fc0303', message: "FAILURE ${currentBuild.fullDisplayName} at ${currentBuild.durationString[0..-13]}"
        }
    }
}
