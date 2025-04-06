import Flag from 'react-world-flags';

interface NewsSummaryProps {
  description: string;
  width: number;
  height: number;
  keyword: string;
  keyword_mind: string;
  country_name: string;
  country_code: string;
}

const NewsSummary = ({
  description,
  width,
  height,
  keyword,
  keyword_mind,
  country_name,
  country_code,
}: NewsSummaryProps) => {
  const Flags = Flag as any;
  const headerString = [keyword, keyword_mind]
    .filter((item) => item && item.trim() !== '') // 빈 문자열 또는 undefined/null 제거
    .join(' > '); // ' > '로 연결
  return (
    <div className="flex flex-col gap-2" style={{ width: `${width}px` }}>
      <div className="flex justify-between ">
        <p className="flex items-center flex-wrap">
          <span className="text-amount-300 text-lg ">{headerString}</span>에
          대한&nbsp;
          <Flags code={country_code} width="24" height="12" /> &nbsp;
          <span className="text-lg text-amount-300"> {country_name}</span>
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
          <div className="body-medium">{description}</div>
        </div>
      </div>
    </div>
  );
};

export default NewsSummary;
