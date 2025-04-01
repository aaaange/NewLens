from elasticsearch import Elasticsearch
import os
from dotenv import load_dotenv

load_dotenv()


class ESClient:
    def __init__(self):
        # 환경변수에서 Elasticsearch 연결정보를 읽습니다.
        es_host = os.getenv("ES_HOST", "localhost")
        es_port = os.getenv("ES_PORT", "9200")
        self.client = Elasticsearch(
            [{"host": es_host, "port": int(es_port), "scheme": "http"}]
        )

    def index_document(self, index_name: str, id: str, document: dict):
        return self.client.index(index=index_name, id=id, body=document)


# 테스트용 코드
if __name__ == "__main__":
    es_client = ESClient()
    doc = {
        "published_at": "2025-03-24T23:39:37",
        "categories": ["news"],
        "keywords": ["test"],
        "sentiment": 1,
    }
    res = es_client.index_document("domestic_news", "test_id", doc)
    print("Indexing result:", res)
