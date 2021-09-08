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
                s3Upload consoleLogLevel: 'INFO', dontSetBuildResultOnFailure: false, dontWaitForConcurrentBuildCompletion: false, entries: [[bucket: 'petclinicayo', excludedFile: '', flatten: false, gzipFiles: false, keepForever: false, managedArtifacts: false, noUploadOnFailure: true, selectedRegion: 'us-iso-east-1', showDirectlyInBrowser: false, sourceFile: 'target/*.jar', storageClass: 'STANDARD', uploadFromSlave: false, useServerSideEncryption: false]], pluginFailureResultConstraint: 'FAILURE', profileName: 'Jenkins', userMetadata: []            
            }
        }
        // stage('Deploy'){
        //     steps {

        //     }
        // }
    }
}