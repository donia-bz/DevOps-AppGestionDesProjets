pipeline {
    agent any

    environment {
        DOCKERHUB = credentials('dockerhub-creds')
        DOCKER_USER = "${DOCKERHUB_USR}"
    }

    stages {

        stage('1 - Git') {
            steps {
                checkout scm
            }
        }

        stage('2 - Maven Compile') {
            steps {
                dir('backend') {
                    sh 'mvn -B clean compile'
                }
            }
        }

        stage('3 - SonarQube') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    dir('backend') {
                        sh 'mvn -B org.sonarsource.scanner.maven:sonar-maven-plugin:5.8.0.7211:sonar -Dsonar.projectKey=gestion-projets -Dsonar.projectName=GestionProjets -Dsonar.host.url=$SONAR_HOST_URL -Dsonar.login=$SONAR_AUTH_TOKEN'
                    }
                }
            }
        }

        stage('4 - Maven Test') {
            steps {
                dir('backend') {
                    sh 'mvn -B test'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'backend/target/surefire-reports/*.xml'
                }
            }
        }

        stage('5 - Maven Package') {
            steps {
                dir('backend') {
                    sh 'mvn -B package -DskipTests'
                }
                archiveArtifacts artifacts: 'backend/target/*.jar', fingerprint: true
            }
        }

        stage('6 - Maven Deploy') {
            steps {
                dir('backend') {
                    sh 'mvn -B deploy -DskipTests -DaltDeploymentRepository=local-repo::file:///var/lib/jenkins/maven-repo'
                }
            }
        }

        stage('7 - Docker Image + Push') {
            steps {
                sh '''
                    echo "$DOCKERHUB_PSW" | docker login -u "$DOCKERHUB_USR" --password-stdin
                    docker build -t $DOCKERHUB_USR/gestion-projets-backend:latest -t $DOCKERHUB_USR/gestion-projets-backend:$BUILD_NUMBER backend
                    docker build -t $DOCKERHUB_USR/gestion-projets-frontend:latest -t $DOCKERHUB_USR/gestion-projets-frontend:$BUILD_NUMBER frontend
                    docker push $DOCKERHUB_USR/gestion-projets-backend:latest
                    docker push $DOCKERHUB_USR/gestion-projets-backend:$BUILD_NUMBER
                    docker push $DOCKERHUB_USR/gestion-projets-frontend:latest
                    docker push $DOCKERHUB_USR/gestion-projets-frontend:$BUILD_NUMBER
                '''
            }
        }

        stage('8 - Docker Compose Up') {
            steps {
                sh '''
                    docker compose up -d
                    for i in $(seq 1 18); do
                        curl -fs http://localhost:8085/entreprise/all && echo " <- backend OK" && break
                        echo "Attente du backend... ($i)"
                        sleep 10
                    done
                    docker compose ps
                '''
            }
        }
    }
}
