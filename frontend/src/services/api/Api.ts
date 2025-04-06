import axios, { AxiosInstance } from 'axios';
import { get } from 'lodash';

export const BASE_URL = import.meta.env.VITE_APP_API_URL;

axios.defaults.withCredentials = false;
axios.defaults.headers.common['Content-Type'] = 'application/json';

const setupInterceptors = (instance: AxiosInstance) => {
  instance.interceptors.request.use(
    (request) => {
      console.log('api: ', request.url, '호출됨.');

      // 리다이렉트된 페이지에서 토큰 추출
      const query = new URLSearchParams(window.location.search);
      const accessToken = query.get('accessToken');
      console.log(accessToken);

      const refreshToken = query.get('refreshToken');
      console.log(refreshToken);

      // localStorage 또는 상태 관리 저장
      if (refreshToken) {
        localStorage.setItem('refreshToken', refreshToken);
      }

      if (accessToken) {
        localStorage.setItem('accessToken', accessToken);
        request.headers.Authorization = 'Bearer ' + accessToken;
      }
      return request;
    },
    (error) => {
      return Promise.reject(error);
    }
  );

  instance.interceptors.response.use(
    (response) => {
      return response;
    },
    async (error) => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const errorState = error.status;

      // switch (errorState) {

      // }

      return Promise.reject(error);
    }
  );
};

const createInstance = (headers = {}): AxiosInstance => {
  const instance = axios.create({
    baseURL: BASE_URL,
    timeout: 30000,
    headers,
  });

  setupInterceptors(instance);

  return instance;
};

export const createAxiosInstance = (): AxiosInstance => createInstance();

export const createMultipartAxiosInstance = (): AxiosInstance =>
  createInstance({ 'Content-Type': 'multipart/form-data' });

export const api = createAxiosInstance();
export const multipartApi = createMultipartAxiosInstance();
