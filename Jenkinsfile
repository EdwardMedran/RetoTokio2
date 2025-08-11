pipeline {
    agent any
    tools {
        jdk 'jdk17'          // Configurado en Manage Jenkins > Tools
        maven 'maven-3.9'    // Idem
    }
    stages {
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Build & Unit Tests') {
            steps {
                bat 'mvn -B -DskipTests=false clean verify'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                bat 'mvn -B -DskipTests package'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }