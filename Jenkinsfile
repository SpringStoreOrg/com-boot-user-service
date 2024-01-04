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
                sh '''
                    docker build . -t fractalwoodstories/user-service:arm64-latest
                    docker push fractalwoodstories/user-service:arm64-latest
                '''

            }
        }
    }
}