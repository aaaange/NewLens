import asyncio
import logging

from apscheduler.schedulers.background import BackgroundScheduler

from fastapi_app.application.use_cases.ingest_news_hdfs import (
    ingest_naver_news_to_hdfs,
)

logger = logging.getLogger(__name__)
scheduler = BackgroundScheduler()


def scheduled_ingestion():
    try:
        logger.info("Starting scheduled ingestion tasks")
        # 새로운 이벤트 루프를 생성하여 두 비동기 작업을 동시에 실행
        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)
        # tasks = [ingest_naver_news_to_hdfs(), ingest_thenewsapi_to_hdfs()]
        tasks = [ingest_naver_news_to_hdfs()]
        loop.run_until_complete(asyncio.gather(*tasks))
        loop.close()
        logger.info("Completed scheduled ingestion tasks")
    except Exception as e:
        logger.error("Error during scheduled ingestion: %s", e)


def start_scheduler():
    # 10분마다 스케줄 실행 (네이버와 thenewsapi 모두 10분 주기로 조회)
    scheduler.add_job(scheduled_ingestion, "interval", minutes=10)
    scheduler.start()
    logger.info("Ingestion scheduler started")


def shutdown_scheduler():
    scheduler.shutdown()
    logger.info("Ingestion scheduler shutdown")
