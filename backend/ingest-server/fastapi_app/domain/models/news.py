from pydantic import BaseModel, Field
from datetime import datetime
import uuid
from typing import List, Optional


class News(BaseModel):
    id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    title: str
    description: str
    url: str
    published_at: str
    image_url: str
    categories: Optional[List[str]] = []
    keywords: Optional[List[str]] = []
    sentiment: int
    raw_data_ref: str
