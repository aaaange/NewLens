import WordCloud from '../components/worldDetail/Wordcloud';
import StackedColumns from '../components/worldDetail/StackedColumns';
import MentionChart from '../components/worldDetail/MentionChart';

const country_name = "미국"

const keyword = "속초 도련님"

const words = [
  { text: '시간', value: 30 },
  { text: '절대적인', value: 20 },
  { text: '사람들', value: 40 },
  { text: '독립적인', value: 25 },
  { text: '선언하다', value: 45 },
  { text: '권력', value: 46 },
  { text: '전쟁', value: 12 },
  { text: '평화', value: 18 },
  { text: '폭정', value: 12 },
  { text: '자유로운', value: 14 },
  { text: '형태', value: 13 },
  { text: '동의', value: 12 },
  { text: '식민지', value: 16 },
  { text: '해산하다', value: 15 },
  { text: '자유', value: 18 },
];

const sentimentData = [
  { period: '2025-03-01', positive: 0.7, neutral: 0.2, negative: 0.1 },
  { period: '2025-03-02', positive: 0.6, neutral: 0.3, negative: 0.1 },
  { period: '2025-03-03', positive: 0.8, neutral: 0.15, negative: 0.05 },
];

const data = [
  { period: "2025-03-01", count: 121 },
  { period: "2025-03-02", count: 126 },
  { period: "2025-03-03", count: 110 },
  { period: "2025-03-04", count: 130 },
  { period: "2025-03-05", count: 90 },
  { period: "2025-03-06", count: 115 },
];

const WorldDetail = () => {
  return (
    <div>
      <h2> 워드클라우드 테스트</h2>
      <WordCloud words={words} width={300} height={200} keyword={keyword} country_name={country_name}/>
      <StackedColumns data={sentimentData} width={300} height={200} keyword={keyword} country_name={country_name}/>
      <MentionChart data={data}  width={300} height={200} keyword={keyword} country_name={country_name} />
    </div>
  );
};

export default WorldDetail;
