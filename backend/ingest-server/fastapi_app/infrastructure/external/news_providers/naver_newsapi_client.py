import requests
import os
from dotenv import load_dotenv

# .env 파일의 환경 변수를 로드합니다.'
load_dotenv()


class NaverNewsAPIClient:
    def __init__(self):
        self.client_id = os.getenv("NAVER_NEWSAPI_CLIENT_ID", "your_client_id")
        self.client_secret = os.getenv(
            "NAVER_NEWSAPI_CLIENT_SECRET", "your_client_secret"
        )
        self.base_url = "https://openapi.naver.com/v1/search/news.json"

    def fetch_news(
        self, query: str = "다", start: int = 1, display: int = 100, sort: str = "date"
    ):
        """
        네이버 뉴스 API를 호출하여 뉴스를 가져옵니다.
        파라미터:
          - query: 검색어 (여기서는 "다"를 사용하여 모든 뉴스를 조회)
          - start: 시작 인덱스 (1, 2)
          - display: 한 번에 가져올 기사 수 (100)
          - sort: 정렬 방식 ("date"로 최신순 정렬)
        """
        headers = {
            "X-Naver-Client-Id": self.client_id,
            "X-Naver-Client-Secret": self.client_secret,
        }
        params = {
            "query": query,
            "display": display,
            "start": start,
            "sort": sort,
        }
        response = requests.get(self.base_url, headers=headers, params=params)
        response.raise_for_status()
        return response.json()


# 테스트 실행 예시
if __name__ == "__main__":
    client = NaverNewsAPIClient()
    news_data = client.fetch_news("다")
    print(news_data)
