from transformers import pipeline


def extract_sentiment(text) -> list:
    # nlptown 모델을 확률 분포 형태로 출력하도록 설정
    classifier = pipeline(
        "sentiment-analysis",
        model="nlptown/bert-base-multilingual-uncased-sentiment",
        return_all_scores=True,
    )

    # 기사 원문 예시 (영어 뉴스 기사)
    # text = (
    #     "The government's new economic reform has shown promising signs of growth, "
    #     "yet experts caution that the long-term impact remains uncertain amidst global volatility."
    # )

    # 모델 예측 (모든 별에 대한 확률 분포가 리스트로 반환됨)
    scores = classifier(text)[0]  # 예: [{'label': '1 star', 'score': 0.10}, ...]

    # 가중 평균 계산
    # 각 label에서 첫 글자가 별점(예: "1", "2", ...)이라고 가정하여 정수로 변환
    weighted_score = sum(int(score["label"][0]) * score["score"] for score in scores)

    # weighted_score는 1~5 범위이므로 이를 0~1 범위로 정규화 (1점이 0%, 5점이 100%가 되도록)
    normalized_score = (weighted_score - 1) / 4

    # 정규화된 점수를 백분율로 변환 (0~100%)
    percentage_score = int(normalized_score * 100)

    # 임계값에 따른 분류
    if percentage_score < 33:
        sentiment = "negative"
    elif percentage_score < 66:
        sentiment = "neutral"
    else:
        sentiment = "positive"

    return [sentiment, percentage_score]

    # print("Weighted Score: {:.2f}%".format(percentage_score))
    # print("Overall Sentiment:", sentiment)
