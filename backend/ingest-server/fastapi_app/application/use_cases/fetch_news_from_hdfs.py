import json
import logging
from datetime import datetime
from fastapi_app.infrastructure.hdfs.hdfs_client import HDFSClient

logger = logging.getLogger(__name__)


def fetch_news_from_hdfs(source: str) -> list:
    """
    HDFS에 저장된 원시 뉴스 데이터를 읽어와서 리스트로 반환합니다.

    저장 경로는 다음과 같이 구성됩니다:
      /data/raw/news/{source}/YYYY/MM/DD/news_YYYYMMDD.json

    :param source: 뉴스 API 구분 값 ("domestic" 또는 "worldwide")
    :return: 뉴스 기사들이 담긴 리스트 (각각 dict)
    """
    hdfs_client = HDFSClient()
    # 오늘 날짜에 따른 디렉토리 및 파일 이름 생성
    today_dir = datetime.now().strftime("%Y/%m/%d")
    file_name = f"news_{datetime.now().strftime('%Y%m%d')}.json"
    # HDFS 기본 경로는 /data/raw/news 이며, source에 따라 하위 폴더가 달라집니다.
    hdfs_path = f"/data/raw/news/{source}/{today_dir}/{file_name}"

    try:
        # with 블록을 사용해 파일 객체를 연다.
        with hdfs_client.read_file(hdfs_path) as reader:
            raw_data = reader.read()
        # raw_data는 newline-delimited JSON 형식이므로, 각 줄을 개별 기사로 파싱합니다.
        articles = [json.loads(line) for line in raw_data.splitlines() if line.strip()]
        logger.info(
            "Fetched %d news articles from HDFS path %s", len(articles), hdfs_path
        )
        return articles
    except Exception as e:
        logger.error("Error fetching news from HDFS: %s", e)
        return []


if __name__ == "__main__":
    articles = fetch_news_from_hdfs("domestic")
    for article in articles:
        print(article)
