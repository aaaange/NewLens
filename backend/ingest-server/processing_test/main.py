#!/usr/bin/env python
from categorization import classify_text
from keyword_extraction import extract_keywords
from sentiment import extract_sentiment
from translation import translate
from utils import format_published_at
from pymongo import MongoClient

client = MongoClient("mongodb://localhost:27017/")
db = client["test_db"]  # 이미 존재하는 DB
collection = db["my_collection"]  # 이미 존재하는 컬렉션


def main():
    # JSON 형식의 뉴스 기사 데이터 (예시)
    news_item = {
        "uuid": "df1a2472-d866-4649-874e-80d23ab422ce",
        "title": "سوق الحقوق في طرابلس.. عقاقبة بروح العصر",
        "description": "مع حلول شهر رمضآن، ينبض سوق الحقوق في طرابلس الليبية بحياة متجددة، حيث يتوافر الأهالي ل... ",
        "keywords": "",
        "snippet": "سوق الحقوق في طرابلس.. عقاقبة بروح العصر\n\nمع حلول شهر رمضآن، ينبض سوق الحقوق في طرابلس الليبية...",
        "url": "https://arabic.rt.com/features/1652610-...",
        "image_url": "https://mf.b37mrtl.ru/media/pics/2025.03/original/67ca347542360439fc2b9ec0.jpg",
        "language": "ar",
        "published_at": "2025-03-06T23:49:55.000000Z",
        "source": "arabic.rt.com",
        "categories": ["general"],
        "relevance_score": None,
    }

    # 본문 텍스트: description이 있으면 사용하고, 없으면 snippet 사용
    text = news_item.get("description") or news_item.get("snippet")

    # 발행일시를 ISO 8601 형식에서 "YYYY-MM-DD HH:MM:SS" 형식으로 변환
    formatted_date = format_published_at(news_item["published_at"])

    # 카테고리 추출 (최대 3개)
    categories = classify_text(text, multi_label=True, max_categories=3)

    # 감성 분석 (긍정, 중립, 부정 점수)
    sentiment_scores = extract_sentiment(text)
    sentiment_dic = {sentiment_scores[0]: sentiment_scores[1]}

    # 뉴스 기사가 한국어가 아니면 한국어로 번역
    if news_item.get("language") != "ko":
        text = translate({news_item["language"]: text})

    # Komoran + TF-IDF를 이용한 핵심 키워드 추출 (최대 5개)
    keywords = extract_keywords(text, max_keywords=10)

    # mongoDB에 데이터 입력
    collection.insert_one(
        {
            "title": translate({news_item["language"]: news_item["title"]}),
            "description": translate({news_item["language"]: news_item["description"]}),
            "url": news_item["url"],
            "published_at": formatted_date,
            "image_url": news_item["image_url"],
            "categories": categories,
            "keywords": keywords,
            "sentiment": sentiment_dic,
            "raw_data_ref": news_item["uuid"],
        }
    )

    # 결과 출력
    print("UUID:", news_item["uuid"])
    print("제목:", translate({news_item["language"]: news_item["title"]}))
    print("요약:", translate({news_item["language"]: news_item["description"]}))
    print("발행일시:", formatted_date)
    print("ImageUrl:", news_item["image_url"])
    print("분류된 카테고리:", categories)
    print("핵심 키워드:", keywords)
    print("감성 점수:", sentiment_scores)
    print("URL:", news_item["url"])


if __name__ == "__main__":
    main()
