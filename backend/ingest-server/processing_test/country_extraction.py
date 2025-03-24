# import pymysql
# from urllib.parse import urlparse
# from openai import OpenAI
# import os
# from dotenv import load_dotenv
#
# # -----------------------------------
# # 1. OpenAI API 키 설정
# # -----------------------------------
# load_dotenv()  # .env 파일 로드
# client = OpenAI(
#     api_key=os.environ["OPENAI_API_KEY"],
# )
#
# # -----------------------------------
# # 2. MySQL 연결 설정
# # -----------------------------------
# MYSQL_HOST = "localhost"
# MYSQL_USER = "root"
# MYSQL_PASSWORD = "ssafy"
# MYSQL_DATABASE = "newlens_db"
#
# # -----------------------------------
# # 3. G20 국가 리스트 (ISO 코드)
# # -----------------------------------
# G20_COUNTRIES = {
#     "AR",
#     "AU",
#     "BR",
#     "CA",
#     "CN",
#     "DE",
#     "EU",
#     "FR",
#     "GB",
#     "IN",
#     "ID",
#     "IT",
#     "JP",
#     "KR",
#     "MX",
#     "RU",
#     "SA",
#     "ZA",
#     "TR",
#     "US",
# }
#
# # -----------------------------------
# # 4. TLD 기반 국가 매핑 (소문자로 비교)
# # -----------------------------------
# tld_mapping = {
#     "ar": "AR",
#     "au": "AU",
#     "br": "BR",
#     "ca": "CA",
#     "cn": "CN",
#     "de": "DE",
#     "eu": "EU",
#     "fr": "FR",
#     "gb": "GB",
#     "in": "IN",
#     "id": "ID",
#     "it": "IT",
#     "jp": "JP",
#     "kr": "KR",
#     "mx": "MX",
#     "ru": "RU",
#     "sa": "SA",
#     "za": "ZA",
#     "tr": "TR",
#     "us": "US",
#     # 예외 처리: 미디어 도메인
#     "rt": "RU",
#     "sputnik": "RU",
#     "bbc": "GB",
#     "guardian": "GB",
#     "xinhuanet": "CN",
#     "cgtn": "CN",
#     "cnn": "US",
#     "nytimes": "US",
#     "foxnews": "US",
#     "cbc": "CA",
#     "abc": "AU",
#     "theaustralian": "AU",
#     "elpais": "ES",
#     "elmundo": "ES",
#     "thehindu": "IN",
#     "timesofindia": "IN",
#     "reuters": "US",
#     "bloomberg": "US",
#     "aljazeera": "QA",
#     "dw": "DE",
#     "lemonde": "FR",
#     "spiegel": "DE",
#     "uk": "GB",
# }
#
#
# def fetch_source_country(source):
#     """
#     MySQL의 source_country 테이블에서 해당 source의 국가 코드를 조회합니다.
#     """
#     conn = None
#     try:
#         conn = pymysql.connect(
#             host=MYSQL_HOST,
#             user=MYSQL_USER,
#             password=MYSQL_PASSWORD,
#             database=MYSQL_DATABASE,
#         )
#         cursor = conn.cursor()
#         cursor.execute(
#             "SELECT country FROM source_country WHERE source = %s", (source,)
#         )
#         result = cursor.fetchone()
#         if result:
#             return result[0]
#     except pymysql.MySQLError as e:
#         print(f"❌ MySQL 조회 오류: {e}")
#     finally:
#         if conn:
#             conn.close()
#     return None
#
#
# def insert_source_country(source, country):
#     """
#     MySQL의 source_country 테이블에 (source, country) 정보를 삽입합니다.
#     이미 존재하면 삽입하지 않습니다.
#     """
#     conn = None
#     try:
#         conn = pymysql.connect(
#             host=MYSQL_HOST,
#             user=MYSQL_USER,
#             password=MYSQL_PASSWORD,
#             database=MYSQL_DATABASE,
#         )
#         cursor = conn.cursor()
#         # 중복 여부 확인
#         cursor.execute(
#             "SELECT COUNT(*) FROM source_country WHERE source = %s", (source,)
#         )
#         if cursor.fetchone()[0] > 0:
#             # print(f"✅ 이미 존재: {source} → {country}")
#             return
#         # 신규 삽입
#         cursor.execute(
#             "INSERT INTO source_country (source, country) VALUES (%s, %s)",
#             (source, country),
#         )
#         conn.commit()
#         # print(f"✅ 삽입 완료: {source} → {country}")
#     except pymysql.MySQLError as e:
#         print(f"❌ MySQL 삽입 오류: {e}")
#     finally:
#         if conn:
#             conn.close()
#
#
# def get_country_by_tld(source):
#     """
#     source 문자열에서 TLD 기반으로 국가 코드를 추출합니다.
#     예: "arabic.rt.com" → "RT" 파트를 통해 매핑 → "RU"
#     """
#     parsed = urlparse(source)
#     domain = parsed.netloc if parsed.netloc else source
#     parts = domain.split(".")
#     for part in parts:
#         part_lower = part.lower()
#         if part_lower in tld_mapping:
#             return tld_mapping[part_lower]
#     return "Unknown"
#
#
# def get_country_by_gpt(source):
#     client = OpenAI()
#
#     prompt = (
#         f"뉴스 미디어 도메인 '{source}'를 기반으로 ISO 국가 코드를 알려주세요. "
#         "예를 들어, 미국이면 'US', 한국이면 'KR'과 같이 한 단어로만 응답해주세요."
#         "알파벳을 제외한 그 어떤 응답도 하지 마세요."
#     )
#     try:
#         response = client.chat.completions.create(
#             model="gpt-3.5-turbo",
#             messages=[
#                 {
#                     "role": "system",
#                     "content": "너는 ISO 국가 코드 전문가야. 정확하게 한 단어로만 답변해.",
#                 },
#                 {"role": "user", "content": prompt},
#             ],
#             max_tokens=5,
#             temperature=0.0,
#         )
#         country = response.choices[0].message.content
#         return country.upper()
#     except Exception as e:
#         print(f"❌ GPT API 호출 오류: {e}")
#         return "Unknown"
#
#
# def determine_source_country(source):
#     """
#     1. MySQL에서 source_country 값을 먼저 조회합니다.
#     2. 값이 없으면 TLD 매핑을 통해 국가 코드를 추출합니다.
#     3. TLD 매핑으로 알 수 없다면 GPT를 호출하여 국가 코드를 받아옵니다.
#     4. 최종적으로 MySQL 테이블에 (source, country) 정보를 삽입한 후 국가 코드를 반환합니다.
#     """
#     # 1️⃣ DB 조회
#     country = fetch_source_country(source)
#     if country:
#         return country
#
#     # 2️⃣ TLD 기반 추출
#     country = get_country_by_tld(source)
#
#     # 3️⃣ TLD 매핑으로도 알 수 없다면 GPT 사용
#     if country == "Unknown":
#         country = get_country_by_gpt(source)
#
#     # 4️⃣ DB에 삽입 및 반환
#     insert_source_country(source, country)
#     return country
