import asyncio
import logging
from apscheduler.schedulers.background import BackgroundScheduler
from fastapi_app.application.use_cases.process_hdfs_news import process_hdfs_news

logger = logging.getLogger(__name__)
scheduler = BackgroundScheduler()


def scheduled_processing():
    try:
        logger.info("Starting scheduled processing task")
        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)
        tasks = [process_hdfs_news("domestic"), process_hdfs_news("worldwide")]
        results = loop.run_until_complete(asyncio.gather(*tasks))
        loop.close()
        domestic_processed, worldwide_processed = results
        logger.info(
            "Processed %d domestic and %d worldwide articles",
            len(domestic_processed),
            len(worldwide_processed),
        )
        # 여기서 후속 저장(DB/Elasticsearch)로 결과를 전달하는 로직을 추가할 수 있음
    except Exception as e:
        logger.error("Error during scheduled processing: %s", e)


def start_processing_scheduler():
    # 전처리 작업은 30분 주기로 실행
    scheduler.add_job(scheduled_processing, "interval", minutes=30)
    scheduler.start()
    logger.info("Processing scheduler started")


def shutdown_processing_scheduler():
    scheduler.shutdown()
    logger.info("Processing scheduler shutdown")
