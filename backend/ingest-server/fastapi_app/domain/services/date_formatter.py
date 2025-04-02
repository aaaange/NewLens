import datetime


def format_date(published_at_str: str) -> str:
    """
    ISO 8601 형식의 날짜 문자열 (예: "2025-03-06T23:49:31.000000Z") 또는
    RFC 2822 형식의 날짜 문자열 (예: "Mon, 26 Sep 2016 07:50:00 +0900")을 받아서
    "YYYY-MM-DDTHH:MM:SS" 형식으로 변환하여 반환합니다.
    """
    if not published_at_str:
        return ""

    # 먼저 ISO 8601 형식 시도 (Z를 +00:00으로 치환)
    try:
        iso_str = published_at_str.replace("Z", "+00:00")
        dt = datetime.datetime.fromisoformat(iso_str)
        return dt.strftime("%Y-%m-%dT%H:%M:%S")
    except ValueError:
        pass

    # RFC 2822 형식 시도
    try:
        dt = datetime.datetime.strptime(published_at_str, "%a, %d %b %Y %H:%M:%S %z")
        return dt.strftime("%Y-%m-%dT%H:%M:%S")
    except ValueError:
        # 두 방식 모두 실패하면 원본 문자열 반환
        return published_at_str


# 예시 테스트
if __name__ == "__main__":
    test_str1 = "2025-03-06T23:49:31.000000Z"
    test_str2 = "Mon, 26 Sep 2016 07:50:00 +0900"
    print(format_date(test_str1))  # 예: "2025-03-06T23:49:31"
    print(format_date(test_str2))  # 예: "2016-09-26T07:50:00"
