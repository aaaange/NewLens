export const country_name: string = '미국';

export const country_code: string = 'US';

export const keyword: string = '속초 도련님인척하는 인천 도련님';

export type WordType = {
  text: string;
  value: number;
};

export const words: WordType[] = [
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
  published_at: string;
  positive: number;
  neutral: number;
  negative: number;
};

export const sentimentData: SentimentData[] = [
  { published_at: '2025-03-01', positive: 0.7, neutral: 0.2, negative: 0.1 },
  { published_at: '2025-03-02', positive: 0.6, neutral: 0.3, negative: 0.1 },
  { published_at: '2025-03-03', positive: 0.8, neutral: 0.15, negative: 0.05 },
];

export type MentionData = {
  published_at: string;
  count: number;
};

export const mentionData: MentionData[] = [
  { published_at: '2025-03-01', count: 121 },
  { published_at: '2025-03-02', count: 126 },
  { published_at: '2025-03-03', count: 110 },
  { published_at: '2025-03-04', count: 130 },
  { published_at: '2025-03-05', count: 90 },
  { published_at: '2025-03-06', count: 115 },
];

export type NewsItemType = {
  news_id: string;
  title: string;
  url: string;
  published_at: string;
  image_url: string;
};

export const newsData: NewsItemType[] = [
  {
    news_id: 'news-1',
    title: 'AI 기술의 발전과 미래',
    url: 'https://n.news.naver.com/article/366/0001062129?cds=news_media_pc&type=editn',
    published_at: '2025-03-11',
    image_url:
      'https://mimgnews.pstatic.net/image/origin/366/2025/03/19/1062129.jpg?type=nf168_108&ut=20250319124606',
  },
  {
    news_id: 'news-2',
    title: '챗봇이 바꾸는 고객 서비스',
    url: 'https://n.news.naver.com/article/366/0001062129?cds=news_media_pc&type=editn',
    published_at: '2025-03-10',
    image_url:
      'https://mimgnews.pstatic.net/image/origin/366/2025/03/19/1062129.jpg?type=nf168_108&ut=20250319124606',
  },
  {
    news_id: 'news-3',
    title: '챗봇이 바꾸는 고객 서비스',
    url: 'https://n.news.naver.com/mnews/article/018/0005968438',
    published_at: '2025-03-10',
    image_url:
      'https://mimgnews.pstatic.net/image/origin/366/2025/03/19/1062129.jpg?type=nf168_108&ut=20250319124606',
  },
  {
    news_id: 'news-4',
    title: '챗봇이 바꾸는 고객 서비스',
    url: 'https://n.news.naver.com/mnews/article/011/0004464558',
    published_at: '2025-03-10',
    image_url:
      'https://mimgnews.pstatic.net/image/origin/366/2025/03/19/1062129.jpg?type=nf168_108&ut=20250319124606',
  },
  {
    news_id: 'news-5',
    title: '챗봇이 바꾸는 고객 서비스',
    url: 'https://n.news.naver.com/mnews/article/277/0005565230',
    published_at: '2025-03-10',
    image_url:
      'https://mimgnews.pstatic.net/image/origin/366/2025/03/19/1062129.jpg?type=nf168_108&ut=20250319124606',
  },
];

export const description: string =
  "봄꽃이 개화하는 시기에 국내 여행객들이 가장 많이 찾는 여행지가 '제주도'라는 조사 결과가 나왔다. 12일 글로벌 여행 플랫폼 트립닷컴은 오는 25일~다음 달 30일 국내 여행객의 여행 추이를 공개했다. 제주시와 서귀포시가 1, 2위에 올랐다 지난해는 반대로 서귀포시가 1위, 제주시가 2위였다. 다음으로는 서울과 부산이 뒤를 이었다.";

const analysis: string = '한줄 비교 요약본 from gpt';

export type VideoItemType = {
  title: string;
  url: string;
  published_at: string;
  thumbnail_url: string;
};

export const videos: VideoItemType[] = [
  {
    title: 'G-DRAGON - POWER (Official Video)',
    url: 'https://youtu.be/NMjhjrBIrG8?si=qiVz-RwxTNtVomuY',
    published_at: '2025-03-11',
    thumbnail_url:
      'https://i.ytimg.com/vi/NMjhjrBIrG8/hqdefault.jpg?sqp=-oaymwEnCNACELwBSFryq4qpAxkIARUAAIhCGAHYAQHiAQoIGBACGAY4AUAB&rs=AOn4CLAGqn1htTmmis_gJ3WCAXl_sb4JHg',
  },
  {
    title: 'G-DRAGON - TOO BAD (feat. Anderson .Paak) (Official Video)',
    url: 'https://youtu.be/o9DhvbqYzns?si=z0o-ST5hR1WPVqJ0',
    published_at: '2025-03-10',
    thumbnail_url:
      'https://i.ytimg.com/vi/o9DhvbqYzns/hqdefault.jpg?sqp=-oaymwEnCPYBEIoBSFryq4qpAxkIARUAAIhCGAHYAQHiAQoIGBACGAY4AUAB&rs=AOn4CLBMTNwTl1ohpQtxXpB6o3-lJ33k-g',
  },
  {
    title: 'G-DRAGON - DRAMA (Official Video)',
    url: 'https://youtu.be/I8I51kSq448?si=3pUvce2mNg_cQqZw',
    published_at: '2025-03-10',
    thumbnail_url:
      'https://i.ytimg.com/vi/I8I51kSq448/hqdefault.jpg?sqp=-oaymwEnCPYBEIoBSFryq4qpAxkIARUAAIhCGAHYAQHiAQoIGBACGAY4AUAB&rs=AOn4CLDlkVggOMAhjX3Q7NCYizt895AOmg',
  },
  {
    title: 'BIGBANG(GD&T.O.P) - 쩔어(ZUTTER) M/V',
    url: 'https://youtu.be/D8t8A8E_Tqc?si=t6GXHKWNsVSR10aa',
    published_at: '2025-03-10',
    thumbnail_url:
      'https://i.ytimg.com/vi/D8t8A8E_Tqc/hq720.jpg?sqp=-oaymwEnCNAFEJQDSFryq4qpAxkIARUAAIhCGAHYAQHiAQoIGBACGAY4AUAB&rs=AOn4CLD8OswOHjC5EjNxZlg6UdI1qmlsOQ',
  },
  {
    title: 'BIGBANG - 우리 사랑하지 말아요(LETs NOT FALL IN LOVE) M/V',
    url: 'https://youtu.be/9jTo6hTZmiQ?si=qcni5e4OYw8jXwx6',
    published_at: '2025-03-10',
    thumbnail_url:
      'https://i.ytimg.com/vi/9jTo6hTZmiQ/hq720.jpg?sqp=-oaymwEnCNAFEJQDSFryq4qpAxkIARUAAIhCGAHYAQHiAQoIGBACGAY4AUAB&rs=AOn4CLDK_El2ASRQbYFp0khWwV2Rd9CfMg',
  },
  {
    title: 'BIGBANG - 맨정신(SOBER) M/V',
    url: 'https://youtu.be/MBNQgq56egk?si=IoxCzEBgO9aT8TNT',
    published_at: '2025-03-10',
    thumbnail_url:
      'https://i.ytimg.com/vi/MBNQgq56egk/hq720.jpg?sqp=-oaymwEnCNAFEJQDSFryq4qpAxkIARUAAIhCGAHYAQHiAQoIGBACGAY4AUAB&rs=AOn4CLDaX2YmHAuANIzq0fpkUEhIzxRoUg',
  },
  {
    title: 'BIGBANG - WE LIKE 2 PARTY M/V',
    url: 'https://youtu.be/oFmfi1vM7co?si=IWpai0hAcWfILycZ',
    published_at: '2025-03-10',
    thumbnail_url:
      'https://i.ytimg.com/vi/oFmfi1vM7co/hq720.jpg?sqp=-oaymwEnCNAFEJQDSFryq4qpAxkIARUAAIhCGAHYAQHiAQoIGBACGAY4AUAB&rs=AOn4CLDUNMNwba9ArctW1DHb9BwN9IVclw',
  },
];
