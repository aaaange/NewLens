import torch
from transformers import AutoTokenizer, AutoModelForSequenceClassification

label_map = {
    0: "general",
    1: "science",
    2: "sports",
    3: "business",
    4: "health",
    5: "entertainment",
    6: "tech",
    7: "politics",
    8: "food",
    9: "travel",
}


def classify_category(text: str) -> str:
    # 저장된 모델과 토크나이저 로드 (trust_remote_code=True 필요)
    model_dir = "./my_finetuned_kobert"
    tokenizer = AutoTokenizer.from_pretrained(model_dir, trust_remote_code=True)
    model = AutoModelForSequenceClassification.from_pretrained(
        model_dir, trust_remote_code=True
    )

    # 테스트 데이터를 토크나이징 (최대 길이 128, 패딩 적용)
    inputs = tokenizer(
        text, padding=True, truncation=True, max_length=128, return_tensors="pt"
    )

    # 모델 평가 모드로 전환 후 추론 진행
    model.eval()
    with torch.no_grad():
        outputs = model(**inputs)
        logits = outputs.logits
        # 각 문장별로 가장 높은 점수를 가진 클래스의 인덱스를 예측
        predictions = torch.argmax(logits, dim=-1)
        predicted_index = predictions.item()  # 단일 텍스트의 경우 하나의 값

    # 예측 인덱스를 해당 카테고리 문자열로 변환
    predicted_category = label_map.get(predicted_index, "unknown")
    return predicted_category
