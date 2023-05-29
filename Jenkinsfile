pipeline {
    agent any

    stages {
        stage('Set Chmod') {
            steps {
                sh 'chmod 777 /var/run/docker.sock'
                sh 'chmod +x ./gradlew'
            }
        }
        stage('Clean') {
            steps {
                sh './gradlew clean'
            }
        }
        stage('Build') {
            environment {
                QUAY_CREDS = credentials('Quay-Access')
            }
            steps {
                sh './gradlew build -Dquarkus.profile=kub -Dquarkus.container-image.username=$QUAY_CREDS_USR -Dquarkus.container-image.password=$QUAY_CREDS_PSW'
            }
        }
        stage('SonarQube analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh "./gradlew sonar"
                }
            }
        }
        stage('Owasp') {
            steps {
                sh './gradlew dependencyCheckAnalyze'
            }
        }
        stage('Checkout K8S manifest SCM') {
            steps {
                checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'Maurycy_ssh', url: 'git@github.com:Magisterka-Maurycy/GitOps.git']])
            }
        }
        stage('Deploy to gitops') {
            steps {
                sshagent(['Maurycy_ssh'])
                        {
                            sh '''
                                git remote get-url origin
                                cp ./build/kubernetes/kubernetes.yml ./kubernetes/mba/main.yaml
			            		git add ./kubernetes/
					            git config user.email "jenkins@example.com"
                                git config user.name "Jenkins"
                                git commit -m 'Jenkins Automatic Deployment - MBA'
					            git push origin HEAD:master
                            '''
                        }
            }
        }
    }
    post {
        success {
            jacoco(
                    execPattern: '**/build/*.exec',
                    classPattern: '**/build/classes/kotlin/main',
                    sourcePattern: '**/src/main'
            )
            publishHTML (target : [allowMissing: false,
                                   alwaysLinkToLastBuild: true,
                                   keepAll: true,
                                   reportDir: 'reports',
                                   reportFiles: 'dependency-check-report.html',
                                   reportName: 'OWASP Dependency Check',
                                   reportTitles: 'OWASP Dependency Check']
            )
        }
    }
}