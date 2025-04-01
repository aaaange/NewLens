import { useEffect, useState } from 'react';
import {
  Bookmark,
  ChevronFirst,
  ChevronLast,
  ChevronLeft,
  ChevronRight,
} from 'lucide-react';
import Flag from 'react-world-flags';
import { getNewsListForModalApi } from '../../services/api/worldService';
import { getCountryName } from '../../utils/countryUtils';

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
    switch (sentiment) {
      case 'positive':
        return 'bg-positive text-white';
      case 'negative':
        return 'bg-negative text-white';
      default:
        return 'bg-neutral';
    }
  };

  const getSentimentText = (sentiment: string) => {
    switch (sentiment) {
      case 'positive':
        return '긍정적';
      case 'negative':
        return '부정적';
      default:
        return '중립적';
    }
  };

  return (
    <div className="flex items-start md:items-center mb-2 hover:bg-gray-50 p-2 rounded transition-colors">
      <img className="w-20 h-14 mr-4 object-fit" src={image} alt={title} />
      <div className="flex-grow">
        <p className="text-slate-400 text-xs mb-1">{date}</p>
        <h3 className="text-black text-sm font-medium mb-2">{title}</h3>
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

interface PaginationPropsType {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  hasNext: boolean;
  hasPrevious: boolean;
  onPageChange: (page: number) => void;
}

const Pagination = ({
  page,
  totalPages,
  hasNext,
  hasPrevious,
  onPageChange,
}: PaginationPropsType) => {
  const getPageNumbers = () => {
    const pageNumbers = [];
    const maxPagesToShow = 5;

    let startPage = Math.max(1, page - Math.floor(maxPagesToShow / 2));
    let endPage = Math.min(totalPages, startPage + maxPagesToShow - 1);

    if (endPage - startPage + 1 < maxPagesToShow) {
      startPage = Math.max(1, endPage - maxPagesToShow + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
      pageNumbers.push(i);
    }

    return pageNumbers;
  };

  return (
    <div className="flex justify-center items-center p-4 flex-wrap gap-1">
      <button
        className="cursor-pointer w-8 h-8 flex items-center justify-center bg-white rounded-lg border border-zinc-200 hover:bg-gray-100 disabled:opacity-50"
        onClick={() => onPageChange(1)}
        disabled={!hasPrevious}
        aria-label="첫 페이지"
      >
        <ChevronFirst color="black" size={16} />
      </button>
      <button
        className="cursor-pointer w-8 h-8 flex items-center justify-center bg-white rounded-lg border border-zinc-200 hover:bg-gray-100 disabled:opacity-50"
        onClick={() => onPageChange(page - 1)}
        disabled={!hasPrevious}
        aria-label="이전 페이지"
      >
        <ChevronLeft color="black" size={16} />
      </button>

      {getPageNumbers().map((pageNum) => (
        <button
          key={pageNum}
          className={`cursor-pointer w-8 h-8 flex items-center justify-center rounded-lg ${
            pageNum === page
              ? 'bg-slate-300 text-white'
              : 'bg-white text-zinc-800 border border-zinc-200 hover:bg-gray-100'
          }`}
          onClick={() => onPageChange(pageNum)}
          aria-label={`${pageNum} 페이지`}
          aria-current={pageNum === page ? 'page' : undefined}
        >
          {pageNum}
        </button>
      ))}

      <button
        className="cursor-pointer w-8 h-8 flex items-center justify-center bg-white rounded-lg border border-zinc-200 hover:bg-gray-100 disabled:opacity-50"
        onClick={() => onPageChange(page + 1)}
        disabled={!hasNext}
        aria-label="다음 페이지"
      >
        <ChevronRight color="black" size={16} />
      </button>
      <button
        className="cursor-pointer w-8 h-8 flex items-center justify-center bg-white rounded-lg border border-zinc-200 hover:bg-gray-100 disabled:opacity-50"
        onClick={() => onPageChange(totalPages)}
        disabled={!hasNext}
        aria-label="마지막 페이지"
      >
        <ChevronLast color="black" size={16} />
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
  // const allNewsItems = [
  //   {
  //     id: 1,
  //     date: '2025년 1월 13일',
  //     title: `제주 중증환자 골든타임 지킴이…'하늘 위 응급실' 닥터헬기[영상]`,
  //     image: '/api/placeholder/80/56',
  //     sentiment: 'positive',
  //     tags: ['중증외상센터', '골든타임'],
  //   },
  //   {
  //     id: 2,
  //     date: '2025년 1월 12일',
  //     title: `의료진 부족에 허덕이는 지방 병원, 응급 상황 대처 어려움 커져`,
  //     image: '/api/placeholder/80/56',
  //     sentiment: 'negative',
  //     tags: ['의료진부족', '지방병원'],
  //   },
  //   {
  //     id: 3,
  //     date: '2025년 1월 11일',
  //     title: `새로운 응급 의료 시스템 도입... "골든타임 확보율 15% 향상"`,
  //     image: '/api/placeholder/80/56',
  //     sentiment: 'positive',
  //     tags: ['응급의료', '골든타임'],
  //   },
  //   {
  //     id: 4,
  //     date: '2025년 1월 10일',
  //     title: `제주도, 농어촌 지역 응급 의료 서비스 확대 계획 발표`,
  //     image: '/api/placeholder/80/56',
  //     sentiment: 'neutral',
  //     tags: ['제주도', '농어촌의료'],
  //   },
  //   {
  //     id: 5,
  //     date: '2025년 1월 9일',
  //     title: `응급 상황 대처 능력 향상을 위한 의료인 교육 프로그램 확대`,
  //     image: '/api/placeholder/80/56',
  //     sentiment: 'positive',
  //     tags: ['의료교육', '응급상황'],
  //   },
  // ];

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

  const handleNewsClick = (newsId: any) => {
    console.log(`뉴스 ID ${newsId} 클릭됨`);
    // 뉴스 상세 페이지 이동 로직 추가 가능
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

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      {/* 흐려진 배경 */}
      <div className="absolute inset-0 bg-black bg-opacity-50 backdrop-blur-sm"></div>

      {/* 모달 콘텐츠 */}
      <div className="relative p-12 w-full max-w-2xl mx-auto bg-white rounded-3xl shadow-lg overflow-hidden z-10">
        <div className="md:p-6">
          {/* 헤더 */}
          <div className="flex items-start justify-between">
            <h2 className="text-2xl md:text-3xl text-gray-500 font-semibold mb-2 flex">
              <div>
                <Flags code={country} width="50" />
              </div>
              <div className="text-black ml-4">{getCountryName(country)}</div>
            </h2>
            <img
              onClick={handleModalClose}
              className="cursor-pointer"
              src="/assets/images/Close_round.png"
              alt="닫기"
            />
          </div>

          {/* 제목 */}
          <div className="mb-4">
            <span className="text-amber-300 text-lg md:text-xl font-semibold">
              {keyword}
            </span>
            <span className="text-black text-base md:text-lg font-semibold">
              에 대한{' '}
            </span>
            <span className="text-amber-300 text-base md:text-lg font-semibold">
              뉴스
            </span>
            <span className="text-black text-base md:text-lg font-semibold">
              {' '}
              기사
            </span>
          </div>

          {/* 뉴스 리스트 */}
          <p className="text-slate-400 text-xs mb-2">총 {totalElements}건</p>
          <div className="space-y-4">
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
            {/* 페이지네이션 */}
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
