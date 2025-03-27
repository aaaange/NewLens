import { useState, useEffect } from 'react';
import { isAxiosError } from 'axios';
import { getCountryDataApi } from '../services/api/worldService';

export interface KeywordData {
  text: string;
  value: number;
}

export interface SentimentData {
  published_at: string;
  positive: number;
  neutral: number;
  negative: number;
}

export interface MentionData {
  published_at: string;
  count: number;
}

export interface ArticleData {
  title: string;
  url: string;
  published_at: string;
  image_url: string;
}

export interface VideoData {
  title: string;
  url: string;
  published_at: string;
  thumbnail_url: string;
}

export interface CountryApiData {
  keywords: KeywordData[];
  description: string;
  sentimentData: SentimentData[];
  mentions: MentionData[];
  articles: ArticleData[];
  videos: VideoData[];
}

export interface CountryParams {
  country: string;
  keyword: string;
  category: string;
  period: number;
  is_korea: boolean;
}

const useCountryData = (params: CountryParams) => {
  const [data, setData] = useState<CountryApiData | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<Error | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      setIsLoading(true);
      try {
        const response = await getCountryDataApi(params);
        setData(response.data.data);
      } catch (err) {
        if (isAxiosError(err)) {
          setError(new Error(err.response?.data?.message || err.message));
        } else {
          setError(err as Error);
        }
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, [
    params.country,
    params.keyword,
    params.category,
    params.period,
    params.is_korea,
  ]);

  return { data, isLoading, error };
};

export default useCountryData;
