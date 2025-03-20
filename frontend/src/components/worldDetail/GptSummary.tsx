const GptSummary = ({ analysis, width, height }) => {
  return (
    <>
      <div
        className="space-y-2 p-2 border-2 border-gray-500 rounded-lg"
        style={{
          width: `${width}px`,
          height: `${height}px`,
        }}
      >
        <p>{analysis}</p>
      </div>
    </>
  );
};

export default GptSummary;
