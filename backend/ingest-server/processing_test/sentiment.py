from transformers import pipeline


def extract_sentiment(text) -> list:
    # nlptown 모델을 확률 분포 형태로 출력하도록 설정
    classifier = pipeline(
        "sentiment-analysis",
        model="nlptown/bert-base-multilingual-uncased-sentiment",
        top_k=None,
    )

    # 기사 원문 예시 (영어 뉴스 기사)
    # text = (
    #     "The government's new economic reform has shown promising signs of growth, "
    #     "yet experts caution that the long-term impact remains uncertain amidst global volatility."
    # )

    # 모델 예측 (모든 별에 대한 확률 분포가 리스트로 반환됨)
    scores = classifier(text)[
        0
    ]  # 예: [{'label': '1 star', 'score': 0.10}, {'label': '2 stars', 'score': 0.20}, ...]

    # 매핑 규칙에 따라 점수 합산
    negative = sum(
        score["score"] for score in scores if score["label"] in ["1 star", "2 stars"]
    )
    neutral = sum(score["score"] for score in scores if score["label"] == "3 stars")
    positive = sum(
        score["score"] for score in scores if score["label"] in ["4 stars", "5 stars"]
    )

    result = [positive, neutral, negative]

    return result

    # print("Positive: {:.2%}".format(positive))
    # print("Neutral:  {:.2%}".format(neutral))
    # print("Negative: {:.2%}".format(negative))
