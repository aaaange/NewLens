# import json
# from hdfs import InsecureClient
# from pymongo import MongoClient
# from elasticsearch import Elasticsearch
#
# # 📌 **HDFS 설정**
# HDFS_URL = "http://localhost:9870"  # HDFS NameNode 주소
# HDFS_PATH = "/data/raw/news/2025/03/news_20250306.json"  # HDFS에 저장된 파일 경로
#
# # HDFS 클라이언트 설정
# hdfs_client = InsecureClient(HDFS_URL, user="root")
#
# # 📌 **MongoDB 설정**
# MONGO_URI = "mongodb://localhost:27017/"  # MongoDB 주소
# mongo_client = MongoClient(MONGO_URI)
# mongo_db = mongo_client["news_db"]  # 사용할 데이터베이스 이름
# mongo_collection = mongo_db["news_articles"]  # 저장할 컬렉션 이름
#
# # 📌 **Elasticsearch 설정**
# ELASTICSEARCH_URL = "http://localhost:9200"  # Elasticsearch 주소
# es = Elasticsearch([ELASTICSEARCH_URL])
# es_index = "news_index"  # 저장할 인덱스 이름
#
# # 📌 **HDFS에서 JSON 데이터 불러오기**
# with hdfs_client.read(HDFS_PATH, encoding="utf-8") as reader:
#     json_lines = reader.readlines()  # JSON 파일을 줄 단위로 읽기
#
# # 📌 **전처리 과정**
# cleaned_data = []
# for line in json_lines:
#     try:
#         record = json.loads(line)  # JSON 문자열을 Python 딕셔너리로 변환
#         record["title"] = record["title"]  # 유니코드 자동 변환됨
#         record["description"] = record["description"]
#         record["snippet"] = record["snippet"]
#
#         # 불필요한 필드 제거
#         keys_to_remove = ["uuid", "image_url", "relevance_score"]
#         for key in keys_to_remove:
#             record.pop(key, None)
#
#         cleaned_data.append(record)
#
#     except json.JSONDecodeError:
#         print("❌ JSON 디코딩 오류 발생, 해당 줄을 건너뜁니다.")
#
# # 📌 **MongoDB에 저장**
# if cleaned_data:
#     mongo_collection.insert_many(cleaned_data)
#     print(f"✅ MongoDB에 {len(cleaned_data)}개 데이터 저장 완료!")
#
# # 📌 **Elasticsearch에 저장**
# for record in cleaned_data:
#     es.index(index=es_index, document=record)
# print(f"✅ Elasticsearch에 {len(cleaned_data)}개 데이터 저장 완료!")
