import pymysql
import os


class MySQLClient:
    def __init__(self):
        self.connection = pymysql.connect(
            host=os.getenv("MYSQL_HOST", "localhost"),
            user=os.getenv("MYSQL_USER", "root"),
            password=os.getenv("MYSQL_PASSWORD", "ssafy"),
            database=os.getenv("MYSQL_DATABASE", "newlens_db"),
            charset="utf8mb4",
            cursorclass=pymysql.cursors.DictCursor,
        )

    def fetch_source_country(self, source: str) -> str:
        """
        source_country 테이블에서 해당 source의 국가 코드를 조회합니다.
        """
        try:
            with self.connection.cursor() as cursor:
                sql = "SELECT country FROM source_country WHERE source = %s"
                cursor.execute(sql, (source,))
                result = cursor.fetchone()
                if result:
                    return result["country"]
        except pymysql.MySQLError as e:
            print(f"MySQL fetch error: {e}")
        return None

    def insert_source_country(self, source: str, country: str):
        """
        source_country 테이블에 (source, country) 정보를 삽입합니다.
        이미 존재하면 삽입하지 않습니다.
        """
        try:
            with self.connection.cursor() as cursor:
                # 중복 여부 확인
                sql_check = (
                    "SELECT COUNT(*) as count FROM source_country WHERE source = %s"
                )
                cursor.execute(sql_check, (source,))
                count = cursor.fetchone()["count"]
                if count > 0:
                    return
                # 신규 삽입
                sql_insert = (
                    "INSERT INTO source_country (source, country) VALUES (%s, %s)"
                )
                cursor.execute(sql_insert, (source, country))
            self.connection.commit()
        except pymysql.MySQLError as e:
            print(f"MySQL insert error: {e}")

    def close(self):
        self.connection.close()
