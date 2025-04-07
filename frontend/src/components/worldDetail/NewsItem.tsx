import { formatDate } from '../../utils/formatDateUtils';

interface NewsItemProps {
  title: string;
  url: string;
  published_at: string;
  image_url: string;
  onClick?: () => void;
}

const NewsItem = ({ title, url, published_at, image_url, onClick }: NewsItemProps) => {
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
        <h3 className="body-medium font-semibold">
          {title.length > 48 ? title.slice(0, 46) + '...' : title}
        </h3>
        {/* 뉴스 제목 */}
        <span className="text-sm text-gray-500 text-end">
          {formatDate(published_at, '')}
        </span>
        {/* 뉴스 발행 날짜 */}
      </div>
    </a>
  );
};

export default NewsItem;
