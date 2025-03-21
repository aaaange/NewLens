import langdetect


def detect_language(text: str) -> str:
    """
    주어진 텍스트의 언어를 감지하여 ISO 639-1 코드로 반환합니다.
    예를 들어, "Hello, world!" -> "en", "안녕하세요" -> "ko"
    """
    try:
        language = langdetect.detect(text)
        return language
    except Exception as e:
        # 언어 감지 실패 시 기본 언어로 'en'을 반환하거나 예외 처리를 수행할 수 있습니다.
        return "en"
