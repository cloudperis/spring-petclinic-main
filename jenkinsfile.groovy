pipeline {
    agent any

    tools {
        maven 'mvn'
        
    }
    
    stages {
        stage('Build') {
            steps {
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

        stage('Publish-Artifact') {
            steps {
                s3Upload consoleLogLevel: 'INFO', dontSetBuildResultOnFailure: false, dontWaitForConcurrentBuildCompletion: false, entries: [[bucket: 'sai-cloudperi', excludedFile: '', flatten: false, gzipFiles: false, keepForever: false, managedArtifacts: false, noUploadOnFailure: true, selectedRegion: 'us-east-1', showDirectlyInBrowser: false, sourceFile: 'target/*.jar', storageClass: 'STANDARD', uploadFromSlave: false, useServerSideEncryption: false]], pluginFailureResultConstraint: 'FAILURE', profileName: 'sai-cloudperi', userMetadata: []
            }
        }

        stage('Deploy-Dev'){
            steps{
                echo 'deploying application updates....'
                withCredentials([[
                      $class: 'AmazonWebServicesCredentialsBinding',
                      credentialsId: "sai-jenkins",
                      accessKeyVariable: 'AKIAX7HQ6UVS5SNMNEH3',
                      secretKeyVariable: '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']]) {

                          
                          sh "aws ec2 reboot-instances --instance-ids ${params.devserver} --region us-east-1"

                      }


            }
        }

        
    }
    //post{
        //success{
           //slackSend channel: 'jenkins-notifications', message: "SUCCESS ${env.JOB_NAME} at ${BUILD_TIMESTAMP}" 
        //}
        //failure{
            //slackSend channel: 'jenkins-notifications', message: "FAILURE ${env.JOB_NAME} at ${BUILD_TIMESTAMP}"
        //}
    //}
}