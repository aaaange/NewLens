import { useEffect, useState } from 'react';
import axios from 'axios';

interface CompareParams {
  category: string;
  period: string;
  keyword: string[];
  country: string[]; // 꼭 2개
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
        params.country.length !== 2 ||
        !params.category ||
        !params.period ||
        params.keyword.length === 0
      ) {
        return;
      }

      setIsLoading(true);
      try {
        const response = await axios.get('/compare-info', {
          params,
        });
        setData(response.data.data);
      } catch (err) {
        if (axios.isAxiosError(err)) {
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
