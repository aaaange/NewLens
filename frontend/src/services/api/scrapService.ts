// scrap에 관련한 api 작성하는 곳
import {
  ScrapNewsActionParams,
  ScrapNewsGetParams,
} from '../../hooks/useScrapNews';
import { api } from './Api';

export const getScrapNewsApi = async (params: ScrapNewsGetParams) => {
  const response = await api.get('scrap/news', { params });
  return response.data;
};

export const postScrapNewsApi = async (data: ScrapNewsActionParams) => {
  const response = await api.post('scrap/news', data);
  return response.data;
};

export const deleteScrapNewsApi = async (data: ScrapNewsActionParams) => {
  const response = await api.delete('scrap/news', { data });
  return response.data;
};
