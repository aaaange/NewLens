// world에 관련한 api를 작성하는 곳.
import { api, multipartApi } from './Api';

//==============================================
// 인터페이스 정의
//==============================================
export interface WorldMapProps {
  tabId: string;
  category: string;
  period: number;
  keyword: string;
}

export interface worldMentionType {
  [key: string]: number;
}

export interface countryNameType {
  [key: string]: string;
}

export type SentimentName = 'positive' | 'neutral' | 'negative';

// 특정 국가의 감정 타입
export interface sentimentType {
  positive: number;
  neutral: number;
  negative: number;
  primarySentiment: SentimentName;
}

export interface sentimentResType extends sentimentType {
  name: string;
}

// 모든 국가의 감정 타입
export interface worldSentimentType {
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
