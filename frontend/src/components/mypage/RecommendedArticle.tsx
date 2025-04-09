import { useEffect, useState } from 'react';
import NewsItem from '../worldDetail/NewsItem';
import { Bookmark, Mail, MailOpen } from 'lucide-react';
import { formatDate } from '../../utils/formatDateUtils';

import {
  words,
  sentimentData,
  mentionData,
  newsData,
  description,
  videos,
} from '../worldDetail/MockData';
import { useScrapNews, News } from '../../hooks/useMypageNews';
import GlobalSpinner from '../common/GlobalSpinner';

const safeData = {
  keywords: words,
  description: description,
  sentimentData: sentimentData,
  mentions: mentionData,
  articles: newsData,
  videos: videos,
};

const RecommendedArticle = () => {
  // const [readItems, setReadItems] = useState<string[]>([]);
  const [articles, setArticles] = useState<News[]>([]);

  const {
    recommendNewsList,
    loading,
    error,
    scrapNews,
    fetchRecommendNews,
    fetchtAccessLog,
  } = useScrapNews();

  // 뉴스 스크랩
  const toggleScrap = async (news_id: string) => {
    try {
      const res = await scrapNews(news_id);
      if (!res) return;

      const { is_scrap } = res;

      setArticles((prev) =>
        prev.map((item) =>
          item.news_id === news_id ? { ...item, is_scrap } : item
        )
      );
    } catch (err) {
      console.error('스크랩 상태 변경 실패:', err);
    }
  };

  useEffect(() => {
    fetchRecommendNews();
  }, []);

  useEffect(() => {
    setArticles(recommendNewsList ?? []); // API에서 받은 데이터로 초기화
  }, [recommendNewsList]);

  if (loading) return <GlobalSpinner />;

  // 뉴스 클릭 시 읽음 처리 (visited_at 반영)
  const handleNewsClick = async (news_id: string) => {
    try {
      const res = await fetchtAccessLog(news_id);

      // 서버에서 visited_at 반환 시 아래와 같이 설정
      const visited_at = res?.visited_at ?? new Date().toISOString();

      setArticles((prev) =>
        prev.map((item) =>
          item.news_id === news_id ? { ...item, visited_at } : item
        )
      );
    } catch (err) {
      console.error('접근 로그 저장 실패:', err);
    }
  };

  // 날짜별로 그룹
  const groupedArticles = articles.reduce(
    (acc: Record<string, any[]>, article, index) => {
      const date = formatDate(article.published_at, 'day');
      if (!acc[date]) acc[date] = [];
      acc[date].push({ ...article, _index: index });
      return acc;
    },
    {}
  );

  return (
    <div>
      <div>
        <h1 className="text-2xl mb-2">추천 기사</h1>
        <p className="body-small text-primary-500">
          읽었던 기사를 기반으로 관심사 기사를 추천해드려요!
        </p>
      </div>
      {articles.length === 0 ? (
        // 뉴스가 없을 때 메시지 표시
        <div className="flex flex-col items-center justify-center min-h-[calc(100vh-280px)] py-12 gap-2">
          <p className="headline-xlarge">😢</p>
          <p className="text-gray-500">아직 추천 뉴스가 없어요</p>
        </div>
      ) : (
        // 뉴스가 있을 때 기존 UI 렌더
        <div className="rounded-lg space-y-6">
          {Object.entries(groupedArticles).map(([date, articles]) => (
            <div key={date}>
              {/* 날짜 헤더 */}
              <h2 className="text-2xl font-semibold mb-3">{date}</h2>

              {/* 해당 날짜의 기사들 */}
              {articles.map((item) => (
                <div
                  key={item.news_id}
                  className="flex items-center gap-4 mb-3"
                >
                  {/* 북마크 버튼 */}
                  <button
                    onClick={() => toggleScrap(item.news_id)}
                    className="cursor-pointer"
                  >
                    <Bookmark
                      size={20}
                      className={`transition-colors duration-200 ${
                        item.is_scrap
                          ? 'fill-yellow-400 text-yellow-400'
                          : 'text-gray-300'
                      }`}
                    />
                  </button>

                  {/* 뉴스 아이템 */}
                  <div className="w-full cursor-pointer">
                    <NewsItem
                      title={item.title}
                      url={item.url}
                      published_at={item.published_at}
                      image_url={item.image_url}
                      onClick={() => handleNewsClick(item.news_id)}
                    />
                  </div>

                  {/* 읽음 여부 */}
                  <div>
                    {item.visited_at && item.visited_at !== '' ? (
                      <MailOpen size={20} className="text-blue-500" />
                    ) : (
                      <Mail size={20} className="text-gray-400" />
                    )}
                  </div>
                </div>
              ))}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default RecommendedArticle;
