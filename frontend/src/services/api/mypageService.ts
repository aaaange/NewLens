// scrap에 관련한 api
import {
  ScrapNewsActionParams,
  ScrapNewsGetParams,
} from '../../hooks/useMypageNews';
import { api } from './Api';

export const getScrapNewsApi = async (params: ScrapNewsGetParams) => {
  const response = await api.get('scrap/news', { params });
  return response.data;
};

export const postScrapNewsApi = async (data: ScrapNewsActionParams) => {
  const response = await api.post('scrap/news/toggle', data);
  return response.data;
};

// export const deleteScrapNewsApi = async (data: ScrapNewsActionParams) => {
//   const response = await api.delete('scrap/news', { data });
//   return response.data;
// };

// recommend 관련 api
export const getRecommendNewsApi = async () => {
  const response = await api.get('recommend/news');
  return response.data;
};

// log 관련 api
export const postNewslogApi = async () => {
  const response = await api.post('user/log/news');
  return response.data;
};
