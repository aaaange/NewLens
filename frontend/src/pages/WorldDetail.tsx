import WordCloud from '../components/worldDetail/Wordcloud';
import StackedColumns from '../components/worldDetail/StackedColumns';
import MentionChart from '../components/worldDetail/MentionChart';
import CountryDropdown from '../components/worldDetail/CountryDropdown';
import NewsList from '../components/worldDetail/NewsList';
import NewsSummary from '../components/worldDetail/NewsSummary';
import GptSummary from '../components/worldDetail/GptSummary';

const country_name = '미국';

const keyword = '속초 도련님';

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
  { period: '2025-03-01', count: 121 },
  { period: '2025-03-02', count: 126 },
  { period: '2025-03-03', count: 110 },
  { period: '2025-03-04', count: 130 },
  { period: '2025-03-05', count: 90 },
  { period: '2025-03-06', count: 115 },
];

const newsData = [
  {
    title: 'AI 기술의 발전과 미래',
    url: 'https://n.news.naver.com/article/366/0001062129?cds=news_media_pc&type=editn',
    published_date: '2025-03-11',
    image_url:
      'https://mimgnews.pstatic.net/image/origin/366/2025/03/19/1062129.jpg?type=nf168_108&ut=20250319124606',
  },
  {
    title: '챗봇이 바꾸는 고객 서비스',
    url: 'https://n.news.naver.com/article/366/0001062129?cds=news_media_pc&type=editn',
    published_date: '2025-03-10',
    image_url:
      'https://mimgnews.pstatic.net/image/origin/366/2025/03/19/1062129.jpg?type=nf168_108&ut=20250319124606',
  },
  {
    title: '챗봇이 바꾸는 고객 서비스',
    url: 'https://n.news.naver.com/article/366/0001062129?cds=news_media_pc&type=editn',
    published_date: '2025-03-10',
    image_url:
      'https://mimgnews.pstatic.net/image/origin/366/2025/03/19/1062129.jpg?type=nf168_108&ut=20250319124606',
  },
  {
    title: '챗봇이 바꾸는 고객 서비스',
    url: 'https://n.news.naver.com/article/366/0001062129?cds=news_media_pc&type=editn',
    published_date: '2025-03-10',
    image_url:
      'https://mimgnews.pstatic.net/image/origin/366/2025/03/19/1062129.jpg?type=nf168_108&ut=20250319124606',
  },
  {
    title: '챗봇이 바꾸는 고객 서비스',
    url: 'https://n.news.naver.com/article/366/0001062129?cds=news_media_pc&type=editn',
    published_date: '2025-03-10',
    image_url:
      'https://mimgnews.pstatic.net/image/origin/366/2025/03/19/1062129.jpg?type=nf168_108&ut=20250319124606',
  },
];

const description =
  "봄꽃이 개화하는 시기에 국내 여행객들이 가장 많이 찾는 여행지가 '제주도'라는 조사 결과가 나왔다. 12일 글로벌 여행 플랫폼 트립닷컴은 오는 25일~다음 달 30일 국내 여행객의 여행 추이를 공개했다. 제주시와 서귀포시가 1, 2위에 올랐다 지난해는 반대로 서귀포시가 1위, 제주시가 2위였다. 다음으로는 서울과 부산이 뒤를 이었다.";

const analysis = "한줄 비교 요약본 from gpt"

const WorldDetail = () => {
  return (
    <div>
      <h2> 워드클라우드 테스트</h2>
      <CountryDropdown width="250px" height="60px" />
      <GptSummary analysis={analysis} width={900} height={125}/>
      <WordCloud
        words={words}
        width={300}
        height={200}
        keyword={keyword}
        country_name={country_name}
      />
      <NewsSummary
        data={description}
        width={410}
        height={150}
        keyword={keyword}
        country_name={country_name}
      />
      <StackedColumns
        data={sentimentData}
        width={300}
        height={200}
        keyword={keyword}
        country_name={country_name}
      />
      <MentionChart
        data={data}
        width={300}
        height={200}
        keyword={keyword}
        country_name={country_name}
      />
      <NewsList
        news={newsData}
        width={400}
        keyword={keyword}
        country_name={country_name}
      />
    </div>
  );
};

export default WorldDetail;
