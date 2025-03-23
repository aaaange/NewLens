interface GptSummaryProps {
  description: string;
  width: number;
  height: number;
}

const GptSummary = ({ description, width, height }: GptSummaryProps) => {
  return (
    <>
      <div
        className="space-y-2 p-2 border-2 border-gray-500 rounded-lg"
        style={{
          width: `${width}px`,
          height: `${height}px`,
        }}
      >
        <p>{description}</p>
      </div>
    </>
  );
};

export default GptSummary;
