# # db.py
# import mysql.connector
# from pymongo import MongoClient
#
# # MySQL 연결 정보
# MYSQL_CONFIG = {
#     "host": "localhost",
#     "user": "root",
#     "password": "ssafy",
#     "database": "newlens_db",
# }
#
# # MongoDB 연결 정보
# MONGO_URI = "mongodb://localhost:27017"
# MONGO_DB_NAME = "test_db"
# MONGO_COLLECTION_NAME = "processed_foreign_news"
#
#
# def save_source_country(source: str, country: str):
#     """
#     MySQL의 source_country 테이블에 (source, country) 정보를 저장합니다.
#     """
#     try:
#         conn = mysql.connector.connect(**MYSQL_CONFIG)
#         cursor = conn.cursor()
#
#         select_sql = "SELECT id FROM source_country WHERE source = %s"
#         cursor.execute(select_sql, (source,))
#         row = cursor.fetchone()
#
#         if row:
#             update_sql = "UPDATE source_country SET country = %s WHERE id = %s"
#             cursor.execute(update_sql, (country, row[0]))
#         else:
#             insert_sql = "INSERT INTO source_country (source, country) VALUES (%s, %s)"
#             cursor.execute(insert_sql, (source, country))
#
#         conn.commit()
#         cursor.close()
#         conn.close()
#     except Exception as e:
#         print(f"[MySQL 저장 오류] {e}")
#
#
# def save_foreign_news(processed_data: dict):
#     """
#     MongoDB의 foreign_news 컬렉션에 전처리된 데이터를 저장합니다.
#     """
#     try:
#         client = MongoClient(MONGO_URI)
#         db = client[MONGO_DB_NAME]
#         collection = db[MONGO_COLLECTION_NAME]
#         collection.insert_one(processed_data)
#         client.close()
#     except Exception as e:
#         print(f"[MongoDB 저장 오류] {e}")
