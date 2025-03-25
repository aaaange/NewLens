from fastapi_app.domain.models.news import News
from fastapi_app.domain.services import (
    sentiment_analysis,
    keyword_extraction,
    translation,
    country_detection,
    category_classification,
    language_detection,
    country_detection,
)
import logging

logger = logging.getLogger(__name__)


def process_news_data(news: News) -> News:
    """
    원시 News 데이터를 받아서 전처리 작업(감정 분석, 키워드 추출, 번역, 나라 추정, 카테고리 분류)을 수행합니다.

    매 단계마다 도메인 서비스를 호출하며, 처리 결과를 News 객체에 업데이트합니다.

    :param news: 원시 News 객체
    :return: 전처리 완료된 News 객체
    """
    try:

        # 나라 추정: 기사 내용 기반으로 해당 기사의 연관 국가 추정
        news.country = country_detection.determine_source_country(news.source)
        logger.debug("Country detection complete: %s", news.country)

        # 나라 추정 결과가 "Unknown"이면 해당 기사 건너뛰기
        if news.country == "Unknown":
            logger.info("Skipping news due to unknown country.")
            return None

        # 감정 분석: 텍스트 내용에 따른 감정 점수를 도출
        news.sentiment = sentiment_analysis.analyze_sentiment(news.content)
        logger.debug("Sentiment analysis complete: %s", news.sentiment)

        # 언어 추정 : 텍스트의 언어를 감지하여 ISO 639-1 코드로 반환
        lang_code = language_detection.detect_language(news.content)

        # 뉴스 기사가 한국어가 아니면 한국어로 번역
        if lang_code != "ko":
            news.content = translation.translate_text(news.content, lang_code)
            news.title = translation.translate_text(news.title, lang_code)

        logger.debug(
            "Translation complete: [title] %s, [description] %s",
            news.title,
            news.content,
        )

        # 키워드 추출: 기사 내용에서 중요한 단어 목록 추출
        news.keywords = keyword_extraction.extract_keywords(news.content)
        if not news.keywords:
            return None
        logger.debug("Keyword extraction complete: %s", news.keywords)

        # 카테고리 분류: 기사 내용을 분석하여 적절한 카테고리 결정
        if not news.categories:
            news.categories = [category_classification.classify_category(news.content)]
        else:
            return None

        # logger.debug("Category classification complete: %s", news.category)

        # 제목이나 내용이 "?" 또는 공백만 있으면 건너뛰기
        if news.title.strip() in {"", "?"} or news.content.strip() in {"", "?"}:
            logger.info("Skipping news due to invalid title or content.")
            return None

    except Exception as e:
        logger.error("Error during processing news data: %s", e)
        # 필요시 예외를 던지거나 원본 객체를 그대로 반환할 수 있습니다.
    return news
