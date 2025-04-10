import GlobalSpinner from '../common/GlobalSpinner';
interface GptSummaryProps {
  description: string;
  isLoading: boolean;
  width: number;
  height: number;
  keyword: string;
}

const GptSummary = ({
  description,
  isLoading,
  width,
  height,
  keyword,
}: GptSummaryProps) => {
  // if (isLoading) return <GlobalSpinner />;
  return (
    <>
      <div
        className="space-y-2 p-2 border-2 border-gray-500 rounded-lg flex flex-col justify-center items-center"
        style={{
          width: `${width}px`,
          height: `${height}px`,
        }}
      >
        <p className="flex items-center flex-wrap text-xl">
          {/* 두 국가에서 본&nbsp; */}
          <span className="text-amount-300 ">{keyword}</span>에 대한 한 줄 요약
        </p>
        {isLoading ? <GlobalSpinner /> : <p>{description}</p>}
      </div>
    </>
  );
};

export default GptSummary;
