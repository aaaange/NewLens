from transformers import M2M100ForConditionalGeneration, M2M100Tokenizer


def translate_text(text: str, target_language: str = "ko"):
    # 1. 모델 및 토크나이저 불러오기
    model_name = "facebook/m2m100_418M"
    tokenizer = M2M100Tokenizer.from_pretrained(model_name)
    model = M2M100ForConditionalGeneration.from_pretrained(model_name)

    tokenizer.src_lang = target_language

    # 문장 토큰화
    encoded = tokenizer(text, return_tensors="pt")

    # 타겟 언어를 'ko'로 설정 (forced_bos_token_id 사용)
    generated_tokens = model.generate(
        **encoded, forced_bos_token_id=tokenizer.get_lang_id("ko")  # 한국어
    )

    # 번역 결과 디코딩
    result = tokenizer.batch_decode(generated_tokens, skip_special_tokens=True)[0]

    return result
