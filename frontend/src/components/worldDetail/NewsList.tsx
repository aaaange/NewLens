import Flag from 'react-world-flags';
import NewsItem from './NewsItem';
import { useScrapNews } from '../../hooks/useMypageNews';

interface NewsItemType {
  news_id: string;
  title: string;
  url: string;
  published_at: string;
  image_url: string;
}

interface NewsListProps {
  news: NewsItemType[];
  width: number;
  keyword: string;
  keyword_mind: string;
  country_name: string;
  country_code: string;
  handleModalOpen: (country: string) => void; // 모달 열기 함수
}
const NewsList = ({
  news,
  width,
  keyword,
  keyword_mind,
  country_name,
  country_code,
  handleModalOpen,
}: NewsListProps) => {
  const { fetchtAccessLog } = useScrapNews();
  const Flags = Flag as any; // Flag 컴포넌트의 타입을 any로 설정

  const headerString = [keyword, keyword_mind]
    .filter((item) => item && item.trim() !== '') // 빈 문자열 또는 undefined/null 제거
    .join(' > '); // ' > '로 연결
  return (
    <div className="flex flex-col gap-2" style={{ width: `${width}px` }}>
      {/* 뉴스 리스트 헤더 */}
      <div className="flex flex-wrap justify-between ">
        <p className="flex items-center flex-wrap">
          <Flags code={country_code} width="24" height="12" /> &nbsp;
          <span className="text-lg "> {country_name}</span>에서 본&nbsp;
          <span className="text-amount-300 text-lg ">{headerString}</span>의
          관련&nbsp;
          <span className="text-amount-300 text-lg "> 뉴스</span>
        </p>
        <div className="ml-auto">
          <div
            onClick={() => handleModalOpen(country_code)}
            className="p-2 cursor-pointer"
          >
            + 더보기
          </div>
        </div>
      </div>

      {/* 뉴스 리스트 영역 */}
      <div className="space-y-2 border-2 border-gray-500 rounded-lg">
        {news.map((item, index) => (
          <NewsItem
            // key={item.url}
            key={index}
            title={item.title} // 뉴스 제목
            url={item.url} // 뉴스 URL
            published_at={item.published_at} // 뉴스 발행 날짜
            image_url={item.image_url} // 뉴스 이미지 URL
            onClick={() => fetchtAccessLog(item.news_id)}
          />
        ))}
      </div>
    </div>
  );
};

export default NewsList;
