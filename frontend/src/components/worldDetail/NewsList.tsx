import Flag from 'react-world-flags';
import NewsItem from './NewsItem';
import { NewsItemType } from '../../pages/WorldDetail';

interface NewsListProps {
  news: NewsItemType[];
  width: number;
  keyword: string;
  country_name: string;
  country_code: string;
}
const NewsList = ({
  news,
  width,
  keyword,
  country_name,
  country_code,
}: NewsListProps) => {
  return (
    <div className="gap-2" style={{ width: `${width}px` }}>
      {/* 뉴스 리스트 헤더 */}
      <div className="flex justify-between ">
        <p className="flex items-center">
          <Flag code={country_code} width="24" height="12" /> &nbsp;
          <span className="text-lg "> {country_name}</span>에서 본&nbsp;
          <span className="text-system-warning text-lg ">{keyword}</span>의
          관련&nbsp;
          <span className="text-system-warning text-lg "> 뉴스</span>
        </p>
        <a href="" className="p-2">
          + 더보기
        </a>
      </div>

      {/* 뉴스 리스트 영역 */}
      <div className="space-y-2 border-2 border-gray-500 rounded-lg">
        {news.map((item, index) => (
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

export default NewsList;
