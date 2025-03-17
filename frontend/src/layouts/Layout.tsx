import { Outlet } from 'react-router-dom';
import Header from '../components/common/Header';

const Layout = () => {
  return (
    <div className="min-h-screen bg-background">
      <Header />
      <main>
        <Outlet />
      </main>
    </div>
  );
};

export default Layout;
