import { useState } from 'react';
import NewsItem from '../worldDetail/NewsItem';
import { Bookmark, Mail, MailOpen } from 'lucide-react';
import { formatDate } from '../../utils/formatDateUtils'

import {
  words,
  sentimentData,
  mentionData,
  newsData,
  description,
  videos,
} from '../worldDetail/MockData';

const safeData = {
  keywords: words,
  description: description,
  sentimentData: sentimentData,
  mentions: mentionData,
  articles: newsData,
  videos: videos,
};

const RecommendedArticle = () => {
  const [bookmarks, setBookmarks] = useState<number[]>([]);
  const [readItems, setReadItems] = useState<number[]>([]);

  // 북마크 토글
  const toggleBookmark = (index: number) => {
    setBookmarks(
      (prev) =>
        prev.includes(index)
          ? prev.filter((i) => i !== index) // 제거
          : [...prev, index] // 추가
    );
  };

  // 읽음 표시
  const markAsRead = (index: number) => {
    setReadItems((prev) => (prev.includes(index) ? prev : [...prev, index]));
  };

  // 날짜별로 그룹
  const groupedArticles = safeData.articles.reduce(
    (acc: Record<string, any[]>, article, index) => {
      const date = formatDate(article.published_at);
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
              <div key={item._index} className="flex items-center gap-4 mb-3">
                {/* 북마크 버튼 */}
                <button
                  onClick={() => toggleBookmark(item._index)}
                  className="cursor-pointer"
                >
                  <Bookmark
                    size={20}
                    className={`transition-colors ${
                      bookmarks.includes(item._index)
                        ? 'fill-yellow-400 text-yellow-400'
                        : 'text-gray-300'
                    }`}
                  />
                </button>

                {/* 뉴스 아이템 */}
                <div
                  className="w-full cursor-pointer"
                  onClick={() => markAsRead(item._index)}
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
                  {readItems.includes(item._index) ? (
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
