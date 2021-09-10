pipeline {
    agent any
    
    tools {
        maven 'mvn'
        
    }



    stages {
        
        stage('Build') {
            steps{
                slackSend channel: 'jenkins-notifications', message: "STARTED ${env.JOB_NAME} at ${BUILD_TIMESTAMP}"
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
                s3Upload consoleLogLevel: 'INFO', dontSetBuildResultOnFailure: false, dontWaitForConcurrentBuildCompletion: false, pluginFailureResultConstraint: 'FAILURE', profileName: 'sai-cloudperi', userMetadata: []
            }
        }

       //stage('Deploy-Dev'){
            //steps{
                //echo 'deploying application updates....'
                //withCredentials([[
                      //$class: 'AmazonWebServicesCredentialsBinding',
                      //credentialsId: "Jenkins-aws",
                      //accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                      //secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {

                          
                        //  sh "aws ec2 reboot-instances --instance-ids ${params.devserver} --region us-east-1"

                      //}


            //}
        //}

        
    }//
    //post{
        //success{
           //slackSend channel: 'jenkins-notifications', message: "SUCCESS ${env.JOB_NAME} at ${BUILD_TIMESTAMP}" 
        //}
        //failure{
            //slackSend channel: 'jenkins-notifications', message: "FAILURE ${env.JOB_NAME} at ${BUILD_TIMESTAMP}"
        //}
    //}
}