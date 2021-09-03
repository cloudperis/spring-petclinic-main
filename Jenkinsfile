pipeline {
    agent any
    stages {
        stage('Source') {
            //cloning my repository
            sh "git clone https://github.com/cloudperis/spring-petclinic-main.git"
        
            
        }
        stage ('Build') {
            echo "building our code ...."
            
        }

        stage ('Test'){
            echo "testing ...."
        }

        stage ('Deploy'){
            echo "Deploying the code....."
        }
 
    }
}