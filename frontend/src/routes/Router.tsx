import { Route, Routes } from 'react-router-dom';
import MainPage from '../pages/MainPage';
import Layout from '../layouts/Layout';
import LoginPage from '../pages/LoginPage';
import AuthLayout from '../layouts/AuthLayout';
import Landing from '../pages/LandingPage';
import WorldDetail from '../pages/WorldDetail';
import KoreaAnalysisPage from '../pages/KoreaAnalysisPage';
import MyPage from '../pages/MyPage';
import PlayGround from '../components/common/PlayGround';

const Router = () => {
  return (
    <Routes>
      {/* 헤더가 없는 페이지 */}
      <Route element={<AuthLayout />}>
        <Route path="/login" element={<LoginPage />} />
      </Route>
      <Route element={<Layout />}>
        <Route path="/" element={<Landing />} />
        <Route path="/playground" element={<PlayGround />} />
        <Route path="/main" element={<MainPage />} />
        <Route path="/mypage" element={<MyPage />} />
        <Route path="/koreaAnalysis" element={<KoreaAnalysisPage />} />
        <Route
          path="/worldDetail/:country/:category/:period/:keyword"
          element={<WorldDetail />}
        />
      </Route>
    </Routes>
  );
};

export default Router;
