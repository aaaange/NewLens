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
      <img src={imageUrl} alt={title} className="w-20 h-20 object-cover mr-4" />
      <div className="flex flex-col w-full justify-between">
        <h3 className="text-lg font-semibold">{title}</h3> {/* 뉴스 제목 */}
        <span className="text-sm text-gray-500 text-end">
          {publishedDate}
        </span>{' '}
        {/* 뉴스 발행 날짜 */}
      </div>
    </a>
  );
};

export default NewsItem;
