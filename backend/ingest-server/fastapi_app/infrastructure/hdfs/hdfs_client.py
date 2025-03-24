import json
from datetime import datetime
import os
from hdfs import InsecureClient


class HDFSClient:
    def __init__(self):
        # .env 또는 환경변수에서 HDFS 호스트와 포트를 불러옵니다.
        hdfs_host = os.getenv("HDFS_HOST", "localhost")
        hdfs_port = os.getenv("HDFS_PORT", "9870")
        self.client = InsecureClient(f"http://{hdfs_host}:{hdfs_port}", user="root")

    def read_file(self, file_path: str) -> str:
        """
        HDFS 상의 파일을 읽어 문자열로 반환합니다.
        """
        return self.client.read(file_path, encoding="utf-8")

    def write_file(self, file_path: str, data: str, overwrite: bool = False):
        """
        HDFS에 데이터를 기록합니다.
        """
        self.client.write(file_path, data, encoding="utf-8", overwrite=overwrite)

    def append_news_articles(self, news_articles, base_dir: str):
        """
        뉴스 기사 리스트를 HDFS에 newline-delimited JSON 형식으로 저장합니다.
        저장 경로는 base_dir (예: /data/raw/news/domestic 또는 /data/raw/news/worldwide) 아래
        년/월/일 디렉토리로 생성하며, 같은 날짜 파일은 하나의 파일에 이어서 기록합니다.

        :param news_articles: 각 기사(dict)들이 담긴 리스트
        :param base_dir: 저장할 기본 디렉토리 (domestic 또는 worldwide)
        """
        # 오늘 날짜를 기준으로 디렉토리 및 파일 이름 지정
        today_dir = datetime.now().strftime("%Y/%m/%d")
        file_name = f"news_{datetime.now().strftime('%Y%m%d')}.json"
        hdfs_dir = f"{base_dir}/{today_dir}"
        hdfs_path = f"{hdfs_dir}/{file_name}"

        # 디렉토리가 없으면 생성
        self.client.makedirs(hdfs_dir)

        # 파일 존재 여부에 따라 새 파일 생성 또는 append 모드로 기록
        if self.client.status(hdfs_path, strict=False) is None:
            with self.client.write(
                hdfs_path, encoding="utf-8", overwrite=True
            ) as writer:
                for article in news_articles:
                    writer.write(json.dumps(article) + "\n")
            print(
                f"Created new file and wrote {len(news_articles)} articles to {hdfs_path}"
            )
        else:
            with self.client.write(hdfs_path, encoding="utf-8", append=True) as writer:
                for article in news_articles:
                    writer.write(json.dumps(article) + "\n")
            print(f"Appended {len(news_articles)} articles to {hdfs_path}")


if __name__ == "__main__":
    client = HDFSClient()
    # 간단한 뉴스 기사 예시
    test_articles = [{"title": "테스트 기사", "content": "내용"}]
    try:
        client.append_news_articles(test_articles, base_dir="/data/raw/news/domestic")
    except Exception as e:
        print("HDFS 테스트 중 오류 발생:", e)
