# import json
# import pymysql
# from urllib.parse import urlparse
#
# # 📌 **로컬 파일 경로 (HDFS 대신 사용)**
# LOCAL_JSON_FILE = "news_20250306.json"  # 파일명을 실제 파일명으로 변경
#
# # 📌 **MySQL 연결 설정**
# MYSQL_HOST = "localhost"
# MYSQL_USER = "root"
# MYSQL_PASSWORD = "ssafy"
# MYSQL_DATABASE = "newlens_db"
#
# # 📌 **G20 국가 리스트 (ISO 코드)**
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
# # 📌 **TLD 기반 국가 매핑**
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
#     # 📌 🇷🇺 러시아 미디어 예외 처리 (RT, Sputnik)
#     "rt": "RU",  # Russia Today (RT.com)
#     "sputnik": "RU",  # Sputnik News
#     # 📌 🇬🇧 영국 미디어 예외 처리 (BBC, The Guardian 등)
#     "bbc": "GB",  # British Broadcasting Corporation (bbc.com)
#     "guardian": "GB",  # The Guardian (theguardian.com)
#     # 📌 🇨🇳 중국 미디어 예외 처리 (Xinhua, CGTN)
#     "xinhuanet": "CN",  # Xinhua News Agency (xinhuanet.com)
#     "cgtn": "CN",  # China Global Television Network (cgtn.com)
#     # 📌 🇺🇸 미국 미디어 예외 처리 (CNN, NYTimes, Fox News)
#     "cnn": "US",  # Cable News Network (cnn.com)
#     "nytimes": "US",  # The New York Times (nytimes.com)
#     "foxnews": "US",  # Fox News (foxnews.com)
#     # 📌 🇨🇦 캐나다 미디어 예외 처리 (CBC)
#     "cbc": "CA",  # Canadian Broadcasting Corporation (cbc.ca)
#     # 📌 🇦🇺 호주 미디어 예외 처리 (ABC, The Australian)
#     "abc": "AU",  # Australian Broadcasting Corporation (abc.net.au)
#     "theaustralian": "AU",  # The Australian (theaustralian.com.au)
#     # 📌 🇪🇸 스페인 미디어 예외 처리 (El País, El Mundo)
#     "elpais": "ES",  # El País (elpais.com)
#     "elmundo": "ES",  # El Mundo (elmundo.es)
#     # 📌 🇮🇳 인도 미디어 예외 처리 (The Hindu, Times of India)
#     "thehindu": "IN",  # The Hindu (thehindu.com)
#     "timesofindia": "IN",  # Times of India (timesofindia.com)
#     # 📌 🌍 기타 주요 미디어
#     "reuters": "US",  # Reuters (reuters.com)
#     "bloomberg": "US",  # Bloomberg (bloomberg.com)
#     "aljazeera": "QA",  # Al Jazeera (aljazeera.com, Qatar)
#     "dw": "DE",  # Deutsche Welle (dw.com, Germany)
#     "lemonde": "FR",  # Le Monde (lemonde.fr, France)
#     "spiegel": "DE",  # Der Spiegel (spiegel.de, Germany)
#     # 📌 🌍 일부 국가의 특수 TLD
#     "uk": "GB",  # United Kingdom (co.uk)
# }
#
#
# # 📌 **국가 추출 함수**
# def get_country(source):
#     domain = source  # 도메인 추출
#     # print(f"🔍 도메인 추출 결과: {domain}")  # 디버깅 추가
#
#     # 1️⃣ 도메인이 없으면 Unknown
#     if not domain:
#         # print("⚠️ 도메인이 비어 있음 → 'Unknown' 반환")
#         return "Unknown"
#
#     # 2️⃣ TLD 기반 국가 확인
#     country = "Unknown"
#     parts = domain.split(".")
#     # print(f"🔍 도메인 분할 결과: {parts}")  # 디버깅 추가
#     for part in parts:
#         # print(f"🔎 검사 중: {part}")  # 디버깅 추가
#         if part in tld_mapping:
#             country = tld_mapping[part]
#             # print(f"✅ 국가 코드 발견: {part} → {country}")
#             break
#
#     # 3️⃣ G20 국가인지 확인
#     if country in G20_COUNTRIES:
#         # print(f"✅ [DEBUG] {country}는 G20 국가임!")
#         return country
#     elif country == "Unknown":
#         return "Unknown"
#     else:
#         # print(f"🚫 [DEBUG] {country}는 G20 국가가 아님 → 'Excluded' 반환")
#         return "Excluded"  # G20 국가가 아닌 경우
#
#
# # 📌 **MySQL에 `source_country` 데이터 삽입**
# def insert_source_country(source, country):
#     try:
#         conn = pymysql.connect(
#             host=MYSQL_HOST,
#             user=MYSQL_USER,
#             password=MYSQL_PASSWORD,
#             database=MYSQL_DATABASE,
#         )
#         cursor = conn.cursor()
#
#         # 1️⃣ 중복 확인: 이미 존재하는 경우 삽입하지 않음
#         cursor.execute(
#             "SELECT COUNT(*) FROM source_country WHERE source = %s", (source,)
#         )
#         if cursor.fetchone()[0] > 0:
#             print(f"✅ 이미 존재: {source} → {country}")
#             return
#
#         # 2️⃣ 새로운 source-country 삽입
#         cursor.execute(
#             "INSERT INTO source_country (source, country) VALUES (%s, %s)",
#             (source, country),
#         )
#         conn.commit()
#         print(f"✅ 삽입 완료: {source} → {country}")
#
#     except pymysql.MySQLError as e:
#         print(f"❌ MySQL 오류 발생: {e}")
#     finally:
#         if conn:
#             conn.close()
#
#
# # 📌 **로컬 JSON 파일에서 데이터 읽고 source_country 테이블 업데이트**
# try:
#     with open(LOCAL_JSON_FILE, "r", encoding="utf-8") as file:
#         json_lines = file.readlines()
#
#     for line in json_lines:
#         try:
#             record = json.loads(line)  # JSON 문자열을 Python 딕셔너리로 변환
#             source = record.get("source", "")
#             # print(source)
#
#             if source:  # `source` 값이 존재하면 국가 매핑 후 MySQL에 삽입
#                 country = get_country(source)
#                 insert_source_country(source, country)
#
#         except json.JSONDecodeError:
#             print("❌ JSON 디코딩 오류 발생, 해당 줄을 건너뜁니다.")
#
#     print("✅ 모든 데이터 처리 완료!")
#
# except FileNotFoundError:
#     print(f"❌ 파일을 찾을 수 없음: {LOCAL_JSON_FILE}")
