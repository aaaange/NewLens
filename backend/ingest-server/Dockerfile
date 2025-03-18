# 베이스 이미지로 Python 3.9 슬림 버전을 사용합니다.
FROM python:3.9-slim

# 작업 디렉터리 설정
WORKDIR /app

# 시스템 패키지 설치 (필요한 경우 추가 패키지를 설치)
RUN apt-get update && apt-get install -y build-essential

# 의존성 파일 복사 및 설치
COPY requirements.txt .
RUN pip install --upgrade pip && \
    pip install --no-cache-dir -r requirements.txt

# 애플리케이션 소스 코드 전체 복사 (.venv 등은 보통 .gitignore 처리)
COPY . .

# FastAPI가 사용할 포트 공개
EXPOSE 8000

# uvicorn 실행 (fastapi_app/main.py의 app 객체가 진입점임)
CMD ["uvicorn", "fastapi_app.main:app", "--host", "0.0.0.0", "--port", "8000"]