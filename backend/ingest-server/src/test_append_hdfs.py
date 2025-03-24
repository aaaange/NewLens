# import json
# from datetime import datetime
# from hdfs import InsecureClient
#
#
# def append_news_to_hdfs(news_articles, base_dir="/data/raw/news"):
#     """
#     news_articles: 뉴스 기사 리스트 (각각 dict)
#     base_dir: HDFS 상의 기본 저장 경로
#     """
#     # 오늘 날짜를 기준으로 디렉토리 및 파일 이름 지정
#     today_dir = datetime.now().strftime("%Y/%m/%d")
#     file_name = f"news_{datetime.now().strftime('%Y%m%d')}.json"
#     hdfs_dir = f"{base_dir}/{today_dir}"
#     hdfs_path = f"{hdfs_dir}/{file_name}"
#
#     # HDFS 클라이언트 생성 (네임노드 주소와 적절한 user 지정)
#     client = InsecureClient("http://localhost:9870", user="root")
#
#     # 저장할 디렉토리가 없으면 생성
#     client.makedirs(hdfs_dir)
#
#     # newline-delimited JSON 형식으로 데이터를 저장하도록 함
#     # (파일이 없으면 새로 만들고, 있으면 append 모드로 열기)
#     if client.status(hdfs_path, strict=False) is None:
#         # 파일이 없으므로, 각 기사를 한 줄씩 기록
#         with client.write(hdfs_path, encoding="utf-8", overwrite=True) as writer:
#             for article in news_articles:
#                 writer.write(json.dumps(article) + "\n")
#         print(
#             f"Created new file and wrote {len(news_articles)} articles to {hdfs_path}"
#         )
#     else:
#         # 파일이 이미 존재하면, append 모드로 열어서 새 줄 추가
#         with client.write(hdfs_path, encoding="utf-8", append=True) as writer:
#             for article in news_articles:
#                 writer.write(json.dumps(article) + "\n")
#         print(f"Appended {len(news_articles)} articles to {hdfs_path}")
#
#
# if __name__ == "__main__":
#     # 예시 API 응답(JSON 구조) (실제 API 호출 결과 대신 사용)
#     api_response = {
#         "meta": {"found": 168962, "returned": 3, "limit": 3, "page": 1},
#         "data": [
#             {
#                 "uuid": "8e9b1c0e-75d5-4739-9ff8-135194cef336",
#                 "title": "Real Madrid : La presse espagnole balance fort sur Mbappé !",
#                 "description": "Après des débuts compliqués avec le Real Madrid...",
#                 "keywords": "",
#                 "snippet": "Après des débuts compliqués avec le Real Madrid...",
#                 "url": "https://www.livefoot.fr/actualite/real-madrid-la-presse-espagnole-balance-fort-sur-mbappe.html",
#                 "image_url": "https://www.livefoot.fr/images/kylian-mbappe-Depositphotos_747337948.jpg",
#                 "language": "fr",
#                 "published_at": "2025-03-13T23:59:59.000000Z",
#                 "source": "livefoot.fr",
#                 "categories": ["sports"],
#                 "relevance_score": None,
#             },
#             {
#                 "uuid": "de77a4c0-4826-45c2-a485-9ebc72df5a37",
#                 "title": "いくつ知ってる？スマート家電10選！暮らしを便利にするアイテムはこちら！｜希@フリーランス",
#                 "description": "※広告リンクを含みます。 こんにちは！三浦です...",
#                 "keywords": "",
#                 "snippet": "※広告リンクを含みます。\n\nこんにちは！三浦です...",
#                 "url": "https://note.com/easy_eagle4925/n/n06ba76ae6c58",
#                 "image_url": "https://assets.st-note.com/production/uploads/images/178951465/rectangle_large_type_2_e367da11ca02ddf2e92f8370abd54f14.png?fit=bounds&quality=85&width=1280",
#                 "language": "ja",
#                 "published_at": "2025-03-13T23:59:59.000000Z",
#                 "source": "note.com",
#                 "categories": ["tech"],
#                 "relevance_score": None,
#             },
#             {
#                 "uuid": "78612372-84c2-4bfc-be40-4a80e62a00ca",
#                 "title": "Reclaman cierre de cruces ilegales para evitar accidentes en la 6 de Noviembre – El Nuevo Diario (República Dominicana)",
#                 "description": "",
#                 "keywords": "",
#                 "snippet": "Alcalde Nelson de la Rosa...",
#                 "url": "https://elnuevodiario.com.do/reclaman-cierre-de-cruces-ilegales-para-evitar-accidentes-en-la-6-de-noviembre/",
#                 "image_url": "https://nuevodiario-assets.s3.us-east-2.amazonaws.com/wp-content/uploads/2025/03/13195918/WhatsApp-Image-2025-03-13-at-7.35.10-PM-1200x1178.jpeg",
#                 "language": "es",
#                 "published_at": "2025-03-13T23:59:58.000000Z",
#                 "source": "elnuevodiario.com.do",
#                 "categories": ["general"],
#                 "relevance_score": None,
#             },
#         ],
#     }
#
#     # news_articles 리스트 추출
#     articles = api_response.get("data", [])
#
#     # HDFS에 데이터를 이어서 저장
#     append_news_to_hdfs(articles)
