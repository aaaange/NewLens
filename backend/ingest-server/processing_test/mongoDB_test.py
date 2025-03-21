from pymongo import MongoClient

client = MongoClient("mongodb://localhost:27017/")
db = client["test_db"]  # 이미 존재하는 DB
collection = db["my_collection"]  # 이미 존재하는 컬렉션

# name이 "Alice"인 문서를 찾아서 age 필드를 20으로 추가/갱신
result = collection.update_many(
    {"name": "Alice"},  # 업데이트 대상 조건
    {"$set": {"age": 20}},  # 새 필드 age를 20으로 설정
)

print("Matched count:", result.matched_count)
print("Modified count:", result.modified_count)
