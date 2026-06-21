pipeline {
    agent any

    tools {
        jdk 'jdk17'
        nodejs 'node18'
    }

    environment {
        BACKEND_DIR  = 'justthree-review-my-webtoon-back'
        FRONTEND_DIR = 'justthree-review-my-webtoon-front'
        DEPLOY_DIR   = '/opt/webtaku'
        SERVICE_NAME = 'webtaku'
        HEALTH_URL   = 'http://127.0.0.1:8089/actuator/health'
    }

    options {
        timestamps()
        timeout(time: 20, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Frontend Build') {
            steps {
                dir("${FRONTEND_DIR}") {
                    sh 'npm ci'
                    sh 'npm run build'
                }
            }
        }

        stage('Backend Build') {
            steps {
                dir("${BACKEND_DIR}") {
                    sh 'chmod +x gradlew'
                    sh './gradlew clean bootJar -x test'
                }
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    sudo install -d -m 755 ${DEPLOY_DIR}
                    sudo cp ${BACKEND_DIR}/build/libs/*.jar ${DEPLOY_DIR}/app.jar
                    sudo systemctl restart ${SERVICE_NAME}
                '''
            }
        }

        stage('Health Check') {
            steps {
                sh '''
                    for i in $(seq 1 30); do
                        if curl -fsS ${HEALTH_URL} >/dev/null; then
                            echo "Service is up"
                            exit 0
                        fi
                        sleep 2
                    done
                    echo "Health check failed"
                    sudo journalctl -u ${SERVICE_NAME} -n 100 --no-pager || true
                    exit 1
                '''
            }
        }
    }

    post {
        success { echo "Deployed build #${env.BUILD_NUMBER}" }
        failure { echo "Build #${env.BUILD_NUMBER} failed" }
    }
}
