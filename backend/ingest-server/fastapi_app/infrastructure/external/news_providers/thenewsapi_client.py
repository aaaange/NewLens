import requests
import os
from datetime import datetime

from dotenv import load_dotenv

load_dotenv()  # .env 파일을 로드


class TheNewsAPIClient:
    def __init__(self):
        self.api_key = os.getenv("THENEWSAPI_KEY", "your_api_key")
        self.base_url = "https://api.thenewsapi.com/v1/news/all"

    def fetch_news(
        self,
        page: int = 1,
        published_on: str = None,
        limit: int = 3,
    ):
        """
        thenewsapi에서 오늘 날짜 뉴스를 가져옵니다.
        파라미터:
          - page: 페이지 번호 (1~15)
          - published_on: 날짜 (YYYY-MM-DD 형식)
          - limit: 한 페이지당 기사 수 (100)
        """
        if published_on is None:
            published_on = datetime.now().strftime("%Y-%m-%d")
        params = {
            "api_token": self.api_key,
            "page": page,
            "published_on": published_on,
            "limit": limit,
        }
        response = requests.get(self.base_url, params=params)
        response.raise_for_status()
        return response.json()


# 테스트 실행 예시
if __name__ == "__main__":
    client = TheNewsAPIClient()
    news_data = client.fetch_news()
    print(news_data)
