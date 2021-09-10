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
                sh 'git clone https://github.com/cloudperis/spring-petclinic-main.git'
                sh 'ls'

                
                
         
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


       stage('Deploy'){
            steps{
                echo 'deploying application updates....'
                
                          
                          
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
