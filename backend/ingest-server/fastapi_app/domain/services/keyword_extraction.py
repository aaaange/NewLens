from konlpy.tag import Komoran
from sklearn.feature_extraction.text import TfidfVectorizer
import numpy as np
import logging

logger = logging.getLogger(__name__)


def extract_keywords(text: str) -> list:
    """
    Komoran으로 텍스트에서 명사(주로 핵심 단어)를 추출한 후,
    TF-IDF를 적용하여 핵심 키워드(max_keywords)를 선택합니다.
    """
    if not text:
        return []

    # 1. Komoran을 사용하여 텍스트에서 명사 추출
    komoran = Komoran()
    tokens = komoran.nouns(text)

    # 만약 추출된 토큰이 없으면 빈 리스트 반환
    if not tokens:
        logger.warning("No tokens extracted from text for TF-IDF keyword extraction.")
        return []

    # 예: "키워드"가 제대로 추출되지 않을 수 있으므로, 다른 분석기로 보완 가능
    # 추출된 명사 리스트를 공백으로 결합하여 새로운 텍스트를 생성
    tokenized_text = " ".join(tokens)

    # 토큰화 결과가 빈 문자열이면 빈 리스트 반환
    if not tokenized_text.strip():
        logger.warning("Tokenized text is empty after joining tokens.")
        return []

    # 2. TF-IDF를 사용하여 핵심 키워드 추출
    # 한 개의 문서로 구성된 코퍼스로 처리합니다.
    corpus = [tokenized_text]
    vectorizer = TfidfVectorizer()
    tfidf_matrix = vectorizer.fit_transform(corpus)

    # 학습된 단어 목록과 해당 TF-IDF 점수 추출
    feature_names = vectorizer.get_feature_names_out()
    scores = tfidf_matrix.toarray()[0]

    # TF-IDF 점수를 내림차순으로 정렬한 인덱스 배열 생성
    sorted_indices = np.argsort(scores)[::-1]

    # 상위 max_keywords 개의 인덱스 선택
    max_keywords = 20
    top_indices = sorted_indices[:max_keywords]

    # TF-IDF 점수가 0보다 큰 단어들만 키워드로 선택
    top_keywords = [feature_names[i] for i in top_indices if scores[i] > 0]

    return top_keywords
