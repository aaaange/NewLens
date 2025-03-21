from konlpy.tag import Komoran
from sklearn.feature_extraction.text import TfidfVectorizer
import numpy as np


def extract_keywords(text: str, max_keywords=5) -> list:
    """
    Komoran으로 텍스트에서 명사(주로 핵심 단어)를 추출한 후,
    TF-IDF를 적용하여 핵심 키워드(max_keywords)를 선택합니다.
    """
    if not text:
        return []

    # 1. Komoran을 사용하여 텍스트에서 명사 추출
    komoran = Komoran()
    tokens = komoran.nouns(text)

    # 예: "키워드"가 제대로 추출되지 않을 수 있으므로, 다른 분석기로 보완 가능
    # 추출된 명사 리스트를 공백으로 결합하여 새로운 텍스트를 생성
    tokenized_text = " ".join(tokens)

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
    top_indices = sorted_indices[:max_keywords]

    # TF-IDF 점수가 0보다 큰 단어들만 키워드로 선택
    top_keywords = [feature_names[i] for i in top_indices if scores[i] > 0]

    return top_keywords


# if __name__ == "__main__":
#     # 테스트용 한국어 예시 문장
#     test_text = "서울 – 2025년 3월 18일, 서울시청은 오늘 오전 기자간담회를 통해 새로운 도시 재생 정책을 발표했다. "
#
#     keywords = extract_keywords(test_text, max_keywords=50)
#     print("핵심 키워드:", keywords)
