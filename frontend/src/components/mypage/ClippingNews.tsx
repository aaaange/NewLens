import { useState, useEffect } from 'react';
import NewsItem from '../worldDetail/NewsItem';
import {
  Bookmark,
  ChevronFirst,
  ChevronLast,
  ChevronLeft,
  ChevronRight,
} from 'lucide-react';
import { useScrapNews, News } from '../../hooks/useMypageNews';
import { toast } from 'react-toastify';

// 페이지네이션 (공통 컴포넌트 못찾은 나...)
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

const ClippingNews = () => {
  const [articles, setArticles] = useState<News[]>([]);

  const { scrapNewsList, loading, fetchScrapNews, scrapNews } = useScrapNews();

  // 뉴스 삭제
  const deleteArticle = async (newsId: string) => {
    try {
      const res = await scrapNews(newsId);
      if (!res) return;

      const { isScrap } = res;

      // 스크랩이 해제된 경우만 UI에서 제거
      if (!isScrap) {
        setArticles((prev) => prev.filter((item) => item.newsId !== newsId));
      }
    } catch (err) {
      console.error('스크랩 해제 실패:', err);
      toast.error('스크랩 해제에 실패했어요. 다시 시도해주세요!');
    }
  };

  useEffect(() => {
    fetchScrapNews();
  }, []);

  useEffect(() => {
    setArticles(scrapNewsList);
  }, [scrapNewsList]);

  // if (loading) return <p>로딩 중...</p>;

  return (
    <div>
      <h2 className="text-2xl mb-4">스크랩 NEWS</h2>
      <div className="rounded-lg">
        {articles.length === 0 ? (
          <div className="flex flex-col justify-center items-center h-screen gap-2">
            <p className="headline-xlarge">📰</p>
            <p className="text-gray-500">아직 스크랩한 뉴스가 없어요!</p>
          </div>
        ) : (
          articles.map((item) => (
            <div key={item.newsId} className="flex items-center gap-4">
              <button
                onClick={() => deleteArticle(item.newsId)}
                className="cursor-pointer"
              >
                <Bookmark
                  size={20}
                  className={`transition-colors duration-200 ${
                    item.isScrap
                      ? 'fill-yellow-400 text-yellow-400'
                      : 'text-gray-300'
                  }`}
                />
              </button>
              <div className="w-full">
                <NewsItem
                  title={item.title}
                  url={item.url}
                  publishedDate={item.publishedAt}
                  imageUrl={item.imageUrl}
                />
              </div>
            </div>
          ))
        )}
      </div>
      {/* 페이지네이션 -- postman 명세서가 안보여요... */}
      {/* <Pagination
        page={currentPage}
        size={itemsPerPage}
        totalElements={totalElements}
        totalPages={totalPages}
        hasNext={hasNext}
        hasPrevious={hasPrevious}
        onPageChange={handlePageChange}
      /> */}
    </div>
  );
};

export default ClippingNews;
