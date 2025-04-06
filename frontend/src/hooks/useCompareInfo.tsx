import { useEffect, useState } from 'react';
import { isAxiosError } from 'axios';
import { getCompareInfoApi } from '../services/api/worldService';

export interface CompareParams {
  category: string;
  period: number;
  keyword: string;
  keyword_mind: string;
  country1: string;
  country2: string;
}

interface CompareResponse {
  analysis: string;
}

const useCompareInfo = (params: CompareParams | null) => {
  const [data, setData] = useState<CompareResponse | null>(null);
  const [error, setError] = useState<Error | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);

  useEffect(() => {
    const fetchData = async () => {
      if (
        !params ||
        !params.country1 ||
        !params.country2 ||
        !params.category ||
        !params.period ||
        !params.keyword
      ) {
        return;
      }

      setIsLoading(true);
      try {
        if (!params) {
          throw new Error('params 확인필요');
        }
        const response = await getCompareInfoApi(params);
        setData({ analysis: response.data.analysis });
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
  }, [JSON.stringify(params)]); // 객체 참조 문제 방지

  return { data, isLoading, error };
};

export default useCompareInfo;
