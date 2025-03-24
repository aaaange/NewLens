from pydantic import BaseSettings


class Settings(BaseSettings):
    app_name: str = "Ingest Server - FastAPI News Pipeline"
    # mongodb_host: str = "localhost"
    # mongodb_port: int = 27017
    # elasticsearch_host: str = "localhost"
    # elasticsearch_port: int = 9200
    hdfs_host: str = "localhost"
    hdfs_port: int = 9870
    thenewsapi_key: str = "your_api_key"
    naver_newsapi_client_id: str = "your_client_id"
    naver_newsapi_client_secret: str = "your_client_secret"
    redis_host: str = "localhost"
    redis_port: int = 6379

    class Config:
        env_file = ".env"


settings = Settings()
