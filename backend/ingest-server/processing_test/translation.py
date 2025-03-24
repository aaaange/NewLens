# from transformers import M2M100ForConditionalGeneration, M2M100Tokenizer
#
#
# def translate(src_text):
#     # 1. 모델 및 토크나이저 불러오기
#     model_name = "facebook/m2m100_418M"
#     tokenizer = M2M100Tokenizer.from_pretrained(model_name)
#     model = M2M100ForConditionalGeneration.from_pretrained(model_name)
#
#     # 2. G20 주요 언어별 예시 문장 (dict 형태)
#     # G20 뉴스 기사 예시 문장 (간단한 뉴스 헤드라인 또는 첫 문장)
#     # samples = {
#     #     "en": "Former President Donald Trump, during a contentious speech at a large rally, cautioned that without immediate radical reforms, the current government's policies might trigger a severe economic downturn and intensify political instability.",
#     #     "es": "El expresidente Donald Trump, durante un polémico discurso en un gran mitin, advirtió que sin reformas radicales inmediatas, las políticas del gobierno actual podrían desencadenar una grave recesión económica y aumentar la inestabilidad política.",
#     #     "fr": "L'ancien président Donald Trump, lors d'un discours controversé dans un vaste rassemblement, a averti que sans réformes radicales immédiates, les politiques du gouvernement actuel pourraient provoquer une forte récession économique et aggraver l'instabilité politique.",
#     #     "pt": "O ex-presidente Donald Trump, durante um discurso polêmico em um grande comício, alertou que sem reformas radicais imediatas, as políticas do governo atual poderiam desencadear uma severa recessão econômica e intensificar a instabilidade política.",
#     #     "de": "Der ehemalige Präsident Donald Trump warnte während einer umstrittenen Rede auf einer großen Kundgebung, dass ohne sofortige radikale Reformen die aktuellen Regierungsmaßnahmen zu einem schweren wirtschaftlichen Abschwung und zu verstärkter politischer Instabilität führen könnten.",
#     #     "it": "L'ex presidente Donald Trump, durante un discorso controverso in un grande raduno, ha avvertito che senza riforme radicali immediate, le politiche dell'attuale governo potrebbero innescare una grave recessione economica e intensificare l'instabilità politica.",
#     #     "ru": "Бывший президент Дональд Трамп во время противоречивой речи на масштабном митинге предупредил, что без немедленных радикальных реформ политики нынешнего правительства могут привести к серьёзному экономическому спаду и усилению политической нестабильности.",
#     #     "ar": "حذر الرئيس السابق دونالد ترامب، خلال خطاب مثير للجدل في تجمع كبير، من أن سياسات الحكومة الحالية قد تؤدي إلى ركود اقتصادي حاد وتصاعد عدم الاستقرار السياسي إذا لم تُنفذ إصلاحات جذرية فورية.",
#     #     "zh": "前总统唐纳德·特朗普在一场备受争议的集会上发表讲话，警告说如果不立即实施激进的改革，当前政府的政策可能会引发严重的经济衰退并加剧政治不稳定。",
#     #     "ja": "元大統領ドナルド・トランプは、大規模な集会での物議を醸すスピーチの中で、即時の抜本的改革が行われなければ、現政権の政策が深刻な経済不況と政治的不安定を引き起こす可能性があると警告しました。",
#     #     "hi": "पूर्व राष्ट्रपति डोनाल्ड ट्रम्प ने एक विवादास्पद भाषण में चेतावनी दी कि तत्काल कठोर सुधारों के बिना, वर्तमान सरकार की नीतियां गंभीर आर्थिक मंदी और राजनीतिक अस्थिरता को जन्म दे सकती हैं।",
#     #     "id": "Mantan Presiden Donald Trump, dalam sebuah pidato kontroversial di sebuah rapat besar, memperingatkan bahwa tanpa reformasi radikal segera, kebijakan pemerintah saat ini dapat memicu penurunan ekonomi yang parah dan meningkatkan ketidakstabilan politik.",
#     # }
#
#     # 3. 각 언어 문장을 한국어로 번역
#     for lang_code, text in src_text.items():
#         # 소스 언어 설정
#         tokenizer.src_lang = lang_code
#
#         # 문장 토큰화
#         encoded = tokenizer(text, return_tensors="pt")
#
#         # 타겟 언어를 'ko'로 설정 (forced_bos_token_id 사용)
#         generated_tokens = model.generate(
#             **encoded, forced_bos_token_id=tokenizer.get_lang_id("ko")  # 한국어
#         )
#
#         # 번역 결과 디코딩
#         result = tokenizer.batch_decode(generated_tokens, skip_special_tokens=True)[0]
#
#         return result
#
#         # print(f"[{lang_code}] {text}  ->  [ko] {result}")
