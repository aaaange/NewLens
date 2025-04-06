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
      <div className="flex flex-col gap-4">
        <h1 className="text-2xl">나의 활동</h1>
        <div className="flex w-full border-1 rounded-2xl p-4">
          <img src="/assets/images/blank_profile.png" className="w-24 h-24" />
          <div className="pl-4 w-full">
            <div className="flex gap-2 items-center">
              <div className='text-lg'>열정_두배</div>
              <img src="/assets/images/update_pen.png" className="w-2 h-4" />
            </div>
            <div className="flex justify-between">
              <div className="flex gap-4">
                <div>
                  <div className="text-primary-500">이름</div>
                  <div className="text-primary-500">플랫폼</div>
                  <div className="text-primary-500">생년월일</div>
                </div>
                <div>
                  <div>김싸피</div>
                  <div>KAKAO</div>
                  <div>2000.01.01</div>
                </div>
              </div>
              <div className="flex items-end">
                <button className='transition-transform duration-300 ease-in-out hover:scale-105 cursor-pointer'>
                  <div className=" border-1 rounded-lg p-1.5 text-sm border-system-danger text-system-danger">
                    탈퇴하기
                  </div>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div>
        <h2 className="text-2xl">최근 본 NEWS</h2>
        <NewsCarousel />
        <div className=" rounded-lg">
          {safeData.articles.map((item, index) => (
            <div key={index} className="flex items-center gap-4">
              {/* 북마크 버튼 */}
              <button
                onClick={() => toggleBookmark(index)}
                className="cursor-pointer"
              >
                <Bookmark
                  size={20}
                  className={`transition-colors ${bookmarks.includes(index) ? 'fill-amount-300 text-amount-300' : 'text-gray-300'}`}
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
    </div>
  );
};

export default MyActivities;
