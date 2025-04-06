import hashlib
import logging
import asyncio
from datetime import datetime

from fastapi_app.infrastructure.external.news_providers.naver_newsapi_client import (
    NaverNewsAPIClient,
)
from fastapi_app.infrastructure.external.news_providers.thenewsapi_client import (
    TheNewsAPIClient,
)
from fastapi_app.infrastructure.hdfs.hdfs_client import HDFSClient
from fastapi_app.infrastructure.redis.redis_client import RedisClient

logger = logging.getLogger(__name__)


async def ingest_naver_news_to_hdfs():
    """
    - 네이버 뉴스 API를 사용하여 최신 뉴스를 100개씩 조회 (start=1,2)
    - '다'를 query로 사용하여 모든 뉴스를 조회
    - 기사별로 (title, pubDate, link) 조합을 해시하여 중복 여부를 Redis로 검사
    - 중복되지 않은 기사만 HDFS에 /data/raw/news/domestic/년/월/일 경로에 저장
    """
    naver_client = NaverNewsAPIClient()
    hdfs_client = HDFSClient()
    redis_client = RedisClient()
    articles_to_store = []

    for start in [1, 2]:
        try:
            response = naver_client.fetch_news(
                query="다", start=start, display=100, sort="date"
            )
            # 네이버 응답 구조는 예: {"items": [...], "lastBuildDate": "..."}
            articles = response.get("items", [])
            for article in articles:
                title = article.get("title", "")
                pubDate = article.get("pubDate", "")
                link = article.get("originallink", "")
                # 중복검사용 해시 생성 (title + pubDate + link)
                key_str = f"{title}-{pubDate}-{link}"
                hash_key = hashlib.sha256(key_str.encode("utf-8")).hexdigest()

                if await redis_client.is_duplicate("naver_duplicates", hash_key):
                    continue  # 중복이면 건너뜀
                else:
                    await redis_client.add_key("naver_duplicates", hash_key)
                    article["id"] = hash_key
                    articles_to_store.append(article)
        except Exception as e:
            logger.error("Error ingesting naver news for start=%s: %s", start, e)

    if articles_to_store:
        try:
            # 저장 기본 경로: /data/raw/news/domestic
            await asyncio.to_thread(
                hdfs_client.append_news_articles,
                articles_to_store,
                base_dir="/data/raw/news/domestic",
            )
            logger.info(
                "Ingested %d naver news articles to HDFS", len(articles_to_store)
            )
        except Exception as e:
            logger.error("Error writing naver news to HDFS: %s", e)


async def ingest_thenewsapi_to_hdfs():
    """
    - thenewsapi에서 오늘 날짜에 해당하는 뉴스를 조회 (page 1~15, limit=100)
    - 각 페이지마다 응답받은 기사에서 uuid 값을 기준으로 중복 여부를 Redis로 검사
    - 중복되지 않은 기사만 HDFS에 /data/raw/news/worldwide/년/월/일 경로에 저장
    """
    thenews_client = TheNewsAPIClient()
    hdfs_client = HDFSClient()
    redis_client = RedisClient()
    articles_to_store = []

    today_str = datetime.now().strftime("%Y-%m-%d")
    for page in range(1, 16):
        try:
            response = thenews_client.fetch_news(
                page=page, published_on=today_str, limit=100
            )
            # thenewsapi 응답 구조는 예: {"data": [...], "meta": {...}}
            articles = response.get("data", [])
            for article in articles:
                uuid_val = article.get("uuid", "")
                if not uuid_val:
                    continue
                if await redis_client.is_duplicate("thenewsapi_duplicates", uuid_val):
                    continue
                else:
                    await redis_client.add_key("thenewsapi_duplicates", uuid_val)
                    articles_to_store.append(article)
        except Exception as e:
            logger.error("Error ingesting thenewsapi news for page %s: %s", page, e)

    if articles_to_store:
        try:
            # 저장 기본 경로: /data/raw/news/worldwide
            await asyncio.to_thread(
                hdfs_client.append_news_articles,
                articles_to_store,
                base_dir="/data/raw/news/worldwide",
            )
            logger.info(
                "Ingested %d thenewsapi news articles to HDFS", len(articles_to_store)
            )
        except Exception as e:
            logger.error("Error writing thenewsapi news to HDFS: %s", e)


if __name__ == "__main__":
    import asyncio

    asyncio.run(ingest_naver_news_to_hdfs())
