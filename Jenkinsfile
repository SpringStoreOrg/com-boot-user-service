pipeline {
    agent any

    stages {
        stage('Maven') {
            steps {
                sh 'mvn package'
            }
        }
        stage('Docker') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'fractalwoodstories-docker-hub', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                    sh """
                        docker build . -t fractalwoodstories/user-service:arm64-latest
                        docker login -u ${USERNAME} -p ${PASSWORD}
                        docker push fractalwoodstories/user-service:arm64-latest
                    """
                }
            }
        }
    }
}