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
import Pagination from '../common/Pagination';
import GlobalSpinner from '../common/GlobalSpinner';

const ClippingNews = () => {
  const [page, setPage] = useState(1);
  const [size] = useState(5);
  const [articles, setArticles] = useState<News[]>([]);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [hasNext, setHasNext] = useState(false);
  const [hasPrevious, setHasPrevious] = useState(false);

  const { scrapNewsList, loading, fetchScrapNews, scrapNews } = useScrapNews();

  // 뉴스 삭제
  const deleteArticle = async (news_id: string) => {
    try {
      const res = await scrapNews(news_id);
      if (!res) return;

      const { is_scrap } = res;

      // 스크랩이 해제된 경우만 UI에서 제거
      if (!is_scrap) {
        setArticles((prev) => prev.filter((item) => item.news_id !== news_id));
      }
    } catch (err) {
      console.error('스크랩 해제 실패:', err);
      toast.error('스크랩 해제에 실패했어요. 다시 시도해주세요!');
    }
  };

  useEffect(() => {
    const loadScrapNews = async () => {
      const data = await fetchScrapNews(page, size);
      if (data) {
        setArticles(data.news);
        setTotalElements(data.total_elements);
        setTotalPages(data.total_pages);
        setHasNext(data.has_next);
        setHasPrevious(data.has_previous);
      }
    };
    loadScrapNews();
  }, [page]);

  useEffect(() => {
    setArticles(scrapNewsList);
  }, [scrapNewsList]);

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
  };

  if (loading) return <GlobalSpinner />;

  return (
    <div>
      <h2 className="text-2xl mb-4">스크랩 NEWS</h2>
      <div className="rounded-lg">
        {articles.length === 0 ? (
          <div className="flex flex-col items-center justify-center min-h-[calc(100vh-280px)] py-12 gap-2">
            <p className="headline-xlarge">📰</p>
            <p className="text-gray-500">아직 스크랩한 뉴스가 없어요!</p>
          </div>
        ) : (
          articles.map((item) => (
            <div key={item.news_id} className="flex items-center gap-4">
              <button
                onClick={() => deleteArticle(item.news_id)}
                className="cursor-pointer"
              >
                <Bookmark
                  size={20}
                  className={`transition-colors duration-200 ${
                    item.is_scrap
                      ? 'fill-yellow-400 text-yellow-400'
                      : 'text-gray-300'
                  }`}
                />
              </button>
              <div className="w-full">
                <NewsItem
                  title={item.title}
                  url={item.url}
                  published_at={item.published_at}
                  image_url={item.image_url}
                />
              </div>
            </div>
          ))
        )}
      </div>
      {/* 페이지네이션 -- postman 명세서가 안보여요... */}
      <Pagination
        page={page}
        size={size}
        totalElements={totalElements}
        totalPages={totalPages}
        hasNext={hasNext}
        hasPrevious={hasPrevious}
        onPageChange={handlePageChange}
      />
    </div>
  );
};

export default ClippingNews;
