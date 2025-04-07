import { api } from './Api';

export const logOutApi = async () => {
  const response = await api.post('user/auth/logout');
  return response.data;
};
