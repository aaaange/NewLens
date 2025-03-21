import Flag from 'react-world-flags';

interface NewsSummaryProps {
  data: string;
  width: number;
  height: number;
  keyword: string;
  country_name: string;
}

const NewsSummary = ({ data, width, height, keyword, country_name }: NewsSummaryProps) => {
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
        className="border-2 py-2 border-gray-500 rounded-lg"
        style={{ width: `${width}px`, height: `${height}px` }}
      >
        {/* 스크롤 박스(자식) */}
        <div
          className="px-2 h-full overflow-y-scroll"
          style={{
            msOverflowStyle: 'none', // IE & Edge
            scrollbarWidth: 'none', // Firefox
          }}
        >
          {/* 실제 내용 */}
          <div>{data}</div>
        </div>
      </div>
    </div>
  );
};

export default NewsSummary;
