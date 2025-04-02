import { Outlet } from 'react-router-dom';
import Header from '../components/common/Header';
import { Fab, Box } from '@mui/material';
import ChatIcon from '@mui/icons-material/Chat';
import QuestionAnswerIcon from '@mui/icons-material/QuestionAnswer';

import CloseIcon from '@mui/icons-material/Close';
import { useState } from 'react';
import Chatbot from '../components/common/Chatbot';
import ChatbotAI from '../components/common/ChatbotAI';

const Layout = () => {
  const [isChatOpen, setIsChatOpen] = useState(false);

  // 챗봇 UI 토글 함수
  const toggleChat = () => {
    setIsChatOpen((prev) => !prev);
  };
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
