import { Route, Routes } from 'react-router-dom';
import MainPage from '../pages/MainPage';
import Layout from '../layouts/Layout';
import LoginPage from '../pages/LoginPage';
import AuthLayout from '../layouts/AuthLayout';
import Landing from '../pages/LandingPage';
import KoreaAnalysis from '../components/korea/KoreaAnalysis';
import WorldDetail from '../pages/WorldDetail';

const Router = () => {
  return (
    <Routes>
      {/* 헤더가 없는 페이지 */}
      <Route element={<AuthLayout />}>
        <Route path="/login" element={<LoginPage />} />
      </Route>
      <Route element={<Layout />}>
        <Route path="/" element={<Landing />} />
        <Route path="/main" element={<MainPage />} />
        <Route path="/koreaAnalysis" element={<KoreaAnalysis />} />
        <Route path="/worldDetail" element={<WorldDetail />} />
      </Route>
    </Routes>
  );
};

export default Router;
