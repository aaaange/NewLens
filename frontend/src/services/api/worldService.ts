// world에 관련한 api를 작성하는 곳.
import { api, multipartApi } from './Api';

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
export const getMindMapApi = async (
  category: string,
  period: number,
  keyword: string
) => {
  const response = await api.get(`search/extract-related_words`, {
    params: { category, period, keyword },
  });
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
export const getNewsListForModalApi = async (
  category: string,
  period: number,
  keyword: string,
  country: string,
  page: number,
  size: number
) => {
  const response = await api.get(`search/news`, {
    params: { category, period, keyword, country, page, size },
  });
  return response.data;
};
