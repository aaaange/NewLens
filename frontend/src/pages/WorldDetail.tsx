import WordCloud from '../components/worldDetail/Wordcloud';
import StackedColumns from '../components/worldDetail/StackedColumns';
import MentionChart from '../components/worldDetail/MentionChart';
import CountryDropdown from '../components/worldDetail/CountryDropdown';
import NewsList from '../components/worldDetail/NewsList';
import NewsSummary from '../components/worldDetail/NewsSummary';
import GptSummary from '../components/worldDetail/GptSummary';
import VideoList from '../components/worldDetail/VideoList';

const country_name: string = '미국';

const country_code: string = 'US';

const keyword: string = '속초 도련님';

export type WordType = {
  text: string;
  value: number;
};

const words: WordType[] = [
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

export type SentimentData = {
  period: string;
  positive: number;
  neutral: number;
  negative: number;
};

const sentimentData: SentimentData[] = [
  { period: '2025-03-01', positive: 0.7, neutral: 0.2, negative: 0.1 },
  { period: '2025-03-02', positive: 0.6, neutral: 0.3, negative: 0.1 },
  { period: '2025-03-03', positive: 0.8, neutral: 0.15, negative: 0.05 },
];

export type MentionData = {
  period: string;
  count: number;
};

const data: MentionData[] = [
  { period: '2025-03-01', count: 121 },
  { period: '2025-03-02', count: 126 },
  { period: '2025-03-03', count: 110 },
  { period: '2025-03-04', count: 130 },
  { period: '2025-03-05', count: 90 },
  { period: '2025-03-06', count: 115 },
];

export type NewsItemType = {
  title: string;
  url: string;
  published_date: string;
  image_url: string;
};

const newsData: NewsItemType[] = [
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

const description: string =
  "봄꽃이 개화하는 시기에 국내 여행객들이 가장 많이 찾는 여행지가 '제주도'라는 조사 결과가 나왔다. 12일 글로벌 여행 플랫폼 트립닷컴은 오는 25일~다음 달 30일 국내 여행객의 여행 추이를 공개했다. 제주시와 서귀포시가 1, 2위에 올랐다 지난해는 반대로 서귀포시가 1위, 제주시가 2위였다. 다음으로는 서울과 부산이 뒤를 이었다.";

const analysis: string = '한줄 비교 요약본 from gpt';

export type VideoItemType = {
  title: string;
  url: string;
  published_date: string;
  thumbnail_url: string;
};

const videos: VideoItemType[] = [
  {
    title: 'G-DRAGON - POWER (Official Video)',
    url: 'https://youtu.be/NMjhjrBIrG8?si=qiVz-RwxTNtVomuY',
    published_date: '2025-03-11',
    thumbnail_url:
      'https://i.ytimg.com/vi/NMjhjrBIrG8/hqdefault.jpg?sqp=-oaymwEnCNACELwBSFryq4qpAxkIARUAAIhCGAHYAQHiAQoIGBACGAY4AUAB&rs=AOn4CLAGqn1htTmmis_gJ3WCAXl_sb4JHg',
  },
  {
    title: 'G-DRAGON - TOO BAD (feat. Anderson .Paak) (Official Video)',
    url: 'https://youtu.be/o9DhvbqYzns?si=z0o-ST5hR1WPVqJ0',
    published_date: '2025-03-10',
    thumbnail_url:
      'https://i.ytimg.com/vi/o9DhvbqYzns/hqdefault.jpg?sqp=-oaymwEnCPYBEIoBSFryq4qpAxkIARUAAIhCGAHYAQHiAQoIGBACGAY4AUAB&rs=AOn4CLBMTNwTl1ohpQtxXpB6o3-lJ33k-g',
  },
  {
    title: 'G-DRAGON - DRAMA (Official Video)',
    url: 'https://youtu.be/I8I51kSq448?si=3pUvce2mNg_cQqZw',
    published_date: '2025-03-10',
    thumbnail_url:
      'https://i.ytimg.com/vi/I8I51kSq448/hqdefault.jpg?sqp=-oaymwEnCPYBEIoBSFryq4qpAxkIARUAAIhCGAHYAQHiAQoIGBACGAY4AUAB&rs=AOn4CLDlkVggOMAhjX3Q7NCYizt895AOmg',
  },
];

const WorldDetail = () => {
  return (
    <div>
      <h2> 워드클라우드 테스트</h2>
      <CountryDropdown width="250px" height="60px" />
      <GptSummary analysis={analysis} width={900} height={125} />
      <WordCloud
        words={words}
        width={300}
        height={200}
        keyword={keyword}
        country_name={country_name}
        country_code={country_code}
      />
      <NewsSummary
        data={description}
        width={410}
        height={150}
        keyword={keyword}
        country_name={country_name}
        country_code={country_code}
      />
      <StackedColumns
        data={sentimentData}
        width={300}
        height={200}
        keyword={keyword}
        country_name={country_name}
        country_code={country_code}
      />
      <MentionChart
        data={data}
        width={300}
        height={200}
        keyword={keyword}
        country_name={country_name}
        country_code={country_code}
      />
      <NewsList
        news={newsData}
        width={400}
        keyword={keyword}
        country_name={country_name}
        country_code={country_code}
      />
      <VideoList
        videos={videos}
        width={410}
        height={265}
        keyword={keyword}
        country_name={country_name}
        country_code={country_code}
      />
    </div>
  );
};

export default WorldDetail;
