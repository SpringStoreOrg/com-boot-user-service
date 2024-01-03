pipeline {
    agent any

    tools {
        jdk 'microsoft-jdk-11.0.21'
        maven 'maven-3.9.6'
    }

    stages {
        stage('java check') {
            steps {
                sh 'java -version'
            }
        }
        stage('Maven') {
            steps {
                sh 'mvn package'
            }
        }
        stage('Tests') {
            steps {
                echo 'TODO'
            }
        }
        stage('Docker build') {
            steps {
                echo 'TODO'
            }
        }
        stage('Docker push') {
            steps {
                echo 'TODO'
            }
        }
    }
}