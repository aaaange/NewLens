from pymongo import MongoClient
import os
from dotenv import load_dotenv

load_dotenv()


class MongoDBClient:
    def __init__(self):
        # 환경변수에서 MongoDB 연결정보를 읽습니다.
        mongo_uri = os.getenv("MONGO_URI", "mongodb://localhost:27017")
        self.client = MongoClient(mongo_uri)
        # 사용할 데이터베이스 이름 (예: "news_db")
        self.db = self.client[os.getenv("MONGO_DB", "news_db")]

    def get_collection(self, collection_name: str):
        return self.db[collection_name]


# 테스트용 코드
if __name__ == "__main__":
    db_client = MongoDBClient()
    collection = db_client.get_collection("domestic_news")
    print("현재 문서 수:", collection.count_documents({}))
