import { useState, useEffect } from 'react';
import { isAxiosError } from 'axios';
import { getDescriptionApi } from '../services/api/worldService';

export interface DescriptionParams {
  category: string;
  period: number;
  keyword: string;
  keyword_mind: string;
  country: string;
  is_korea: boolean;
}

const useDescription = (params: DescriptionParams) => {
  const [data, setData] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<Error | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      setIsLoading(true);
      try {
        const response = await getDescriptionApi(params);
        setData(response.analysis);
      } catch (err) {
        console.error('❌ GPT 요약 API 호출 중 에러 발생:', err);
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
    params.keyword_mind,
  ]);

  return { data, isLoading, error };
};

export default useDescription;
