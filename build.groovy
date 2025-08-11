timestamps {
    node {
        checkout scm

        def run = { cmd -> isUnix() ? sh(cmd) : bat(cmd) }
        def mvn = { args -> isUnix() ? "./mvnw ${args}" : "mvnw.cmd ${args}" }

        try {
            stage('Build & Unit Tests') {
                run(mvn('-B clean verify'))
            }
        } finally {
            junit 'target/surefire-reports/*.xml'
        }

        stage('Package') {
            run(mvn('-B -DskipTests package'))
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
    }
}

