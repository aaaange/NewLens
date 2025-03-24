# import datetime
#
#
# def format_date(published_at_str: str) -> str:
#     """
#     ISO 8601 형식의 날짜 문자열 (예: "2025-03-06T23:49:31.000000Z")을 받아서
#     "YYYY-MM-DDTHH:MM:SS" 형식으로 변환하여 반환합니다.
#     """
#     if not published_at_str:
#         return ""
#
#     # "Z"를 "+00:00"으로 치환해서 Python이 인식할 수 있는 ISO 8601 형식으로 만듭니다.
#     iso_str = published_at_str.replace("Z", "+00:00")
#
#     try:
#         dt = datetime.datetime.fromisoformat(iso_str)
#         # 변환 형식: "YYYY-MM-DDTHH:MM:SS"
#         return dt.strftime("%Y-%m-%dT%H:%M:%S")
#     except ValueError:
#         # 파싱 실패 시 원본 문자열 반환
#         return published_at_str
