import { useState } from 'react';
import 'keen-slider/keen-slider.min.css';
import { useKeenSlider } from 'keen-slider/react';
import { newsData } from '../worldDetail/MockData';
import { ArrowLeft, ArrowRight, Bookmark } from 'lucide-react';

export default function NewsCarousel() {
  // 슬라이더 ref, 인스턴스 참조 가져오기
  const [sliderRef, instanceRef] = useKeenSlider<HTMLDivElement>({
    slides: {
      perView: 3,
      spacing: 15,
    },
    mode: 'snap',
    loop: false,
  });

  const [bookmarks, setBookmarks] = useState<number[]>([]);

  const toggleBookmark = (index: number) => {
    setBookmarks(
      (prev) =>
        prev.includes(index)
          ? prev.filter((i) => i !== index) // 제거
          : [...prev, index] // 추가
    );
  };

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
        {newsData.map((news, i) => (
          <div
            key={i}
            className="keen-slider__slide rounded overflow-hidden shadow bg-white"
          >
            <a href={news.url} target="_blank" rel="noopener noreferrer">
              <div className="relative">
                {/* 북마크 버튼 */}
                <button
                  className="absolute z-10 m-2 bg-white hover:bg-gray-200 text-gray-800 rounded-full p-2 shadow cursor-pointer"
                  onClick={(e) => {
                    e.preventDefault(); // 링크 클릭 막기
                    console.log(`북마크 클릭: ${news.title}`);
                    toggleBookmark(i);
                  }}
                >
                  <Bookmark
                    size={20}
                    className={`transition-colors ${bookmarks.includes(i) ? 'fill-amount-300 text-amount-300' : 'text-gray-300'}`}
                  />
                </button>

                {/* 이미지 */}
                <img
                  src={news.image_url}
                  alt={news.title}
                  className="w-full h-40 object-cover"
                />
              </div>

              {/* 텍스트 */}
              <div className="p-4">
                <h3 className="text-base font-semibold line-clamp-2 text-primary-900">
                  {news.title}
                </h3>
                <p className="text-xs text-gray-500 mt-2">
                  {news.published_at}
                </p>
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
