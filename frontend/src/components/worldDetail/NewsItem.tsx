import { formatDate } from '../../utils/formatDateUtils';

interface NewsItemProps {
  title: string;
  url: string;
  publishedDate: string;
  imageUrl: string;
}

const NewsItem = ({ title, url, publishedDate, imageUrl }: NewsItemProps) => {
  return (
    <a
      href={url}
      target="_blank"
      rel="noopener noreferrer"
      className="flex p-4 hover:bg-primary-900 transition"
    >
      <img
        onError={(e) => {
          e.currentTarget.onerror = null; // 무한 루프 방지
          e.currentTarget.src = '/assets/images/logo-newLens.png'; // Vite, CRA 공통으로 사용 가능
        }}
        src={imageUrl}
        alt={title}
        className="w-20 h-20 object-cover mr-4"
      />
      <div className="flex flex-col w-full justify-between">
        <h3 className="body-medium font-semibold">{title}</h3> {/* 뉴스 제목 */}
        <span className="text-sm text-gray-500 text-end">
          {formatDate(publishedDate, '')}
        </span>{' '}
        {/* 뉴스 발행 날짜 */}
      </div>
    </a>
  );
};

export default NewsItem;
