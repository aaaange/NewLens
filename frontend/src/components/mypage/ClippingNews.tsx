import { useState } from 'react';
import NewsItem from '../worldDetail/NewsItem';
import { Bookmark, Trash } from 'lucide-react';

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

const ClippingNews = () => {
  const [clippedArticles, setClippedArticles] = useState(safeData.articles);

  const deleteArticle = (index: number) => {
    setClippedArticles((prev) => prev.filter((_, i) => i !== index));
  };

  return (
    <div>
      <h2 className="text-2xl">스크랩 NEWS</h2>
      <div className="rounded-lg">
        {clippedArticles.map((item, index) => (
          <div key={index} className="flex items-center gap-4">
            {/* 삭제 버튼 */}
            <button
              onClick={() => deleteArticle(index)}
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
                publishedDate={item.published_at}
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
