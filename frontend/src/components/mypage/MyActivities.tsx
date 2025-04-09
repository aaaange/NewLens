import { useState } from 'react';
import NewsItem from '../worldDetail/NewsItem';
import { Bookmark } from 'lucide-react';

import {
  words,
  sentimentData,
  mentionData,
  newsData,
  description,
  videos,
} from '../worldDetail/MockData';
import NewsCarousel from './NewsCarousel';
import MyProfile from './MyProfile';

const safeData = {
  keywords: words,
  description: description,
  sentimentData: sentimentData,
  mentions: mentionData,
  articles: newsData,
  videos: videos,
};

const MyActivities = () => {
  const [bookmarks, setBookmarks] = useState<number[]>([]);

  const toggleBookmark = (index: number) => {
    setBookmarks(
      (prev) =>
        prev.includes(index)
          ? prev.filter((i) => i !== index) // 제거
          : [...prev, index] // 추가
    );
  };
  return (
    <div className="flex flex-col gap-10">
      <div>
        <MyProfile />
      </div>
      <div>
        <h2 className="text-2xl mb-4">최근 본 NEWS</h2>
        <NewsCarousel />
      </div>
    </div>
  );
};

export default MyActivities;
