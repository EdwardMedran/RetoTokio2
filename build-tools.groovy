// Scripted Pipeline usando JDK/Maven definidos en Jenkins Tools
timestamps {
    node {
        checkout scm

        // Configura JAVA_HOME y MAVEN en PATH desde Tools
        env.JAVA_HOME = tool name: 'jdk17', type: 'jdk'
        def mvnHome   = tool name: 'maven3', type: 'maven'
        env.PATH = "${env.JAVA_HOME}/bin:${mvnHome}/bin:${env.PATH}"

        try {
            stage('Build & Unit Tests') {
                if (isUnix()) {
                    sh 'mvn -B clean verify'
                } else {
                    bat 'mvn -B clean verify'
                }
            }
        } finally {
            junit 'target/surefire-reports/*.xml'
        }

        stage('Package') {
            if (isUnix()) {
                sh 'mvn -B -DskipTests package'
            } else {
                bat 'mvn -B -DskipTests package'
            }
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
    }
}
