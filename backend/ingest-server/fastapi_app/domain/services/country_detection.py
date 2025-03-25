from urllib.parse import urlparse
from fastapi_app.infrastructure.persistence.mysql_client import MySQLClient

# TLD 기반 국가 매핑 (소문자 키)
tld_mapping = {
    "ar": "AR",
    "au": "AU",
    "br": "BR",
    "ca": "CA",
    "cn": "CN",
    "de": "DE",
    "eu": "EU",
    "fr": "FR",
    "gb": "GB",
    "in": "IN",
    "id": "ID",
    "it": "IT",
    "jp": "JP",
    "kr": "KR",
    "mx": "MX",
    "ru": "RU",
    "sa": "SA",
    "za": "ZA",
    "tr": "TR",
    "us": "US",
    # 미디어 도메인 예외 처리
    "rt": "RU",
    "sputnik": "RU",
    "bbc": "GB",
    "guardian": "GB",
    "xinhuanet": "CN",
    "cgtn": "CN",
    "cnn": "US",
    "nytimes": "US",
    "foxnews": "US",
    "cbc": "CA",
    "abc": "AU",
    "theaustralian": "AU",
    "elpais": "ES",
    "elmundo": "ES",
    "thehindu": "IN",
    "timesofindia": "IN",
    "reuters": "US",
    "bloomberg": "US",
    "aljazeera": "QA",
    "dw": "DE",
    "lemonde": "FR",
    "spiegel": "DE",
    "uk": "GB",
}


def get_country_by_tld(source: str) -> str:
    """
    source 문자열에서 TLD 기반으로 국가 코드를 추출합니다.
    예: "arabic.rt.com" → "rt"를 매핑 → "RU"
    """
    parsed = urlparse(source)
    domain = parsed.netloc if parsed.netloc else source
    parts = domain.split(".")
    for part in parts:
        part_lower = part.lower()
        if part_lower in tld_mapping:
            return tld_mapping[part_lower]
    return "Unknown"


def determine_source_country(source: str) -> str:
    """
    MySQL에서 먼저 source_country 값을 조회하고, 없으면 TLD 매핑을 통해 국가 코드를 반환합니다.
    """
    mysql_client = MySQLClient()
    country = mysql_client.fetch_source_country(source)
    if country:
        mysql_client.close()
        return country

    country = get_country_by_tld(source)
    mysql_client.close()
    return country
