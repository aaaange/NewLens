import { useState } from 'react';

const KeywordRanking = () => {
  const keywords = [
    {
      id: 1,
      text: '삼성 청년 SW 아카데미',
      tag: 'HOT',
      tagColor: 'text-red-600',
    },
    { id: 2, text: '김싸피', tag: null },
    { id: 3, text: '삼성 채용', tag: null },
    { id: 4, text: 'IT', tag: null },
    { id: 5, text: '청년 취업', tag: null },
    { id: 6, text: '오픽 접수', tag: null },
    { id: 7, text: '상반기 채용', tag: 'NEW', tagColor: 'text-blue-400' },
    { id: 8, text: '인공지능', tag: null },
    { id: 9, text: 'Chat GPT', tag: 'NEW', tagColor: 'text-blue-400' },
    { id: 10, text: '삼성 전자 채용', tag: null },
  ];

  // 현재 시간을 설정 - 추후에 별도 util로 빼는게 좋을듯
  const [currentTime] = useState(() => {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const ampm = now.getHours() >= 12 ? '오후' : '오전';
    const hour12 = now.getHours() % 12 || 12;

    return `${year}.${month}.${day} ${ampm} ${String(hour12).padStart(2, '0')}:${minutes}`;
  });

  const handleKeywordClick = (keyword: string) => {
    console.log(`검색어 클릭: ${keyword}`);
  };

  return (
    <div className="bg-white rounded-[20px] shadow-md w-[305px] p-6">
      <div className="flex flex-col">
        {/* 헤더 */}
        <div className="mb-4">
          <h2 className="text-lg font-semibold text-black mb-1">
            실시간 인기 검색어
          </h2>
          <div className="flex justify-between items-center">
            <p className="caption-small text-tetiary-500">
              실시간으로 가장 핫한 키워드!
            </p>
            <p className="caption-small text-tetiary-500">{currentTime}</p>
          </div>
        </div>

        {/* 키워드 목록 */}
        <div className="flex flex-col">
          {keywords.map((keyword) => (
            <button
              key={keyword.id}
              className="cursor-pointer flex items-center group hover:bg-gray-50 py-1 px-1 rounded transition-colors"
              onClick={() => handleKeywordClick(keyword.text)}
            >
              <span
                className={`w-5 h-5 text-center mr-2.5 ${
                  keyword.id <= 3
                    ? 'text-slate-600 font-bold'
                    : 'text-gray-500 font-normal'
                }`}
              >
                {keyword.id}
              </span>
              <span
                className={`${
                  keyword.id <= 3
                    ? 'text-gray-600 font-bold'
                    : 'text-gray-500 font-normal'
                } group-hover:text-black`}
              >
                {keyword.text}
              </span>
              {keyword.tag && (
                <div className="ml-2.5 px-2 py-1 bg-gray-100 rounded-full h-4 flex items-center">
                  <span className={`text-xs ${keyword.tagColor}`}>
                    {keyword.tag}
                  </span>
                </div>
              )}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
};

export default KeywordRanking;
