## 1. 사용한 환경 개요

### (1) 사용한 기술 스택

- **JVM**: OpenJDK 17
- **웹서버**: Nginx
- **WAS**: Spring Boot (Gradle)
- **DB**: MySQL 8
- **NoSQL**: MongoDB
- **Cache**: Redis
- **메시지 브로커**: Kafka
- **CI/CD**: Jenkins (Docker 기반 Pipeline 사용)
- **배포 환경**: AWS EC2 (2대)
- **버전 관리**: GitLab
- **데이터 처리**: Hadoop, Spark

### (2) 포트 설정

- **Nginx**: 80, 443  
- **Spring Gateway**: 8080  
- **user-server**: 9001  
- **search-server**: 9002  
- **statics-server**: 9003  
- **MySQL**: 3306  
- **Redis**: 6379  
- **Kafka**: 9092  
- **Jenkins**: 9090  
- **MongoDB**: 27017  
- **Hadoop NameNode**: 9870  
- **Hadoop DataNode**: 9864~9869

---

## 2. 빌드 및 배포 상세 정보

### (1) 프로젝트 구조

/project-root <br>
├── backend │ <br> ---
├── spring-gateway │ <br>--- ├── search-server │ <br>--- ├── user-server │ <br>--- └── statics-server <br> ├── frontend  <br>├── nginx <br>
 ├── deployment │ <br>--- ├── docker-compose-springboot-mongodb.yml │  <br> ---└── docker-compose-hadoop.yml


### (2) Docker Compose 설정

#### spring-gateway / user-server / search-server / statics-server

- Gradle `bootJar` 빌드 후 Docker 이미지 생성
- MongoDB, Redis, Kafka, MySQL 등 서비스에 `depends_on` 설정
- `.env` 환경변수 로딩
- `wait-for-it.sh`를 사용해 의존 서비스 준비 완료 후 실행
- Kafka 메시지 크기 설정 (`10MB`) 적용

#### nginx

- `nginx.conf` 사용
- letsencrypt 경로 마운트로 HTTPS 적용
- gateway 이후 실행되도록 구성

#### Hadoop (ingest-server + HDFS)

- ingest-server는 Spark/HDFS 기반 뉴스 수집 파이프라인 담당
- NameNode, DataNode1, DataNode2 설정
- hostname 기반 통신 설정 (`dfs_datanode_hostname` 직접 지정)
- 각 포트를 고정 매핑

### (3) Jenkins Pipeline (CI/CD)

```groovy
pipeline {
    agent any
    environment {
        DOCKER_REGISTRY = 'kimsangouk'
    }
    stages {
        stage('Checkout') {
            steps {
                git credentialsId: 'gitlab', branch: 'master', url: 'https://lab.ssafy.com/s12-bigdata-dist-sub1/S12P21A508.git'
            }
        }
        stage('Inject frontend .env') {
            steps {
                sh 'cp /home/ubuntu/frontend.env frontend/.env'
            }
        }
        stage('Build Docker Images') {
            steps {
                sh 'docker build -t ${DOCKER_REGISTRY}/spring-gateway:latest backend/spring-gateway'
                sh 'docker build -t ${DOCKER_REGISTRY}/search-server:latest backend/search-server'
                sh 'docker build -t ${DOCKER_REGISTRY}/user-server:latest backend/user-server'
                sh 'docker build -t ${DOCKER_REGISTRY}/statics-server:latest backend/statics-server'
                sh 'docker build -f nginx/Dockerfile -t ${DOCKER_REGISTRY}/nginx:latest .'
            }
        }
        stage('Login to DockerHub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                }
            }
        }
        stage('Push Docker Images') {
            steps {
                sh 'docker push ${DOCKER_REGISTRY}/spring-gateway:latest'
                sh 'docker push ${DOCKER_REGISTRY}/search-server:latest'
                sh 'docker push ${DOCKER_REGISTRY}/user-server:latest'
                sh 'docker push ${DOCKER_REGISTRY}/statics-server:latest'
                sh 'docker push ${DOCKER_REGISTRY}/nginx:latest'
            }
        }
        stage('Deploy to EC2') {
            steps {
                sshagent(['ec2_p_ssafy_io']) {
                    sh '''
                    scp -o StrictHostKeyChecking=no deployment/docker-compose-springboot-mongodb.yml ubuntu@j12a508.p.ssafy.io:/home/ubuntu/docker-compose-springboot-mongodb.yml

                    ssh -o StrictHostKeyChecking=no ubuntu@j12a508.p.ssafy.io "cd /home/ubuntu && \
                    docker-compose -f docker-compose-springboot-mongodb.yml pull && \
                    docker-compose -f docker-compose-springboot-mongodb.yml up -d nginx user-server search-server statics-server"
                    '''
                }
            }
        }
    }
}
