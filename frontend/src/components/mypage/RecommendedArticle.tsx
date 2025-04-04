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
import { useScrapNews } from '../../hooks/useMypageNews';

const safeData = {
  keywords: words,
  description: description,
  sentimentData: sentimentData,
  mentions: mentionData,
  articles: newsData,
  videos: videos,
};

const RecommendedArticle = () => {
  const [bookmarks, setBookmarks] = useState<string[]>([]);
  const [readItems, setReadItems] = useState<string[]>([]);

  const { recommendNewsList, loading, error, scrapNews, fetchRecommendNews } =
    useScrapNews();

  // 뉴스 스크랩
  const BookmarkedNews = async (newsId: string) => {
    try {
      await scrapNews(newsId);
      await fetchRecommendNews();
    } catch (err) {
      console.error('뉴스 스크랩 실패:', err);
    }
  };

  useEffect(() => {
    fetchRecommendNews();
  }, []);

  if (loading) return <p>Loading...</p>;

  // 북마크 토글
  const toggleBookmark = (newsId: string) => {
    setBookmarks((prev) =>
      prev.includes(newsId)
        ? prev.filter((id) => id !== newsId)
        : [...prev, newsId]
    );
  };
  // 읽음 표시
  const markAsRead = (newsId: string) => {
    setReadItems((prev) => (prev.includes(newsId) ? prev : [...prev, newsId]));
  };

  // 날짜별로 그룹
  const groupedArticles = recommendNewsList.reduce(
    (acc: Record<string, any[]>, article, index) => {
      const date = formatDate(article.public_at, 'day');
      if (!acc[date]) acc[date] = [];
      acc[date].push({ ...article, _index: index });
      return acc;
    },
    {}
  );

  return (
    <div>
      <div className="rounded-lg space-y-6">
        {Object.entries(groupedArticles).map(([date, articles]) => (
          <div key={date}>
            {/* 날짜 헤더 */}
            <h2 className="text-2xl font-semibold mb-3">{date}</h2>

            {/* 해당 날짜의 기사들 */}
            {articles.map((item) => (
              <div key={item.news_id} className="flex items-center gap-4 mb-3">
                {/* 북마크 버튼 */}
                <button
                  onClick={() => toggleBookmark(item.news_id)}
                  className="cursor-pointer"
                >
                  <Bookmark
                    size={20}
                    className={`transition-colors ${
                      bookmarks.includes(item.news_id)
                        ? 'fill-yellow-400 text-yellow-400'
                        : 'text-gray-300'
                    }`}
                  />
                </button>

                {/* 뉴스 아이템 */}
                <div
                  className="w-full cursor-pointer"
                  onClick={() => markAsRead(item.news_id)}
                >
                  <NewsItem
                    title={item.title}
                    url={item.url}
                    publishedDate={item.published_at}
                    imageUrl={item.image_url}
                  />
                </div>

                {/* 읽음 여부 */}
                <div>
                  {readItems.includes(item.news_id) ? (
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
    </div>
  );
};

export default RecommendedArticle;
