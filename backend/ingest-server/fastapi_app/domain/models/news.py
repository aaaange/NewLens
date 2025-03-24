# from pydantic import BaseModel, Field
# from typing import List, Optional
# from datetime import datetime
# import uuid
#
#
# class News(BaseModel):
#     id: str = Field(default_factory=lambda: str(uuid.uuid4()))
#     title: str
#     content: str
#     source: Optional[str] = None
#     published_at: datetime
#     sentiment: Optional[float] = None  # 감정 분석 점수
#     keywords: Optional[List[str]] = []  # 추출된 키워드 리스트
#     translation: Optional[str] = None  # 번역 결과
#     country: Optional[str] = None  # 나라 추정 결과
#     category: Optional[str] = None  # 카테고리 분류 결과
