
### 사용한 외부 서비스 목록 및 용도

| 서비스 이름                      | 용도                                | 주요 사용 위치              |
| -------------------------------- | ----------------------------------- | --------------------------- |
| **Google Cloud Translation API** | 뉴스 제목 번역 (요약 전시용)        | 통계 서버 (`GoogleClient`)  |
| **Kakao 이미지 검색 API**        | 뉴스 썸네일 이미지 검색             | 통계 서버 (`KakaoClient`)   |
| **YouTube Data API**             | 국가별 키워드 관련 영상 검색        | 통계 서버 (`YouTubeClient`) |
| **DockerHub**                    | Docker 이미지 저장소 (CI/CD 배포용) | Jenkins Pipeline            |
| **GitLab**                       | 소스 저장소 / CI 트리거             | Jenkins                     |

---
