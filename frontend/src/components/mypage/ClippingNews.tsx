import { useState, useEffect } from 'react';
import NewsItem from '../worldDetail/NewsItem';
import { Trash } from 'lucide-react';

import {
  words,
  sentimentData,
  mentionData,
  newsData,
  description,
  videos,
} from '../worldDetail/MockData';
import { useScrapNews } from '../../hooks/useScrapNews';

const safeData = {
  keywords: words,
  description: description,
  sentimentData: sentimentData,
  mentions: mentionData,
  articles: newsData,
  videos: videos,
};

const ClippingNews = () => {
  const [clippedArticles, setClippedArticles] = useState(safeData.articles);

  const { newsList, loading, fetchScrapNews, removeScrapNews } = useScrapNews();

  // ~~~~~~~~~ 목 데이터 ~~~~~~~~~~
  // const deleteArticle = (index: number) => {
  //   setClippedArticles((prev) => prev.filter((_, i) => i !== index));
  // };
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  // 뉴스 삭제
  const deleteArticle = async (newsId: string) => {
    try {
      await removeScrapNews(newsId);
      await fetchScrapNews(); // 삭제 후 목록 갱신
    } catch (err) {
      console.error('뉴스 삭제 실패:', err);
    }
  };

  useEffect(() => {
    fetchScrapNews(); // 기본 5개
  }, []);

  if (loading) return <p>로딩 중...</p>;

  return (
    <div>
      <h2 className="text-2xl">스크랩 NEWS</h2>
      <div className="rounded-lg">
        {/* {clippedArticles.map((item, index) => ( */}
        {newsList.map((item, index) => (
          <div key={index} className="flex items-center gap-4">
            {/* 삭제 버튼 */}
            <button
              // onClick={() => deleteArticle(index)}
              onClick={() => deleteArticle(item.news_id)}
              className="cursor-pointer"
            >
              <Trash
                size={20}
                className="text-red-400 hover:text-system-danger transition-colors"
              />
            </button>
            <div className="w-full">
              <NewsItem
                title={item.title}
                url={item.url}
                // publishedDate={item.published_at}
                publishedDate={item.public_at}
                imageUrl={item.image_url}
              />
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ClippingNews;
