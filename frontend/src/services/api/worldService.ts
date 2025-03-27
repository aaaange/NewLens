// world에 관련한 api를 작성하는 곳.
import { CountryParams } from '../../hooks/useCountryData';
import { CompareParams } from '../../hooks/useCompareInfo';
import { api } from './Api';

//==============================================
// 인터페이스 정의
//==============================================
export interface WorldMapData {
  mention: mentionResType;
  sentiment: sentimentResType;
}

export type mentionResType = { name: string; count: number }[];

export interface mentionObjType {
  [key: string]: number;
}

export interface countryNameType {
  [key: string]: string;
}

export type sentimentResType = {
  name: string;
  positive: number;
  neutral: number;
  negative: number;
}[];

export type SentimentName = 'positive' | 'neutral' | 'negative';

// 특정 국가의 감정 타입
export interface sentimentType {
  positive: number;
  neutral: number;
  negative: number;
  primarySentiment: SentimentName;
}

// 모든 국가의 감정 타입
export interface sentimentObjType {
  [countryCode: string]: sentimentType;
}

export interface NewsModalReqType {
  category: string;
  period: number;
  keyword: string;
  'keyword-mind': string;
  'keyword-cloud': string;
  country: string;
  page: number;
  size: number;
}

interface MindMapReqType {
  category: string;
  period: number;
  keyword: string;
  is_korea: boolean;
}

//==============================================
// API 정의
//==============================================
export const getWorldMapDataApi = async (
  category: string,
  period: number,
  keyword: string
) => {
  const response = await api.get(`search/worldwide`, {
    params: { category, period, keyword },
  });
  return response.data;
};

//메인 완
export const getMindMapApi = async (params: MindMapReqType) => {
  const response = await api.get(`search/extract-related_words`, { params });
  return response.data;
};

//메인 완
export const getKeywordRankingApi = async (
  category: string,
  period: number,
  is_korea: boolean
) => {
  const response = await api.get(`search/keyword-ranking`, {
    params: { category, period, is_korea },
  });
  return response.data;
};

// api 논의후
export const getNewsListForModalApi = async (params: NewsModalReqType) => {
  const response = await api.get(`search/country/news-modal`, { params });
  return response.data;
};

export const getCountryDataApi = async (params: CountryParams) => {
  const response = await api.get('search/country/dashboard', { params });
  return response.data; // { code, success, message, data }
};

export const getCompareInfoApi = async (params: CompareParams) => {
  const response = await api.get('search/ountry/compare-info', { params });
  return response.data;
};
