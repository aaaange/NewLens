import { Outlet } from 'react-router-dom';
import Header from '../components/common/Header';

const Layout = () => {
  return (
    <div className="min-h-screen w-full bg-background">
      <Header />
      <main className="overflow-visible bg-background">
        <Outlet />
      </main>
    </div>
  );
};

export default Layout;
