import { useEffect, useState } from 'react';
import {
  getUserInfoApi,
  patchNicknameApi,
} from '../../services/api/AuthService';
import Modal from '../common/Modal';

const MyProfile = () => {
  const [userInfo, setUserInfo] = useState({
    nickname: '',
    email: '',
    profileImage: '',
    providerName: '',
  });
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [newNickname, setNewNickname] = useState('');

  const handleNicknameUpdate = async () => {
    const nicknameRegex = /^[a-zA-Z가-힣0-9]+$/;

    if (!nicknameRegex.test(newNickname)) {
      alert(
        '특수문자 없이 한글(자음만, 모음만x), 영어 또는 숫자만 입력 가능합니다.'
      );
      return;
    }

    try {
      await patchNicknameApi(newNickname);
      setUserInfo((prev) => ({ ...prev, nickname: newNickname }));
      setIsModalOpen(false);
    } catch (err) {
      console.error('닉네임 변경 실패:', err);
    }
  };

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const res = await getUserInfoApi();
        setUserInfo(res.data); // res 자체에 data가 감싸져 있을 경우
      } catch (err) {
        console.error('유저 정보 조회 실패:', err);
      }
    };

    fetchUserInfo();
  }, []);

  return (
    <div>
      <div className="flex flex-col gap-4">
        <h1 className="text-2xl">나의 활동</h1>
        <div className="flex w-full border-1 rounded-2xl p-4">
          <img
            src={
              userInfo.profileImage && userInfo.profileImage.trim() !== ''
                ? userInfo.profileImage
                : '/assets/images/blank_profile.png'
            }
            className="w-24 h-24"
            alt="프로필 이미지"
          />
          <div className="pl-4 w-full">
            <div className="flex gap-2 items-center">
              <div className="text-lg">{userInfo.nickname}</div>
              <img
                src="/assets/images/update_pen.png"
                className="w-2.5 h-4 cursor-pointer"
                onClick={() => {
                  setNewNickname(userInfo.nickname); // 현재 닉네임으로 초기화
                  setIsModalOpen(true);
                }}
              />
            </div>
            <div className="flex items-center gap-6">
              <div>
                <div className="text-primary-500">이름</div>
                <div className="text-primary-500">E-mail</div>
                <div className="text-primary-500">플랫폼</div>
              </div>
              <div>
                <div>{userInfo.nickname}</div>
                <div>{userInfo.email}</div>
                <div>{userInfo.providerName.toUpperCase()}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* 닉네임 수정 모달 */}
      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)}>
        <h2 className="headline-medium text-primary-950">닉네임 변경</h2>
        <div className="text-primary-500 body-small my-2">
          한글, 영어, 숫자로 20자 이내 입력 가능해요😊
        </div>
        <input
          type="text"
          minLength={2}
          maxLength={20}
          className="border-2 p-2 rounded w-full border-primary-500 text-primary-800"
          value={newNickname}
          onChange={(e) => setNewNickname(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === 'Enter') {
              handleNicknameUpdate();
            }
          }}
        />
        <div className="flex justify-end gap-2 mt-4">
          <button
            className="px-4 py-2 bg-gray-300 rounded cursor-pointer active:bg-gray-400 active:scale-95 transition duration-150"
            onClick={() => setIsModalOpen(false)}
          >
            취소
          </button>
          <button
            className="px-4 py-2 bg-blue-500 text-white rounded cursor-pointer active:bg-blue-600 active:scale-95 transition duration-150"
            onClick={handleNicknameUpdate}
          >
            저장
          </button>
        </div>
      </Modal>
    </div>
  );
};

export default MyProfile;
