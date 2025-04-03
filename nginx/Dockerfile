# 1단계: React 애플리케이션 빌드
FROM node:22-alpine AS build

# 작업 디렉토리 설정
WORKDIR /app

# package.json과 yarn.lock 파일을 먼저 복사하여 의존성을 설치
COPY ../frontend/package.json ../frontend/yarn.lock ./

# yarn을 사용하여 의존성 설치
RUN yarn install

# 프론트엔드 전체 소스코드 복사
COPY ../frontend .

# React 빌드 (create-react-app의 경우 기본적으로 build 폴더에 결과물이 생성됨)
RUN yarn build

# NGINX 설정 파일 복사 (docker build context에 nginx.conf가 있어야 함)
COPY nginx.conf /etc/nginx/nginx.conf

# 정적 파일이 있다면 복사 (예: /usr/share/nginx/html)
COPY static/ /usr/share/nginx/html/static/

EXPOSE 80 443

CMD ["nginx", "-g", "daemon off;"]