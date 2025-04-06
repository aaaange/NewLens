import { useState, useEffect } from 'react';
import NewsItem from '../worldDetail/NewsItem';
import { Bookmark } from 'lucide-react';
import { useScrapNews, News } from '../../hooks/useMypageNews';
import { toast } from 'react-toastify';

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
    </div>
  );
};

export default ClippingNews;
