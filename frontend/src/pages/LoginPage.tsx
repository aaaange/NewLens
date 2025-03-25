const LoginPage = () => {
  return (
    <div className="bg-black h-screen flex justify-center items-center">
      <div className="flex flex-col w-1/3 items-center gap-3">
        <img
          src="/assets/images/logo-newLens.png"
          alt="로고"
          // className="w-1/3"
        />
        <div className="flex flex-col items-center text-2xl">
          <span>실시간으로 변화하는 세계의 모든 이슈, </span>
          <span>한번에 분석하고 각 국의 반응을 비교하세요!</span>
        </div>
        <img
          src="/assets/images/kakao_login.png"
          alt="카카오 로그인"
          className="w-2/3"
        />
        <img
          src="/assets/images/google_login.png"
          alt="구글 로그인"
          className="w-2/3"
        />
        <img
          src="/assets/images/ssafy_login.png"
          alt="싸피 로그인"
          className="w-2/3"
        />
      </div>
      <div>
        <img
          src="/assets/images/earth_login.gif"
          alt="지구"
          // className="w-2/3"
        />
      </div>
    </div>
  );
};

export default LoginPage;
