import { useState, useEffect } from 'react';
import { api } from '../services/api/Api';
import { isAxiosError } from 'axios';
import { getCountryDataApi } from '../services/api/worldService';

export interface KeywordData {
  name: string;
  count: number;
}

export interface SentimentData {
  period: string;
  positive: number;
  neutral: number;
  negative: number;
}

export interface MentionData {
  period: string;
  count: number;
}

export interface ArticleData {
  title: string;
  url: string;
  published_date: string;
  image_url: string;
}

export interface VideoData {
  title: string;
  url: string;
  published_date: string;
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
  keyword: string | string[];
  category: string;
  period: string;
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
