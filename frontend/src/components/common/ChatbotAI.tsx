import React, { useEffect, useRef, useState } from 'react';
// import {
//   deleteChatHistory,
//   getChatHistory,
//   saveChatHistory,
// } from '../../lib/chatbot';
// import {
//   closeWebSocket,
//   connectWebSocket,
//   isWebSocketConnected,
//   sendMessageViaWebSocket,
// } from '../../lib/websocket';
// import { splitIntoMessages } from '../../util/chatbot';
// import { getUserIdFromToken } from '../../util/getUserIdFromToken';
// import { getUserInfoById } from '../../util/getUserInfoById';
// import { getLanguage, getTranslations } from '../../util/languageUtils';
import Chatbot from './Chatbot';

// 타입 정의
interface Message {
  sender: 'user' | 'assistant' | 'option' | 'recommend';
  text: string;
  type?: string;
}

const ChatbotAI = () => {
  // 상태 관리
  const [newMessage, setNewMessage] = useState<string>('');
  const [selectedBot, setSelectedBot] = useState<string>('');
  const [messages, setMessages] = useState<Message[]>([]);
  const [isTyping, setIsTyping] = useState<boolean>(false);
  const [currentApi, setCurrentApi] = useState<string>('');
  const [isBotSelected, setIsBotSelected] = useState<boolean>(false);
  const [hasSentInitialMessage, setHasSentInitialMessage] =
    useState<boolean>(false);
  const [userInfo, setUserInfo] = useState<string>('');
  const [language, setLanguage] = useState<string>(getLanguage());
  const messagesEndRef = useRef<HTMLDivElement>(null);

  // 언어 번역 데이터 가져오기
  const { chatbot: chatbotTranslations } = getTranslations(language);

  // 초기 메시지 저장
  const saveInitialMessage = async () => {
    const initialMessage: Message = {
      text: chatbotTranslations.initialMessage,
      sender: 'assistant',
      type: 'message',
    };
    setMessages([initialMessage]);
    await saveChatHistory('assistant', initialMessage.text, 'message');
  };

  // WebSocket 연결 관리
  useEffect(() => {
    if (!isBotSelected || !currentApi) return;

    connectWebSocket(currentApi, (newMessage) => {
      setMessages((prev) => [...prev, newMessage]);
    });

    return () => {
      closeWebSocket();
    };
  }, [isBotSelected, currentApi]);

  // 메시지 전송 로직
  const sendMessage = async () => {
    if (!newMessage.trim()) return;

    if (!isWebSocketConnected()) return;

    const userMessage: Message = { text: newMessage, sender: 'user' };
    setMessages((prev) => [...prev, userMessage]);
    setNewMessage('');
    setIsTyping(true);

    sendMessageViaWebSocket(newMessage);
  };

  // 메시지 입력 핸들러
  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter' && isBotSelected) {
      e.preventDefault();
      sendMessage();
    }
  };

  // 채팅 기록 가져오기
  useEffect(() => {
    const fetchChatHistory = async () => {
      const data = await getChatHistory();

      if (data.length > 0) {
        let newMessages: Message[] = [];
        let detectedBotType = '';

        data.forEach((msg) => {
          if (msg.type === 'option') {
            detectedBotType = msg.content;
            setSelectedBot(detectedBotType);
          }

          if (msg.role === 'assistant') {
            newMessages = [
              ...newMessages,
              ...splitIntoMessages(
                msg.content,
                'assistant',
                selectedBot
              ).filter((msg) => msg.text.trim() !== ''),
            ];
          } else {
            newMessages.push({ text: msg.content, sender: msg.role });
          }
        });

        if (detectedBotType) {
          setCurrentApi(
            detectedBotType === chatbotTranslations.chatT
              ? `${process.env.REACT_APP_WS_BASE_URL}/ws/chatT`
              : `${process.env.REACT_APP_WS_BASE_URL}/ws/chatF`
          );
          setSelectedBot(detectedBotType);
          setIsBotSelected(true);
          setHasSentInitialMessage(true);
        }

        setMessages(newMessages);
      } else {
        saveInitialMessage();
        setIsBotSelected(false);
        setHasSentInitialMessage(false);
      }
    };

    fetchChatHistory();
  }, []);

  // 메시지 변경 시 스크롤 처리
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  return (
    <div className="flex flex-col h-full w-full">
      {/* 헤더 */}
      <div className="sticky top-0 flex justify-between items-center p-4 bg-white border-b">
        <button className="text-blue-500">뒤로가기</button>
        <p className="text-lg font-bold">K-ing 챗봇</p>
        <button
          onClick={saveInitialMessage}
          className="px-3 py-1 bg-blue-500 text-white rounded"
        >
          새 대화 시작
        </button>
      </div>

      {/* 메시지 영역 */}
      <div className="flex-1 overflow-y-auto p-4">
        {messages.map((message, index) => (
          <div
            key={index}
            className={`flex ${
              message.sender === 'user' ? 'justify-end' : 'justify-start'
            } mb-2`}
          >
            <div
              className={`px-4 py-2 rounded-lg ${
                message.sender === 'user'
                  ? 'bg-blue-100 text-blue-800'
                  : 'bg-gray-100 text-gray-800'
              }`}
            >
              {message.text}
            </div>
          </div>
        ))}
        {/* 타이핑 인디케이터 */}
        {isTyping && (
          <div className="flex justify-start mb-2">
            <div className="px-4 py-2 rounded-lg bg-gray-100 text-gray-800">
              ...
            </div>
          </div>
        )}
        {/* 스크롤 포인트 */}
        <div ref={messagesEndRef}></div>
      </div>

      {/* 입력 영역 */}
      <div className="flex items-center p-4 border-t bg-white">
        <input
          type="text"
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder={
            isBotSelected
              ? chatbotTranslations.msgPlaceholder
              : chatbotTranslations.botPlaceholder
          }
          disabled={!isBotSelected}
          className="flex-grow px-4 py-2 border rounded-lg"
        />
        <button
          onClick={sendMessage}
          disabled={!isBotSelected}
          className={`ml-2 px-4 py-2 rounded-lg ${
            isBotSelected
              ? 'bg-blue-500 text-white'
              : 'bg-gray-300 text-gray-500'
          }`}
        >
          전송
        </button>
      </div>
    </div>
  );
};

export default ChatbotAI;
