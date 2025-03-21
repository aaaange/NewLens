import { useState } from 'react';
import {
  Bookmark,
  ChevronFirst,
  ChevronLast,
  ChevronLeft,
  ChevronRight,
} from 'lucide-react';
import Flag from 'react-world-flags';
interface propsType {
  date: string;
  title: string;
  image: string;
  sentiment: string;
  tags: string[];
  bookmarked: boolean;
  onToggleBookmark: () => {};
}

const NewsItem = ({
  date,
  title,
  image,
  sentiment,
  tags,
  bookmarked,
  onToggleBookmark,
}: propsType) => {
  // 감정에 따라서 태그의 스타일 결정
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

  // 감정 표현 한글화 - req 확인후 수정해야함 한글로 올수도?
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
      <img
        className="w-20 h-14 mr-4 object-fit"
        src="/assets/images/logo-newLens.png"
        alt={title}
      />
      <div className="flex-grow">
        <p className="text-slate-400 text-xs mb-1">{date}</p>
        <h3 className="text-black text-sm font-medium mb-2">{title}</h3>
        <div className="flex flex-wrap gap-2">
          <span
            className={`px-2 py-1 rounded-full text-xs ${getSentimentStyle(sentiment)}`}
          >
            #{getSentimentText(sentiment)}
          </span>
          {tags.map((tag: string, index: number) => (
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
        className="flex items-center justify-centerrounded-full cursor-pointer"
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

// 페이지네이션도 공통으로 빼야할지 고민해보기
interface paginationPropsType {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}
const Pagination = ({
  currentPage,
  totalPages,
  onPageChange,
}: paginationPropsType) => {
  const getPageNumbers = () => {
    const pageNumbers = [];
    const maxPagesToShow = 5;

    let startPage = Math.max(1, currentPage - Math.floor(maxPagesToShow / 2));
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
        disabled={currentPage === 1}
        aria-label="첫 페이지"
      >
        <ChevronFirst color="black" size={16} />
      </button>
      <button
        className="cursor-pointer w-8 h-8 flex items-center justify-center bg-white rounded-lg border border-zinc-200 hover:bg-gray-100 disabled:opacity-50"
        onClick={() => onPageChange(currentPage - 1)}
        disabled={currentPage === 1}
        aria-label="이전 페이지"
      >
        <ChevronLeft color="black" size={16} />
      </button>

      {getPageNumbers().map((page) => (
        <button
          key={page}
          className={`cursor-pointer w-8 h-8 flex items-center justify-center rounded-lg ${
            page === currentPage
              ? 'bg-slate-300 text-white'
              : 'bg-white text-zinc-800 border border-zinc-200 hover:bg-gray-100'
          }`}
          onClick={() => onPageChange(page)}
          aria-label={`${page} 페이지`}
          aria-current={page === currentPage ? 'page' : undefined}
        >
          {page}
        </button>
      ))}

      <button
        className="cursor-pointer w-8 h-8 flex items-center justify-center bg-white rounded-lg border border-zinc-200 hover:bg-gray-100 disabled:opacity-50"
        onClick={() => onPageChange(currentPage + 1)}
        disabled={currentPage === totalPages}
        aria-label="다음 페이지"
      >
        <ChevronRight color="black" size={16} />
      </button>
      <button
        className="cursor-pointer w-8 h-8 flex items-center justify-center bg-white rounded-lg border border-zinc-200 hover:bg-gray-100 disabled:opacity-50"
        onClick={() => onPageChange(totalPages)}
        disabled={currentPage === totalPages}
        aria-label="마지막 페이지"
      >
        <ChevronLast color="black" size={16} />
      </button>
    </div>
  );
};

const NewsModal = () => {
  const allNewsItems = [
    {
      id: 1,
      date: '2025년 1월 13일',
      title: `제주 중증환자 골든타임 지킴이…'하늘 위 응급실' 닥터헬기[영상]`,
      image: '/api/placeholder/80/56',
      sentiment: 'positive',
      tags: ['중증외상센터', '골든타임'],
    },
    {
      id: 2,
      date: '2025년 1월 12일',
      title: `의료진 부족에 허덕이는 지방 병원, 응급 상황 대처 어려움 커져`,
      image: '/api/placeholder/80/56',
      sentiment: 'negative',
      tags: ['의료진부족', '지방병원'],
    },
    {
      id: 3,
      date: '2025년 1월 11일',
      title: `새로운 응급 의료 시스템 도입... "골든타임 확보율 15% 향상"`,
      image: '/api/placeholder/80/56',
      sentiment: 'positive',
      tags: ['응급의료', '골든타임'],
    },
    {
      id: 4,
      date: '2025년 1월 10일',
      title: `제주도, 농어촌 지역 응급 의료 서비스 확대 계획 발표`,
      image: '/api/placeholder/80/56',
      sentiment: 'neutral',
      tags: ['제주도', '농어촌의료'],
    },
    {
      id: 5,
      date: '2025년 1월 9일',
      title: `응급 상황 대처 능력 향상을 위한 의료인 교육 프로그램 확대`,
      image: '/api/placeholder/80/56',
      sentiment: 'positive',
      tags: ['의료교육', '응급상황'],
    },
  ];

  // 상태 관리
  const [currentPage, setCurrentPage] = useState(1);
  const [bookmarks, setBookmarks] = useState<{ [key: number]: boolean }>({});

  // 페이지 변경 핸들러
  const handlePageChange = (page: any) => {
    setCurrentPage(page);
    // 여기에 페이지 변경 시 데이터 로드 로직 추가 가능
  };

  // 북마크 토글 핸들러
  const toggleBookmark = (newsId: any) => {
    setBookmarks((prev) => ({
      ...prev,
      [newsId]: !prev[newsId],
    }));
  };

  // 뉴스 아이템 클릭 핸들러
  const handleNewsClick = (newsId: any) => {
    console.log(`뉴스 ID ${newsId} 클릭됨`);
    // 여기에 뉴스 상세 페이지로 이동하는 로직 추가 가능
  };

  return (
    <div className="p-12 w-full max-w-2xl mx-auto bg-white rounded-3xl shadow-lg overflow-hidden">
      <div className="md:p-6">
        <div className="flex items-start justify-between">
          <h2 className="text-2xl md:text-3xl text-gray-500 font-semibold mb-2 flex">
            <div>
              <Flag code="US" width="50" />
            </div>
            <div className="text-black ml-4">미국</div>
          </h2>
          <img
            className="cursor-pointer"
            src="/assets/images/Close_round.png"
          />
        </div>
        <div className="mb-4">
          <span className="text-amber-300 text-lg md:text-xl font-semibold">
            속초도련님
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
        <p className="text-slate-400 text-xs mb-2">총 120건</p>

        <div className="space-y-4">
          {allNewsItems.map((item) => (
            <div
              key={item.id}
              onClick={() => handleNewsClick(item.id)}
              className="cursor-pointer"
            >
              <NewsItem
                {...item}
                bookmarked={!!bookmarks[item.id]}
                onToggleBookmark={async () => toggleBookmark(item.id)}
              />
            </div>
          ))}
        </div>
      </div>

      <Pagination
        currentPage={currentPage}
        totalPages={24} // 총 120건, 페이지당 5개 = 24페이지
        onPageChange={handlePageChange}
      />
    </div>
  );
};

export default NewsModal;
