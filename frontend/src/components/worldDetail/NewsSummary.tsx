import Flag from 'react-world-flags';

const NewsSummary = ({ data, width, height, keyword, country_name }) => {
  return (
    <div>
      <div className="flex justify-between ">
        <p className="flex items-center">
          <span className="text-amount-300 text-lg ">{keyword}</span>에
          대한&nbsp;
          <Flag code="US" width="24" height="12" /> &nbsp;
          <span className="text-lg text-amount-300 text-lg">
            {' '}
            {country_name}
          </span>
          &nbsp;언론의 반응은?
        </p>
      </div>
      <div
        className="space-y-2 p-2 border-2 border-gray-500 rounded-lg"
        style={{
          width: `${width}px`,
          height: `${height}px`,
          overflowY: "scroll", // 스크롤 동작 가능
          msOverflowStyle: "none", // IE & Edge
          scrollbarWidth: "none", // Firefox
        }}
      >
        <div>{data}</div>
      </div>
    </div>
  );
};

export default NewsSummary;
