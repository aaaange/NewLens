import { useState, useEffect } from 'react';
import 'keen-slider/keen-slider.min.css';
import { useKeenSlider } from 'keen-slider/react';
import { ArrowLeft, ArrowRight, Bookmark } from 'lucide-react';
import { useScrapNews, News } from '../../hooks/useMypageNews';

export default function NewsCarousel() {
  const {
    logNewsList,
    loading,
    error,
    scrapNews,
    fetchNewsLog,
  } = useScrapNews();
  const [articles, setArticles] = useState<News[]>([]);

  // 슬라이더 ref, 인스턴스 참조 가져오기
  const [sliderRef, instanceRef] = useKeenSlider<HTMLDivElement>({
    slides: {
      perView: 3,
      spacing: 15,
    },
    mode: 'snap',
    loop: false,
  });

  // 뉴스 스크랩
  const toggleScrap = async (newsId: string) => {
    const result = await scrapNews(newsId);

    if (result) {
      const { isScrap } = result;
      setArticles((prev) =>
        prev.map((news) =>
          news.newsId === newsId ? { ...news, isScrap } : news
        )
      );
    } else {
      console.error('스크랩 상태 갱신 실패!');
    }
  };

  useEffect(() => {
    fetchNewsLog();
  }, []);

  useEffect(() => {
    setArticles(logNewsList); // API에서 받아온 데이터 복사
  }, [logNewsList]);

  if (loading) return <p>Loading...</p>;

  return (
    <div className="relative max-w-5xl mx-auto px-4">
      {/* 왼쪽 버튼 */}
      <button
        onClick={() => instanceRef.current?.prev()}
        className="absolute left-0 top-1/2 -translate-y-1/2 z-10 bg-gray-700 shadow rounded-full p-2 hover:bg-gray-800"
      >
        <ArrowLeft className="w-5 h-5" />
      </button>

      {/* 슬라이더 본체 */}
      <div ref={sliderRef} className="keen-slider">
        {logNewsList.map((news) => (
          <div className="keen-slider__slide rounded overflow-hidden shadow bg-white">
            <a href={news.url} target="_blank" rel="noopener noreferrer">
              <div className="relative">
                {/* 북마크 버튼 */}
                <button
                  onClick={() => toggleScrap(news.newsId)}
                  className="cursor-pointer"
                >
                  <Bookmark
                    size={20}
                    className={`transition-colors ${
                      news.isScrap
                        ? 'fill-yellow-400 text-yellow-400'
                        : 'text-gray-300'
                    }`}
                  />
                </button>

                {/* 이미지 */}
                <img
                  src={news.imageUrl}
                  alt={news.title}
                  className="w-full h-40 object-cover"
                />
              </div>

              {/* 텍스트 */}
              <div className="p-4">
                <h3 className="text-base font-semibold line-clamp-2 text-primary-900">
                  {news.title}
                </h3>
                <p className="text-xs text-gray-500 mt-2">{news.publishedAt}</p>
              </div>
            </a>
          </div>
        ))}
      </div>

      {/* 오른쪽 버튼 */}
      <button
        onClick={() => instanceRef.current?.next()}
        className="absolute right-0 top-1/2 -translate-y-1/2 z-10 bg-gray-700 shadow rounded-full p-2 hover:bg-gray-800"
      >
        <ArrowRight className="w-5 h-5" />
      </button>
    </div>
  );
}
