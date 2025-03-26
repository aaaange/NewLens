import NewsItem from '../worldDetail/NewsItem';

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

const MyActivities = () => {
  return (
    <div className=" ">
      <h1>나의 활동</h1>
      <div className="flex w-full border-1">
        <img src="/assets/images/blank_profile.png" />
        <div>
          <div className="flex">
            <div>열정_두배</div>
            <img src="/assets/images/update_pen.png" className="w-2.5 h-4" />
          </div>
          <div className="flex">
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
          <div className="flex justify-end">
            <div className=" border-1 border-system-danger text-system-danger">
              탈퇴하기
            </div>
          </div>
        </div>
      </div>
      <h2>최근 본 NEWS</h2>
      <div className=" rounded-lg">
        {safeData.articles.map((item, index) => (
          <NewsItem
            // key={item.url}
            key={index}
            title={item.title} // 뉴스 제목
            url={item.url} // 뉴스 URL
            publishedDate={item.published_date} // 뉴스 발행 날짜
            imageUrl={item.image_url} // 뉴스 이미지 URL
          />
        ))}
      </div>
    </div>
  );
};

export default MyActivities;
