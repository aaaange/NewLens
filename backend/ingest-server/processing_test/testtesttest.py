# from langdetect import detect, detect_langs
#
# # G20 주요 언어별 샘플 텍스트
# sample_texts = {
#     "en": "Hello, how are you?",
#     "es": "Hola, ¿cómo estás?",
#     "pt": "Olá, como você está?",
#     "fr": "Bonjour, comment allez-vous?",
#     "zh": "你好，你怎么样？",
#     "de": "Hallo, wie geht es dir?",
#     "hi": "नमस्ते, आप कैसे हैं?",
#     "id": "Halo, apa kabar?",
#     "it": "Ciao, come stai?",
#     "ja": "こんにちは、お元気ですか？",
#     "ru": "Привет, как дела?",
#     "ar": "مرحبا، كيف حالك؟",
#     "ko": "안녕하세요, 어떻게 지내세요?",
#     "tr": "Merhaba, nasılsın?",
# }
#
# # 각 언어에 대해 감지 결과 출력
# for lang_code, text in sample_texts.items():
#     detected = detect(text)
#     detected_probs = detect_langs(text)
#     print(f"\n샘플 언어 코드: {lang_code}")
#     print(f"텍스트: {text}")
#     print(f"감지된 언어: {detected}")
#     print("언어별 확률:")
#     for prob in detected_probs:
#         print(f"  {prob.lang}: {prob.prob:.2f}")
