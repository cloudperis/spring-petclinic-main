pipeline {
    agent any
    
    tools {
        maven 'mvn'
        
    }



    stages {
        
        stage('check distro') {
            steps{
                
                sh "java -version"

            }


        }
        stage('Source') {
            steps {
                // Get some codes from github repository
                git 'https://github.com/cloudperis/spring-petclinic-main.git'
                

                
                
         
            }
        }   
        stage('Build') {
            steps{
                sh "mvn package"
            }

        }  

                //Run maven on unix agent

                // To run maven on a windows agent, use
                // bat "mvn -Dmaven.test.failure.ignore=true clean package"
        

            
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
                s3Upload consoleLogLevel: 'INFO', dontSetBuildResultOnFailure: false, dontWaitForConcurrentBuildCompletion: false, entries: [[bucket: 'app-sanmi-pet-clinic', excludedFile: '', flatten: false, gzipFiles: false, keepForever: false, managedArtifacts: false, noUploadOnFailure: true, selectedRegion: 'us-east-2', showDirectlyInBrowser: false, sourceFile: 'target/*.jar', storageClass: 'STANDARD', uploadFromSlave: false, useServerSideEncryption: false]], pluginFailureResultConstraint: 'FAILURE', profileName: 'Jenkins_sanmi', userMetadata: []

            }
            
        }


       stage('Deploy'){
            steps{
                echo 'deploying application updates....'
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: "Sanmi-AWS Credentials",
                    accesskeyVariable: 'AWS_ACESS_KEY_ID',
                    secretkeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {

                        sh "aws ec2 reboot-instances --instance-ids ${params.sevserver} --region us-east-2"
                    }
                
                
                          
                          
                }


            }
        }

        
    }
        //post {
            // If maven was able to run the test, even if some of the test
            // failed, record the test result and archive the jar file
            //success {
                //junit '**/target/surefire-report/TEST-*.xml'
                //archiveArtifacts 'target/*.jar'
            // }
        //}
