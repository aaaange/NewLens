import React, { useState, useCallback } from 'react';
import { Wordcloud } from '@visx/wordcloud';
import Flag from 'react-world-flags';
import { WordType } from '../../pages/WorldDetail';

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

interface WordCloudProps {
  keywords: WordType[];
  width: number;
  height: number;
  keyword: string;
  country_name: string;
  country_code: string;
}

interface CloudWord {
  text: string;
  value: number;
  x: number;
  y: number;
  size: number;
  rotate: number;
}

const WordCloud = ({
  keywords,
  width,
  height,
  keyword,
  country_name,
  country_code,
}: WordCloudProps) => {
  // console.log('워드클라우드 키워드:', keyword);
  const handleWordClick = (word: CloudWord): void => {
    // alert는 반환 값이 없음으로 void
    alert(`클릭한 단어: ${word.text}, 빈도: ${word.value}`);
  };

  return (
    <>
      <p className="flex">
        <span className="text-system-warning">{keyword}</span>에 대한&nbsp;
        <Flag code={country_code} width="24" height="12" /> &nbsp;
        <span className="text-system-warning"> {country_name}</span>의 관련
        키워드
      </p>
      <svg width={width} height={height}>
        <Wordcloud
          words={keywords}
          width={width}
          height={height}
          fontSize={(word) => Math.sqrt(word.value) * 5}
          padding={1}
          rotate={0}
        >
          {(cloudWords) => (
            <WordRenderer
              cloudWords={cloudWords}
              handleWordClick={handleWordClick}
            />
          )}
        </Wordcloud>
      </svg>
    </>
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
      setHoveredWord(word.text);
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
        fontSize={hoveredWord === word.text ? word.size + 5 : word.size}
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

export default WordCloud;
