const NewsItem = ({ title, url, publishedDate, imageUrl }) => {
  return (
    <a href={url} target="_blank" rel="noopener noreferrer" className="flex items-center p-4 hover:bg-primary-900 transition">
      <img src={imageUrl} alt={title} className="w-20 h-20 object-cover mr-4" />
      <div className="flex flex-col">
        <h3 className="text-lg font-semibold">{title}</h3> {/* 뉴스 제목 */}
        <span className="text-sm text-gray-500">{publishedDate}</span> {/* 뉴스 발행 날짜 */}
      </div>
    </a>
  );
};

export default NewsItem;
