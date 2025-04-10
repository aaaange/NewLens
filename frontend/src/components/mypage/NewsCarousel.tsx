import { useState, useEffect } from 'react';
import 'keen-slider/keen-slider.min.css';
import { useKeenSlider } from 'keen-slider/react';
import { ArrowLeft, ArrowRight, Bookmark } from 'lucide-react';
import { useScrapNews, News } from '../../hooks/useMypageNews';
import GlobalSpinner from '../common/GlobalSpinner';
import { formatDate } from '../../utils/formatDateUtils';

export default function NewsCarousel() {
  const {
    logNewsList,
    loading,
    error,
    scrapNews,
    fetchNewsLog,
    fetchtAccessLog,
  } = useScrapNews();
  const [articles, setArticles] = useState<News[]>([]);

  // 슬라이더 ref, 인스턴스 참조 가져오기
  const [sliderRef, instanceRef] = useKeenSlider<HTMLDivElement>({
    slides: {
      perView: 3,
      spacing: 15,
    },
    loop: false,
    mode: 'snap',
  });

  // 뉴스 스크랩
  const toggleScrap = async (news_id: string) => {
    const result = await scrapNews(news_id);

    if (result) {
      const { is_scrap } = result;
      setArticles((prev) =>
        prev.map((news) =>
          news.news_id === news_id ? { ...news, is_scrap } : news
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

  useEffect(() => {
    if (instanceRef.current) {
      instanceRef.current.update();
    }
  }, [articles]);

  if (loading) return <GlobalSpinner />;

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
        {articles.length === 0 ? (
          <div className="flex flex-col items-center justify-center w-full h-60 rounded">
            <p className="headline-xlarge">🗞️</p>
            <p className="text-gray-500">최근 본 뉴스가 없어요!</p>
          </div>
        ) : (
          articles.map((news) => (
            <div
              key={news.news_id}
              className="keen-slider__slide rounded overflow-hidden shadow bg-primary-900 w-lg"
            >
              <a
                href={news.url}
                target="_blank"
                rel="noopener noreferrer"
                onClick={() => fetchtAccessLog(news.news_id)}
              >
                <div className="relative">
                  <button
                    onClick={(e) => {
                      e.preventDefault(); // 링크 이동 막기
                      toggleScrap(news.news_id);
                    }}
                    className="cursor-pointer p-2 m-2"
                  >
                    <Bookmark
                      size={20}
                      className={`transition-colors ${
                        news.is_scrap
                          ? 'fill-amount-300 text-amount-300'
                          : 'text-gray-300'
                      }`}
                    />
                  </button>
                  <img
                    onError={(e) => {
                      e.currentTarget.onerror = null; // 무한 루프 방지
                      e.currentTarget.src = '/assets/images/logo-newLens.png'; // Vite, CRA 공통으로 사용 가능
                    }}
                    src={news.image_url}
                    alt={news.title}
                    className="w-full h-40 object-cover"
                  />
                </div>
                <div className="p-4">
                  <h3 className="text-base font-semibold line-clamp-2 text-white">
                    {news.title}
                  </h3>
                  <p className="text-xs text-gray-300 mt-2">
                    {formatDate(news.published_at, 'full')}
                  </p>
                </div>
              </a>
            </div>
          ))
        )}
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
