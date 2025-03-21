from transformers import pipeline

# 후보 카테고리 목록
candidate_labels = [
    "general",
    "science",
    "sports",
    "business",
    "health",
    "entertainment",
    "tech",
    "politics",
    "food",
    "travel",
]

# Zero-shot 분류 파이프라인 생성 (facebook/bart-large-mnli 모델 사용)
classifier = pipeline("zero-shot-classification", model="facebook/bart-large-mnli")


def classify_text(text: str, multi_label: bool = True, max_categories: int = 3) -> list:
    """
    주어진 텍스트를 zero-shot 분류 모델을 사용하여 후보 카테고리 중에서 분류합니다.
    multi_label=True로 설정하면 텍스트가 여러 카테고리에 속할 수 있다고 가정하며,
    max_categories에 따라 최대 지정된 개수의 카테고리만 반환합니다.
    """
    result = classifier(text, candidate_labels, multi_label=multi_label)
    # result["labels"]는 점수가 높은 순으로 정렬된 카테고리 목록입니다.
    return result["labels"][:max_categories]


# if __name__ == "__main__":
#     # 뉴스 기사 형식의 예시 텍스트
#     test_text = (
#         "서울 – 2025년 3월 18일, 서울시청은 오늘 오전 기자간담회를 통해 "
#         "새로운 도시 재생 정책을 발표했다. 이번 정책은 낙후된 도심 지역을 활성화하고, "
#         "청년 창업 지원과 공공 주택 공급 확대를 목표로 하고 있다. "
#         "정부와 협력하여 추진하는 이 정책은 경제 활성화와 지역 균형 발전에 크게 기여할 것으로 기대된다. "
#         "전문가들은 이번 정책이 향후 5년간 서울시의 도시 경쟁력을 높이는 데 중요한 역할을 할 것으로 분석했다."
#     )
#
#     categories = classify_text(
#         test_text, candidate_labels, multi_label=True, max_categories=3
#     )
#     print("분류된 카테고리:", categories)
