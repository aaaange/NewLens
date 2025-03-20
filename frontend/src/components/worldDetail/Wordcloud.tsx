import React, { useState, useCallback } from 'react';
import { Wordcloud } from '@visx/wordcloud';
import Flag from 'react-world-flags';

const colors = [
  '#FFD700',
  '#FF5733',
  '#36D7B7',
  '#F4D03F',
  '#EC7063',
  '#85C1E9',
  '#F5B7B1',
  '#58D68D',
];

const WordCloud = ({ words, width, height, keyword, country_name }) => {
  const [hoveredWord, setHoveredWord] = useState(null);

  const handleWordClick = (word) => {
    alert(`클릭한 단어: ${word.text}, 빈도: ${word.value}`);
  };

  return (
    <>
      <p className="flex">
        <span className="text-system-warning">{keyword}</span>에 대한&nbsp;
        <Flag code="US" width="24" height="12" /> &nbsp;
        <span className="text-system-warning"> {country_name}</span>의 관련
        키워드
      </p>
      <svg width={width} height={height}>
        <Wordcloud
          words={words}
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

// 단어 렌더링을 최적화한 컴포넌트 (불필요한 재렌더링 방지)
const WordRenderer = React.memo(({ cloudWords, handleWordClick }) => {
  const [hoveredWord, setHoveredWord] = useState(null);

  const handleMouseEnter = useCallback((word) => {
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
});

// 기본 props 설정 (사용자가 크기를 지정하지 않으면 기본값 적용)
WordCloud.defaultProps = {
  width: 500,
  height: 500,
};

export default WordCloud;
