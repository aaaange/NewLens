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
import PrivateRoute from './PrivateRoute';

const Router = () => {
  return (
    <Routes>
      {/* 헤더가 없는 페이지 */}
      <Route element={<AuthLayout />}>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/" element={<Landing />} />
      </Route>
      <Route element={<Layout />}>
        <Route path="/main" element={<MainPage />} />
        <Route path="/koreaAnalysis" element={<KoreaAnalysisPage />} />
        <Route element={<PrivateRoute />}>
          <Route path="/mypage" element={<MyPage />} />
        </Route>
        <Route
          path="/worldDetail/:country/:category/:period/:keyword/:keyword_mind?"
          element={<WorldDetail />}
        />
      </Route>
    </Routes>
  );
};

export default Router;
