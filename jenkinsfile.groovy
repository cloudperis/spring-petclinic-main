pipeline{
    agent any

    tools {
        maven 'mvn'
        
    }

    stages{
        stage('Build'){
            steps {
                sh "mvn package"
            }
        }
        stage('Test'){
            steps {
                junit '**/target/surefire-reports/TEST-*.xml'
            }
        }
        stage('Archive'){
            steps {
                archiveArtifacts 'target/*.jar'
            }
        }
        stage('Publish-Artifact'){
            steps {
                    s3Upload consoleLogLevel: 'INFO', dontSetBuildResultOnFailure: false, dontWaitForConcurrentBuildCompletion: false, entries: [[bucket: 'petclinicayo', excludedFile: '', flatten: false, gzipFiles: false, keepForever: false, managedArtifacts: false, noUploadOnFailure: true, selectedRegion: 'us-east-1', showDirectlyInBrowser: false, sourceFile: 'target/*.jar', storageClass: 'STANDARD', uploadFromSlave: false, useServerSideEncryption: false]], pluginFailureResultConstraint: 'FAILURE', profileName: 'JENKINS-AYO', userMetadata: []        
                }
                }
                
        // stage('Deploy'){
        //     steps { 
        //         echo 'deploying application updates....'
        //         withCredentials([[
        //               $class: 'AmazonWebServicesCredentialsBinding',
        //               credentialsId: "Jenkins",
        //               accessKeyVariable: 'AWS_ACCESS_KEY_ID',
        //               secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) 
        //                 {
        //                   sh "aws ec2 reboot-instances --instance-ids ${params.devserver} --region us-east-1"
        //                 }
        //     }
        // }
    }
}