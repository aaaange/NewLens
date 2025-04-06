import { useLocation } from 'react-router-dom';
import { useEffect } from 'react';

const PageListener = () => {
  const location = useLocation();

  useEffect(() => {
    if (!location.pathname.includes('/worldDetail')) {
      // 디테일 페이지가 아닐 때만 초기화
      localStorage.removeItem('firstCountry');
      localStorage.removeItem('secondCountry');
    }
  }, [location]);

  return null;
};

export default PageListener;
