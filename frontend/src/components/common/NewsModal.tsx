import { useEffect, useState } from 'react';
import { Bookmark } from 'lucide-react';
import Flag from 'react-world-flags';
import { getNewsListForModalApi } from '../../services/api/worldService';
import { getCountryName } from '../../utils/countryUtils';
import { useNavigate } from 'react-router-dom';
import { formatDate } from '../../utils/formatDateUtils';
import Pagination from './Pagination';
import classes from './Common.module.css';
import SentimentDissatisfiedIcon from '@mui/icons-material/SentimentDissatisfied';
interface itemPropsType {
  date: string;
  title: string;
  image: string;
  sentiment: string;
  tags: string[];
  bookmarked: boolean;
  onToggleBookmark: () => void;
}

const NewsItem = ({
  date,
  title,
  image,
  sentiment,
  tags,
  bookmarked,
  onToggleBookmark,
}: itemPropsType) => {
  const getSentimentStyle = (sentiment: string) => {
    const sentimentValue = parseInt(sentiment, 10); // 문자열을 숫자로 변환
    if (sentimentValue >= 0 && sentimentValue <= 33) {
      return 'bg-negative text-white'; // 부정적 스타일
    } else if (sentimentValue >= 34 && sentimentValue <= 66) {
      return 'bg-neutral text-white'; // 중립적 스타일
    } else if (sentimentValue >= 67 && sentimentValue <= 100) {
      return 'bg-positive text-white'; // 긍정적 스타일
    } else {
      return 'bg-gray-500 text-white'; // 예외 처리 스타일
    }
  };

  const getSentimentText = (sentiment: string) => {
    const sentimentValue = parseInt(sentiment, 10); // 문자열을 숫자로 변환
    if (sentimentValue >= 0 && sentimentValue <= 33) {
      return '부정적';
    } else if (sentimentValue >= 34 && sentimentValue <= 66) {
      return '중립적';
    } else if (sentimentValue >= 67 && sentimentValue <= 100) {
      return '긍정적';
    } else {
      return '알 수 없음'; // 예외 처리
    }
  };

  return (
    <div className="flex items-start md:items-center mb-2 hover:bg-gray-50 p-2 rounded transition-colors">
      <img className="w-20 h-14 mr-4 object-fit" src={image} alt={title} />
      <div className="flex-grow">
        <p className="text-slate-400 text-xs mb-1">
          {formatDate(date, 'full')}
        </p>
        <h3 className="text-black text-sm caption-medium w-8/9 mb-2">
          {title}
        </h3>
        <div className="flex flex-wrap gap-2">
          <span
            className={`px-2 py-1 rounded-full text-xs ${getSentimentStyle(sentiment)}`}
          >
            #{getSentimentText(sentiment)}
          </span>
          {tags.map((tag, index) => (
            <span
              key={index}
              className="px-2 py-1 bg-gray-100 rounded-full text-zinc-500 text-xs"
            >
              #{tag}
            </span>
          ))}
        </div>
      </div>
      <button
        className="flex items-center justify-center rounded-full cursor-pointer"
        onClick={(e) => {
          e.stopPropagation();
          onToggleBookmark();
        }}
        aria-label={bookmarked ? '북마크 제거' : '북마크 추가'}
      >
        <Bookmark
          size={18}
          className={
            bookmarked ? 'fill-amount-300 text-amount-300' : 'text-gray-300'
          }
        />
      </button>
    </div>
  );
};

// category: category,
// period: period,
// keyword: keyword,
// 'keyword-mind': keyword_mind,
// 'keyword-cloud': keyword_cloud,
// country: country,
interface propsType {
  category: string;
  period: number;
  keyword: string;
  keyword_mind: string;
  keyword_cloud: string;
  country: string;
  handleModalClose?: () => void;
}
const NewsModal = ({
  category,
  period,
  keyword,
  keyword_mind,
  keyword_cloud,
  country,
  handleModalClose,
}: propsType) => {
  const [currentPage, setCurrentPage] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  interface NewsItemType {
    news_id: string;
    published_at: string;
    title: string;
    image_url: string;
    keywords: string[];
    url: string;
  }

  const [newsItems, setNewsItems] = useState<NewsItemType[]>([]);
  const [hasNext, setHasNext] = useState(false);
  const [hasPrevious, setHasPrevious] = useState(false);
  const itemsPerPage = 5;

  const fetchNewsList = async () => {
    try {
      const params = {
        category: category,
        period: period,
        keyword: keyword,
        keyword_mind: keyword_mind,
        keyword_cloud: keyword_cloud,
        country: country,
        page: currentPage,
        size: itemsPerPage,
        is_korea: false,
      };
      const response = await getNewsListForModalApi(params);
      console.log(response.data);
      setNewsItems(response.data.news);
      setCurrentPage(response.data.page);
      setTotalPages(response.data.totalPages);
      setTotalElements(response.data.totalElements);
      setHasNext(response.data.hasNext);
      setHasPrevious(response.data.hasPrevious);
    } catch (error) {
      console.error('뉴스 목록 가져오기 실패:', error);
    }
  };

  console.log('뉴스 목록:', newsItems);

  const [bookmarks, setBookmarks] = useState<{ [key: number]: boolean }>({});

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  const toggleBookmark = (newsId: number) => {
    setBookmarks((prev) => ({
      ...prev,
      [newsId]: !prev[newsId],
    }));
  };
  const handleNewsClick = (newsUrl: string) => {
    window.open(newsUrl, '_blank'); // 새 탭에서 URL 열기
    console.log(`새 탭에서 뉴스 URL ${newsUrl} 열림`);
  };

  const Flags = Flag as any;

  useEffect(() => {
    fetchNewsList();
  }, [
    category,
    period,
    keyword,
    keyword_mind,
    keyword_cloud,
    country,
    currentPage,
  ]);

  const headerString = [keyword, keyword_mind, keyword_cloud]
    .filter((item) => item && item.trim() !== '') // 빈 문자열 또는 undefined/null 제거
    .join(' > '); // ' > '로 연결

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      {/* 흐려진 배경 */}
      <div
        className="absolute inset-0 bg-black opacity-80 bg-opacity-50"
        onClick={handleModalClose}
      ></div>

      {/* 모달 콘텐츠 */}
      <div
        className={`relative p-2 w-full max-w-2xl mx-auto bg-white rounded-2xl shadow-lg h-11/12 overflow-y-auto ${classes.hide_scrollbar}`}
        style={{
          scrollbarWidth: 'none',
          msOverflowStyle: 'none',
        }}
      >
        <div className="md:p-6 flex flex-col h-full">
          {/* 헤더 */}
          <div className="flex items-start justify-between">
            <h2 className="text-2xl md:text-3xl text-gray-500 font-semibold mb-2 flex mx-5">
              <Flags code={country} width="40" />
              <span className="text-black ml-4 headline-medium">
                {getCountryName(country)}
              </span>
            </h2>
            <img
              onClick={handleModalClose}
              className="cursor-pointer"
              src="/assets/images/Close_round.png"
              alt="닫기"
            />
          </div>

          {/* 제목 */}
          <div className="mb-4 mt-2 ml-6">
            <span className="text-amount-400 headline-medium md:text-xl font-semibold">
              {headerString}
            </span>
            <span className="text-black text-base md:text-lg font-semibold">
              에 대한{' '}
            </span>
            <span className="text-amount-400 text-base md:text-lg font-semibold">
              뉴스
            </span>
            <span className="text-black text-base md:text-lg font-semibold">
              {' '}
              기사
            </span>
          </div>

          {/* 뉴스 리스트 또는 알림 메시지 */}
          {newsItems.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-full text-center text-gray-500">
              <SentimentDissatisfiedIcon
                className="text-gray-400"
                style={{ fontSize: '64px' }}
              />
              <p className="text-lg font-medium mt-4">
                키워드에 해당하는 기사가 없습니다.
              </p>
            </div>
          ) : (
            <>
              {/* 뉴스 리스트 */}
              <p className="text-slate-400 text-xs justify-end flex mr-6">
                총 {totalElements}건
              </p>
              <div className="space-y-4 flex-grow overflow-y-auto m-4">
                {newsItems.map((item, index) => (
                  <div
                    key={index}
                    onClick={() => handleNewsClick(item.url)}
                    className="cursor-pointer"
                  >
                    <NewsItem
                      date={item.published_at}
                      title={item.title}
                      image={item.image_url}
                      sentiment={item.keywords[0]}
                      tags={item.keywords.slice(1)}
                      bookmarked={false}
                      onToggleBookmark={() => toggleBookmark(index)}
                    />
                  </div>
                ))}
              </div>
            </>
          )}

          {/* 페이지네이션 하단 고정 */}
          <div className="mt-auto">
            <Pagination
              page={currentPage}
              size={itemsPerPage}
              totalElements={totalElements}
              totalPages={totalPages}
              hasNext={hasNext}
              hasPrevious={hasPrevious}
              onPageChange={handlePageChange}
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default NewsModal;
