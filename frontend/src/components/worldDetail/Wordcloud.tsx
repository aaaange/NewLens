import React, { useState, useCallback, useMemo } from 'react';
import { Wordcloud } from '@visx/wordcloud';
import Flag from 'react-world-flags';

const colors: string[] = [
  '#FFD700',
  '#FF5733',
  '#36D7B7',
  '#F4D03F',
  '#EC7063',
  '#85C1E9',
  '#F5B7B1',
  '#58D68D',
];

interface WordType {
  text: string;
  value: number;
}

interface WordCloudProps {
  keywords: WordType[];
  width: number;
  height: number;
  keyword: string;
  keyword_mind: string;
  country_name: string;
  country_code: string;
  handleWordCloudChange?: (word: string) => void; // 단어 클릭 시 부모 컴포넌트에 전달하는 함수
  handleModalOpen?: (country: string) => void; // 모달 열기 함수
}

interface CloudWord {
  text?: string;
  value?: number;
  x?: number;
  y?: number;
  size?: number;
  rotate?: number;
}

const WordCloud = ({
  keywords,
  width,
  height,
  keyword,
  keyword_mind,
  country_name,
  country_code,
  handleWordCloudChange,
  handleModalOpen,
}: WordCloudProps) => {
  const memoizedKeywords = useMemo(() => {
    return keywords.map((w) => ({ ...w })); // 새로운 객체로 만들어 메모이제이션
  }, [keywords]);

  const handleWordClick = (word: CloudWord): void => {
    // alert는 반환 값이 없음으로 void
    if (word.text) {
      handleWordCloudChange?.(word.text);
    }
    handleModalOpen?.(country_code);
  };

  const minFontSize = 14; // 최소 글자 크기
  const maxFontSize = 48; // 최대 글자 크기

  // value의 최소/최대값 찾기
  const values = keywords.map((w) => w.value);
  const min = Math.min(...values);
  const max = Math.max(...values);

  // 정규화된 fontSize 함수
  const getFontSize = (word: WordType) => {
    if (max === min) return (minFontSize + maxFontSize) / 2; // 모든 값이 같을 때 중간값
    const normalized = (word.value - min) / (max - min);
    return minFontSize + normalized * (maxFontSize - minFontSize);
  };

  const Flags = Flag as any;
  const headerString = [keyword, keyword_mind]
    .filter((item) => item && item.trim() !== '') // 빈 문자열 또는 undefined/null 제거
    .join(' > '); // ' > '로 연결

  return (
    <div className="flex flex-col gap-2" style={{ width: `${width}px` }}>
      <p className="flex items-center flex-wrap">
        <span className="text-amount-300 text-lg ">{headerString}</span>
        에 대한&nbsp;
        <Flags code={country_code} width="24" height="12" /> &nbsp;
        <span className="text-amount-300 text-lg"> {country_name}</span>의 관련
        키워드
      </p>
      <svg width={width} height={height}>
        <Wordcloud
          key="fixed"
          words={memoizedKeywords}
          width={width}
          height={height}
          fontSize={getFontSize}
          padding={1}
          rotate={0}
          spiral="archimedean"
          random={() => 0.42}
        >
          {(cloudWords) => (
            <WordRenderer
              cloudWords={cloudWords}
              handleWordClick={handleWordClick}
            />
          )}
        </Wordcloud>
      </svg>
    </div>
  );
};

interface WordRendererProps {
  cloudWords: CloudWord[];
  handleWordClick: (word: CloudWord) => void;
}

// 단어 렌더링을 최적화한 컴포넌트 (불필요한 재렌더링 방지)
const WordRenderer = React.memo(
  ({ cloudWords, handleWordClick }: WordRendererProps) => {
    const [hoveredWord, setHoveredWord] = useState<string | null>(null);

    const handleMouseEnter = useCallback((word: CloudWord) => {
      if (word.text) {
        setHoveredWord(word.text);
      }
    }, []);

    const handleMouseLeave = useCallback(() => {
      setHoveredWord(null);
    }, []);

    return cloudWords.map((word, i) => (
      <text
        key={word.text}
        fill={colors[i % colors.length]}
        textAnchor="middle"
        transform={`translate(${word.x}, ${word.y}) rotate(${word.rotate})`}
        fontSize={
          hoveredWord && word.text && hoveredWord === word.text
            ? (word.size ?? 0) + 5
            : (word.size ?? 0)
        }
        style={{
          cursor: 'pointer',
          userSelect: 'none',
          opacity: hoveredWord === word.text ? 1 : 0.7,
          transition: 'font-size 0.2s ease, opacity 0.2s ease',
        }}
        onClick={() => handleWordClick(word)}
        onMouseEnter={() => handleMouseEnter(word)}
        onMouseLeave={handleMouseLeave}
      >
        {word.text}
      </text>
    ));
  }
);

export default React.memo(WordCloud);
