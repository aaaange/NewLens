# ingest-server/fastapi_app/application/use_cases/store_news.py

from fastapi_app.domain.models.news import News
from fastapi_app.infrastructure.storage.mongodb.mongodb_client import MongoDBClient
from fastapi_app.infrastructure.storage.elasticsearch.elasticsearch_client import (
    ESClient,
)
import logging

logger = logging.getLogger(__name__)


def store_news_to_databases(news_list: list[News]):
    """
    뉴스 리스트를 MongoDB와 Elasticsearch에 저장합니다.
    - MongoDB에는 id, title, url, published_at, image_url, categories, keywords, sentiment, raw_data_ref
      를 저장합니다.
    - Elasticsearch에는 id, published_at, categories, keywords, sentiment 만 저장합니다.
    테이블/인덱스명은 모두 "domestic_news"로 사용합니다.
    """
    # MongoDB 저장
    mongo_client = MongoDBClient()
    collection = mongo_client.get_collection("domestic_news")
    mongo_docs = []
    for news in news_list:
        doc = {
            "id": news.raw_data_ref,  # raw_data_ref에 uuid가 저장되어 있음
            "title": news.title,
            "description": news.description,
            "url": news.url,
            "published_at": news.published_at,
            "image_url": news.image_url,
            "categories": news.categories,
            "keywords": news.keywords,
            "sentiment": news.sentiment,
            "raw_data_ref": news.raw_data_ref,
        }
        mongo_docs.append(doc)
    if mongo_docs:
        result = collection.insert_many(mongo_docs)
        logger.info("Inserted %d documents into MongoDB", len(result.inserted_ids))

    # Elasticsearch 저장
    es_client = ESClient()
    for news in news_list:
        doc = {
            "published_at": news.published_at,
            "categories": news.categories,
            "keywords": news.keywords,
            "sentiment": news.sentiment,
        }
        res = es_client.index_document("domestic_news", news.raw_data_ref, doc)
        logger.info(
            "Indexed document %s into Elasticsearch: %s", news.raw_data_ref, res
        )
