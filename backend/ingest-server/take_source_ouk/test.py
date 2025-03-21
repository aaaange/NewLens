import requests
import json


def fetch_news_from_newsapi():
    """
    The News API를 호출하여 최신 뉴스 헤드라인을 받아온 뒤,
    콘솔에 JSON 형태로 출력하는 간단한 예시 함수
    """
    # 1) API 엔드포인트 & API 키 설정
    #    country=us -> 미국 뉴스 헤드라인
    #    그 외 파라미터들(country, category, q 등)을 적절히 바꿀 수 있습니다.
    api_key = "oKc7obFF3z4bt67W2utPVPD65uJOUGptawIPdgaz"
    language = "ko"
    limit = 100
    published_on = "2025-03-13"
    page = 2
    url = f"https://api.thenewsapi.com/v1/news/all?api_token={api_key}&language={language}&limit={limit}&published_on={published_on}&page={page}"

    # 2) GET 요청
    response = requests.get(url)

    # 3) 성공 여부 확인
    if response.status_code == 200:
        data = response.json()
        # 4) 전체 응답을 콘솔에 출력 (articles 포함)
        print(json.dumps(data, indent=2, ensure_ascii=False))
        #   데이터 예시:
        #   {
        #       "status": "ok",
        #       "totalResults": 34,
        #       "articles": [ ... ]
        #   }
    else:
        print(f"API 호출 실패. 상태 코드: {response.status_code}")
        print(response.text)


if __name__ == "__main__":
    fetch_news_from_newsapi()
