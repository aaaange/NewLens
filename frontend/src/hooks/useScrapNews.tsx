import { useState } from 'react';
import {
  getScrapNewsApi,
  postScrapNewsApi,
  deleteScrapNewsApi,
} from '../services/api/scrapService';

export interface ScrapNewsGetParams {
  size: number;
}

export interface ScrapNewsActionParams {
  news_id: string;
}

export interface News {
  news_id: string;
  title: string;
  url: string;
  public_at: string;
  country: string;
  keywords: string[];
  image_url: string;
}

export const useScrapNews = () => {
  const [newsList, setNewsList] = useState<News[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  // GET
  const fetchScrapNews = async (size = 5) => {
    try {
      setLoading(true);
      const response = await getScrapNewsApi({ size });
      setNewsList(response.data.news); // 형식 이거 맞는지 확인하기
    } catch (err) {
      setError(err as Error);
    } finally {
      setLoading(false);
    }
  };

  // POST
  const scrapNews = async (newsId: string) => {
    try {
      await postScrapNewsApi({ news_id: newsId });
    } catch (err) {
      setError(err as Error);
    }
  };

  // DELETE
  const removeScrapNews = async (newsId: string) => {
    try {
      await deleteScrapNewsApi({ news_id: newsId });
    } catch (err) {
      setError(err as Error);
    }
  };

  return {
    newsList,
    loading,
    error,
    fetchScrapNews,
    scrapNews,
    removeScrapNews,
  };
};
