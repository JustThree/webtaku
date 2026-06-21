pipeline {
    agent any

    environment {
        IMAGE_NAME    = 'webtaku'
        DEPLOY_DIR    = '/opt/webtaku'
        COMPOSE_FILE  = "${DEPLOY_DIR}/docker-compose.app.yml"
        HEALTH_PORT   = '8089'
    }

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Image') {
            steps {
                sh '''
                    docker build \
                      -t ${IMAGE_NAME}:${BUILD_NUMBER} \
                      -t ${IMAGE_NAME}:latest \
                      .
                '''
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    docker compose -f ${COMPOSE_FILE} up -d --remove-orphans
                '''
            }
        }

        stage('Health Check') {
            steps {
                sh '''#!/bin/bash
                    set +e   # curl 실패해도 다음 iteration로 진행
                    for i in $(seq 1 30); do
                        curl -fsS --connect-timeout 2 http://host.docker.internal:${HEALTH_PORT}/ > /dev/null 2>&1
                        status=$?
                        # 0 = 2xx/3xx, 22 = HTTP 4xx/5xx, 모두 "앱 응답함"
                        if [ $status -eq 0 ] || [ $status -eq 22 ]; then
                            echo "Service is up (curl exit=$status)"
                            exit 0
                        fi
                        echo "[$i/30] not ready (curl exit=$status), sleeping..."
                        sleep 2
                    done
                    echo "Health check timeout"
                    docker logs --tail 100 webtaku || true
                    exit 1
                '''
            }
        }
    }

    post {
        success { echo "Build #${env.BUILD_NUMBER} deployed" }
        failure { echo "Build #${env.BUILD_NUMBER} failed" }
    }
}
