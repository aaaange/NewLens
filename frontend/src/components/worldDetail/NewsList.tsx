import Flag from 'react-world-flags';
import NewsItem from './NewsItem';

const NewsList = ({ news, width, height, keyword, country_name }) => {
  return (
    <div className="gap-2" style={{ width: `${width}px`, height: `${height}px` }}>
      {/* 뉴스 리스트 헤더 */}
      <p className="flex">
        <Flag code="US" width="24" height="12" /> &nbsp;
        <span className="text-lg "> {country_name}</span>에서 본&nbsp;
        <span className="text-system-warning text-lg ">{keyword}</span>
        의 관련&nbsp;
        <span className="text-system-warning text-lg "> 뉴스</span>
      </p>

      {/* 뉴스 리스트 영역 */}
      <div className="space-y-2 p-2 border-2 border-gray-500 rounded-lg">
        {news.map((item, index) => (
          <NewsItem
            key={index} // React에서 리스트 렌더링 시 고유한 key 필요
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

export default NewsList;
