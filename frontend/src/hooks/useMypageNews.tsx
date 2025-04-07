import { useState } from 'react';
import {
  getScrapNewsApi,
  postScrapNewsApi,
  // deleteScrapNewsApi,
  postNewslogApi,
  getRecommendNewsApi,
} from '../services/api/mypageService';

export interface ScrapNewsGetParams {
  size: number;
}

export interface ScrapNewsActionParams {
  newsId: string;
}

export interface News {
  newsId: string;
  title: string;
  url: string;
  publishedAt: string;
  country: string;
  keywords: string[];
  imageUrl: string;

  // 선택적 필드로 선언
  isScrap?: boolean;
  visited_at?: string;
}

export const useScrapNews = () => {
  const [scrapNewsList, setScrapNewsList] = useState<News[]>([]);
  const [recommendNewsList, setRecommendNewsList] = useState<News[]>([]);
  const [logNewsList, setLogNewsList] = useState<News[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  // GET SCRAP
  const fetchScrapNews = async (size = 5) => {
    try {
      setLoading(true);
      const response = await getScrapNewsApi({ size });
      setScrapNewsList(response.data.news); // 형식 이거 맞는지 확인하기
    } catch (err) {
      setError(err as Error);
    } finally {
      setLoading(false);
    }
  };

  // POST SCRAP
  const scrapNews = async (
    newsId: string
  ): Promise<{ isScrap: boolean } | null> => {
    try {
      const response = await postScrapNewsApi({ newsId });
      return response.data.data; // { isScrap: true/false } 리턴
    } catch (err) {
      setError(err as Error);
      return null;
    }
  };

  // DELETE SCRAP
  // const removeScrapNews = async (newsId: string) => {
  //   try {
  //     await deleteScrapNewsApi({ newsId: newsId });
  //   } catch (err) {
  //     setError(err as Error);
  //   }
  // };

  // GET LOG
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
    // removeScrapNews,
    fetchNewsLog,
    fetchRecommendNews,
  };
};
