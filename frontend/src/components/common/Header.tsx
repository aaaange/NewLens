import { useEffect, useRef, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../../stores/useAuthStore';
import { DropdownMenu } from './DropdownMenu';
import { logOutApi } from '../../services/api/AuthService';

const Header = () => {
  const { accessToken, clearAccessToken } = useAuthStore();
  const isLogin = !!accessToken;

  const navigate = useNavigate();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);

  const dropdownRef = useRef<HTMLDivElement>(null);

  // 드롭다운 토글 함수
  const toggleDropdown = () => {
    setIsDropdownOpen(!isDropdownOpen);
  };

  // 외부 클릭 감지 및 드롭다운 닫기
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target as Node)
      ) {
        setIsDropdownOpen(false); // 외부 클릭 시 드롭다운 닫기
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const handleLogout = async () => {
    try {
      const response = await logOutApi(); // 로그아웃 API 호출
      setIsModalOpen(false); // 모달 닫기
      console.log(response);
    } catch (error) {
      console.log(error);
    } finally {
      clearAccessToken(); // Zustand에서 토큰 제거
      navigate('/login');
    }
  };

  return (
    <div>
      <header>
        <nav>
          <div className="flex items-center justify-between h-[90px] bg-background px-4">
            <div className="w-[120px] cursor-pointer">
              <Link to="/main">
                <img src="/assets/images/logo-newLens.png" alt="로고" />
              </Link>
            </div>

            <div className="flex items-end gap-4 mr-4">
              <div className="w-[44px]">
                <Link to="/">
                  <img src="/assets/images/info.png" alt="서비스소개" />
                </Link>
              </div>
              <div className="w-[35px]">
                <Link to="/koreaAnalysis">
                  <img
                    src="/assets/images/domesticNews.png"
                    alt="한국특화페이지"
                  />
                </Link>
              </div>
              {isLogin ? (
                <div className="flex items-center gap-4">
                  {/* 프로필 사진 및 드롭다운 */}
                  <div
                    className="w-[40px] cursor-pointer relative"
                    onClick={toggleDropdown}
                    ref={dropdownRef} // 드롭다운 참조 추가
                  >
                    <img src="/assets/images/profile.png" alt="프로필사진" />
                    {isDropdownOpen && (
                      // 드롭다운 메뉴
                      <div className="absolute top-[50px] right-0 z-200">
                        <DropdownMenu
                          list={[
                            {
                              text: '프로필 보기',
                              onClick: () => {
                                navigate('/mypage');
                                console.log('프로필 보기 클릭됨');
                                setIsDropdownOpen(false); // 닫기
                              },
                            },
                            {
                              text: '로그아웃',
                              onClick: () => {
                                handleLogout();
                                setIsDropdownOpen(false);
                              },
                            },
                          ]}
                        />
                      </div>
                    )}
                  </div>
                </div>
              ) : (
                // 로그인 버튼
                <div className="w-[35px] ">
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
