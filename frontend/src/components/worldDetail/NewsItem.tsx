import { getCountryName } from '../../utils/countryUtils';
import { formatDate } from '../../utils/formatDateUtils';

interface NewsItemProps {
  title: string;
  url: string;
  published_at: string;
  image_url: string;

  onClick?: () => void;
  keywords?: string[];
  country?: string;
}

const NewsItem = ({
  title,
  url,
  published_at,
  image_url,
  onClick,
  keywords,
  country,
}: NewsItemProps) => {
  return (
    <a
      href={url}
      target="_blank"
      rel="noopener noreferrer"
      className="flex p-4 hover:bg-primary-900 transition"
      onClick={onClick}
    >
      <img
        onError={(e) => {
          e.currentTarget.onerror = null; // 무한 루프 방지
          e.currentTarget.src = '/assets/images/logo-newLens.png'; // Vite, CRA 공통으로 사용 가능
        }}
        src={image_url}
        alt={title}
        className="w-20 h-20 object-cover mr-4"
      />
      <div className="flex flex-col w-full justify-between">
        <div>
          {/* 뉴스 제목 */}
          <h3 className="body-medium font-semibold">
            {title.length > 48 ? title.slice(0, 46) + '...' : title}
          </h3>

          {/* 국가명 및 키워드 태그 */}
          {(country || (keywords && keywords.length > 0)) && (
            <div className="flex flex-wrap items-center gap-1 mt-1">
              {country && (
                <span className="text-xs text-gray-700 bg-primary-100 px-2 py-1 rounded-full font-medium">
                  #{getCountryName(country.toUpperCase())}
                </span>
              )}
              {keywords &&
                keywords.map((keyword, idx) => (
                  <span
                    key={idx}
                    className="text-xs text-gray-600 bg-gray-100 px-2 py-1 rounded-full"
                  >
                    #{keyword}
                  </span>
                ))}
            </div>
          )}
        </div>

        {/* 뉴스 발행 날짜 */}
        <span className="text-sm text-gray-500 text-end">
          {formatDate(published_at, '')}
        </span>
      </div>
    </a>
  );
};

export default NewsItem;
