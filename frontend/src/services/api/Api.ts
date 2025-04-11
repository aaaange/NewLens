import axios, { AxiosInstance } from 'axios';
import { useAuthStore } from '../../stores/useAuthStore';

export const BASE_URL = import.meta.env.VITE_APP_API_URL;

axios.defaults.withCredentials = false;
axios.defaults.headers.common['Content-Type'] = 'application/json';
const setupInterceptors = (instance: AxiosInstance) => {
  instance.interceptors.request.use(
    (config) => {
      // console.log('API 호출:', config.url);

      const { accessToken } = useAuthStore.getState();

      if (accessToken) {
        config.headers.Authorization = `Bearer ${accessToken}`;
        config.withCredentials = true;
      }

      return config;
    },
    (error) => Promise.reject(error)
  );

  instance.interceptors.response.use(
    (response) => response,
    async (error) => {
      if (error.response?.status === 401) {
        // 인증 실패 시 로그아웃 처리
        useAuthStore.getState().clearAccessToken();
        console.error('인증 실패: 로그아웃 처리됨');
      }
      return Promise.reject(error);
    }
  );
};

export const createAxiosInstance = (): AxiosInstance => {
  const instance = axios.create({
    baseURL: BASE_URL,
    timeout: 30000,
    headers: { 'Content-Type': 'application/json' },
  });

  setupInterceptors(instance);
  return instance;
};

export const api = createAxiosInstance();
