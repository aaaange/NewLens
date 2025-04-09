import { api } from './Api';

export const logOutApi = async () => {
  const response = await api.post('user/auth/logout');
  return response.data;
};

// PATCH 닉네임 변경
export const patchNicknameApi = async (nickname:string) => {
  const response = await api.patch('user/auth/nickname', nickname);
  return response.data;
};

// // POST 회원 탈퇴
// export const signOutApi = async () => {
//   const response = await api.post('user/auth/signout');
//   return response.data;
// };

// GET 회원 정보 조회
export const getUserInfoApi = async () => {
  const response = await api.get('user/auth/user_info');
  return response.data;
};
