import { useState } from 'react';
import { Link } from 'react-router-dom';

const Header = () => {
  const [isLogin, setIsLogin] = useState(true);
  return (
    <div>
      <header>
        <nav>
          <div className="flex items-center justify-between shadow-light h-[100px] bg-background px-4">
            <div className="w-[120px]">
              <Link to="/main">
                <img src="/assets/images/logo-newLens.png" alt="로고" />
              </Link>
            </div>
            <div>
              <Link to="/">about</Link>
            </div>
            <div className="w-[120px]">
              <Link to="/playground">양영조페이지</Link>
            </div>
            <div className="w-[120px]">
              <Link to="/main">이승주페이지</Link>
            </div>

            <div className="flex items-center gap-4 mr-4">
              <div className="w-[40px]">
                <Link to="/koreaAnalysis">
                  <img
                    src="/assets/images/domesticNews.png"
                    alt="한국특화페이지"
                  />
                </Link>
              </div>
              {isLogin ? (
                <div className="flex items-center gap-4">
                  <div className="w-[47px]">
                    <Link to="/">
                      <img src="/assets/images/signout.png" alt="로그아웃" />
                    </Link>
                  </div>
                  <div className="w-[40px]">
                    <Link to="/mypage">
                      <img src="/assets/images/notifi.png" alt="알람" />
                    </Link>
                  </div>
                  <div className="w-[40px]">
                    <Link to="/mypage">
                      <img src="/assets/images/profile.png" alt="프로필사진" />
                    </Link>
                  </div>
                </div>
              ) : (
                <div className="w-[40px]">
                  <Link to="/login">
                    <img src="/assets/images/signin.png" alt="로그인" />
                  </Link>
                </div>
              )}
            </div>
          </div>
        </nav>
      </header>
    </div>
  );
};

export default Header;
