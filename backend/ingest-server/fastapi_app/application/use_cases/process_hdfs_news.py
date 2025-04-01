import asyncio
import hashlib
import logging
from datetime import datetime, timedelta
from fastapi_app.application.use_cases.fetch_news_from_hdfs import fetch_news_from_hdfs
from fastapi_app.application.use_cases.process_news import (
    process_news_data,
)  # 기존 전처리 로직 (동기 함수)
from fastapi_app.domain.models.news import News
from fastapi_app.domain.services.date_formatter import format_date
from fastapi_app.infrastructure.redis.redis_client import RedisClient

logger = logging.getLogger(__name__)


async def process_hdfs_news(source: str) -> list:
    """
    HDFS에 저장된 원시 뉴스를 읽어와, 중복 검사 및 “최근 30분 내” 게시된 기사만 전처리합니다.
    중복 기준:
      - domestic: SHA256 hash(title, pubDate, link)
      - worldwide: uuid
    Redis 집합: "processed_domestic" 또는 "processed_worldwide"

    :param source: "domestic" 또는 "worldwide"
    :return: 전처리 완료된 News 모델 리스트
    """
    redis_client = RedisClient()
    articles = fetch_news_from_hdfs(source)
    now = datetime.now()

    # 00:00 ~ 01:00 사이면 전날 뉴스도 추가로 불러옴
    if now.hour < 1:
        yesterday = now - timedelta(days=1)
        articles_yesterday = fetch_news_from_hdfs(source, date=yesterday)
        articles.extend(articles_yesterday)
        logger.info(
            "Including %d articles from yesterday for processing",
            len(articles_yesterday),
        )

    processed_articles = []
    threshold = now - timedelta(minutes=60)

    for article in articles:
        try:
            # 고유 키 생성
            if source.lower() == "domestic":
                title = article.get("title", "")
                pubDate = article.get("pubDate", article.get("published_at", ""))
                link = article.get("originallink", "")
                key_str = f"{title}-{pubDate}-{link}"
                unique_key = hashlib.sha256(key_str.encode("utf-8")).hexdigest()
                redis_set = "processed_domestic"
            elif source.lower() == "worldwide":
                unique_key = article.get("uuid", "")
                redis_set = "processed_worldwide"
            else:
                continue

            # published_at 처리 (문자열을 datetime으로 변환)
            published_at_str = article.get("published_at", article.get("pubDate", ""))
            formatted_date_str = format_date(published_at_str)

            published_at = datetime.fromisoformat(formatted_date_str)

            # 최근 30분 이내 기사만 처리
            if published_at < threshold:
                continue

            # Redis에서 중복 검사
            is_dup = await redis_client.is_duplicate(redis_set, unique_key)
            if is_dup:
                continue
            else:
                await redis_client.add_key(redis_set, unique_key)

            # News 모델 생성 (필요한 필드 매핑)
            news_obj = News(
                title=article.get("title", ""),
                description=article.get("description", article.get("snippet", "")),
                url=article.get("originallink", ""),
                published_at=formatted_date_str,
                image_url="",
                categories=[],
                keywords=[],
                sentiment=-1,
                raw_data_ref=article.get("id", ""),
            )

            # 제목이나 내용이 "?" 또는 공백만 있으면 건너뛰기
            if news_obj.title.strip() in {"", "?"} or news_obj.description.strip() in {
                "",
                "?",
            }:
                logger.info("Skipping news due to invalid title or content.")
                continue

            # 기존 전처리 로직을 동기 함수로 호출
            processed = process_news_data(news_obj)

            if processed is None:
                logger.info(
                    "Skipping news due to processing failure (e.g., empty keywords or unknown country)."
                )
                continue

            processed_articles.append(processed)
        except Exception as e:
            logger.error("Error processing article: %s", e)

    logger.info(
        "Processed %d articles from source '%s'", len(processed_articles), source
    )
    return processed_articles


if __name__ == "__main__":
    import asyncio
    import logging
    import time

    # 로깅 기본 설정
    logging.basicConfig(level=logging.INFO)

    # 테스트할 뉴스 소스 (예: "domestic" 또는 "worldwide")
    source = "domestic"

    # 시작 시간 기록
    start_time = time.time()

    # 비동기 함수 실행 및 결과 출력
    processed_articles = asyncio.run(process_hdfs_news(source))

    # 종료 시간 기록
    end_time = time.time()

    # 처리된 기사들을 출력
    for article in processed_articles:
        print(article)

    # 전체 실행 시간 계산 및 출력
    total_time = end_time - start_time
    print(f"Total execution time: {total_time:.2f} seconds")
