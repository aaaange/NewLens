import { useState } from 'react';
import {
  getScrapNewsApi,
  postScrapNewsApi,
  // deleteScrapNewsApi,
  postNewslogApi,
  getRecommendNewsApi,
  postAccessLogApi,
} from '../services/api/mypageService';

export interface ScrapNewsGetParams {
  page: number;
  size: number;
}

export interface ScrapNewsActionParams {
  news_id: string;
}

export interface News {
  news_id: string;
  title: string;
  url: string;
  published_at: string;
  country: string;
  keywords: string[];
  image_url: string;

  // 선택적 필드로 선언
  is_scrap?: boolean;
  visited_at?: string;
}

export const useScrapNews = () => {
  const [scrapNewsList, setScrapNewsList] = useState<News[]>([]);
  const [recommendNewsList, setRecommendNewsList] = useState<News[]>([]);
  const [logNewsList, setLogNewsList] = useState<News[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  // GET SCRAP
  const fetchScrapNews = async (page = 1, size = 5) => {
    try {
      setLoading(true);
      const response = await getScrapNewsApi({ page, size });
      return response.data; // 전체 데이터 반환
    } catch (err) {
      setError(err as Error);
      return null;
    } finally {
      setLoading(false);
    }
  };

  // POST SCRAP
  const scrapNews = async (
    news_id: string
  ): Promise<{ is_scrap: boolean } | null> => {
    try {
      const response = await postScrapNewsApi({ news_id });
      return response.data; // { isScrap: true/false } 리턴
    } catch (err) {
      setError(err as Error);
      return null;
    }
  };

  // DELETE SCRAP
  // const removeScrapNews = async (news_id: string) => {
  //   try {
  //     await deleteScrapNewsApi({ news_id: news_id });
  //   } catch (err) {
  //     setError(err as Error);
  //   }
  // };

  // LOG 조회
  const fetchNewsLog = async () => {
    try {
      setLoading(true);
      const response = await postNewslogApi();
      setLogNewsList(response.data.news); // 응답 형식 다시 확인하기기
    } catch (err) {
      setError(err as Error);
    } finally {
      setLoading(false);
    }
  };

  // LOG 기록
  const fetchtAccessLog = async (news_id: string) => {
    try {
      const response = await postAccessLogApi({ news_id });
      console.log(news_id);

      return response.data;
    } catch (err) {
      setError(err as Error);
      return null;
    }
  };

  // GET RECOMMEND
  const fetchRecommendNews = async () => {
    try {
      setLoading(true);
      const response = await getRecommendNewsApi();
      setRecommendNewsList(response.data.news); // 응답 형식 다시 확인하기
    } catch (err) {
      setError(err as Error);
    } finally {
      setLoading(false);
    }
  };

  return {
    scrapNewsList,
    recommendNewsList,
    logNewsList,
    loading,
    error,
    fetchScrapNews,
    scrapNews,
    fetchNewsLog,
    fetchtAccessLog,
    fetchRecommendNews,
  };
};
