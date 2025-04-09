// scrap에 관련한 api
import {
  ScrapNewsActionParams,
  ScrapNewsGetParams,
} from '../../hooks/useMypageNews';
import { api } from './Api';

export const getScrapNewsApi = async (params: ScrapNewsGetParams) => {
  const response = await api.get('user/scrap/news', { params });
  return response.data;
};

export const postScrapNewsApi = async (data: ScrapNewsActionParams) => {
  const response = await api.post('user/scrap/news/toggle', data);
  return response.data;
};

// export const deleteScrapNewsApi = async (data: ScrapNewsActionParams) => {
//   const response = await api.delete('scrap/news', { data });
//   return response.data;
// };

// recommend 관련 api
export const getRecommendNewsApi = async () => {
  const response = await api.get('user/recommend/news');
  return response.data;
};

// log 관련 api
// log 조회
export const postNewslogApi = async () => {
  const response = await api.post('user/log/news');
  return response.data;
};

// log 기록
export const postAccessLogApi = async (data: ScrapNewsActionParams) => {
  const response = await api.post('user/log/access', data);
  return response.data;
};
