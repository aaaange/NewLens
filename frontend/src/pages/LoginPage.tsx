import { useNavigate } from 'react-router-dom';
import PlayGround from '../components/common/PlayGround';

const LoginPage = () => {
  const navigate = useNavigate();
  const handleBackpage = () => {
    navigate('/main');
  };

  const handleGoogleLogin = () => {
    window.location.href = `${import.meta.env.VITE_APP_GOOGLE_LOGIN}`;
  };
  const handleKakaoLogin = () => {
    window.location.href = `${import.meta.env.VITE_APP_KAKAO_LOGIN}`;
  };
  const handleSsafyLogin = () => {
    window.location.href = `${import.meta.env.VITE_APP_SSAFY_LOGIN}`;
  };

  return (
    <div className="bg-background h-screen flex justify-between items-center relative overflow-hidden">
      <div className="flex flex-col w-1/3 items-center gap-6 z-10 ml-[15vw]">
        <img
          src="/assets/images/logo-newLens.png"
          alt="로고"
          className="w-4/5"
        />
        <div className="flex flex-col items-center text-xl">
          <span>실시간으로 변화하는 세계의 모든 이슈, </span>
          <span>한번에 분석하고 각 국의 반응을 비교하세요!</span>
        </div>
        <div className="flex flex-col w-full gap-3 items-center">
          <img
            src="/assets/images/kakao_login.png"
            alt="카카오 로그인"
            className="w-2/4 transition-transform duration-300 ease-in-out hover:scale-105 cursor-pointer"
          />
          <img
            onClick={handleGoogleLogin}
            src="/assets/images/google_login.png"
            alt="구글 로그인"
            className="w-2/4 transition-transform duration-300 ease-in-out hover:scale-105 cursor-pointer"
          />
          <img
            src="/assets/images/ssafy_login.png"
            alt="싸피 로그인"
            className="w-2/4 transition-transform duration-300 ease-in-out hover:scale-105 cursor-pointer"
          />
        </div>
        <div
          onClick={handleBackpage}
          className="transition-transform duration-300 ease-in-out hover:scale-105 cursor-pointer"
        >
          ← 돌아가기
        </div>
      </div>
      <div className="absolute right-0 top-0 translate-x-[20vw] -translate-y-[3vh] z-0 ">
        <PlayGround />
      </div>
    </div>
  );
};

export default LoginPage;
