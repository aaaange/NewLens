from fastapi import FastAPI
from fastapi_app.interfaces.http import news_controller
from fastapi_app.interfaces.scheduler import ingestion_schedular, processing_scheduler

app = FastAPI(title="Ingest Server - FastAPI News Pipeline")


# 애플리케이션 시작 시 스케줄러 시작
@app.on_event("startup")
async def startup_event():
    ingestion_schedular.start_scheduler()
    processing_scheduler.start_processing_scheduler()  # 30분 주기로 HDFS 원시 데이터 전처리


# 애플리케이션 종료 시 스케줄러 종료
@app.on_event("shutdown")
async def shutdown_event():
    ingestion_schedular.shutdown_scheduler()
    processing_scheduler.shutdown_processing_scheduler()


if __name__ == "__main__":
    import uvicorn

    uvicorn.run("fastapi_app.main:app", host="0.0.0.0", port=8000, reload=True)
