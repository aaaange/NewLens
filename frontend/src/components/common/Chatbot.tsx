import React, { useState } from 'react';
import { Fab, Box } from '@mui/material';
import ChatIcon from '@mui/icons-material/Chat';
import CloseIcon from '@mui/icons-material/Close';
const Chatbot = () => {
  const [messages, setMessages] = useState([
    { sender: 'bot', text: '안녕하세요! 무엇을 도와드릴까요?' },
  ]);
  const [input, setInput] = useState('');

  const handleSend = () => {
    if (!input.trim()) return;
    setMessages([...messages, { sender: 'user', text: input }]);
    setInput('');
    // 봇 응답 추가 (예시)
    setTimeout(() => {
      setMessages((prev) => [
        ...prev,
        { sender: 'bot', text: `입력하신 내용은 "${input}"입니다.` },
      ]);
    }, 1000);
  };

  return (
    <Box
      sx={{
        position: 'fixed',
        bottom: 80,
        right: 20,
        width: '300px',
        height: '400px',
        backgroundColor: '#fff',
        boxShadow: '0px 4px 10px rgba(0, 0, 0, 0.2)',
        borderRadius: '10px',
        overflow: 'hidden',
        zIndex: 999,
      }}
    >
      <div
        style={{
          padding: '10px',
          borderBottom: '1px solid #ddd',
          fontWeight: 'bold',
        }}
      >
        챗봇
      </div>
      <div
        style={{
          height: 'calc(100% - 40px)',
          overflowY: 'auto',
          padding: '10px',
        }}
      >
        {messages.map((msg, index) => (
          <div
            key={index}
            style={{ textAlign: msg.sender === 'user' ? 'right' : 'left' }}
          >
            <div
              style={{
                display: 'inline-block',
                padding: '8px',
                borderRadius: '10px',
                backgroundColor: msg.sender === 'user' ? '#d1e7dd' : '#f8d7da',
                marginBottom: '5px',
              }}
            >
              {msg.text}
            </div>
          </div>
        ))}
      </div>
      <div style={{ display: 'flex', gap: '8px', padding: '10px' }}>
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="메시지를 입력하세요..."
          style={{
            flexGrow: 1,
            padding: '8px',
            borderRadius: '5px',
            border: '1px solid #ccc',
          }}
        />
        <button
          onClick={handleSend}
          style={{ padding: '8px', borderRadius: '5px' }}
        >
          전송
        </button>
      </div>
    </Box>
  );
};

export default Chatbot;
