import { ReactNode, useState, useEffect } from 'react';

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  children: ReactNode;
}

const Modal = ({ isOpen, onClose, children }: ModalProps) => {
  const [showAnimation, setShowAnimation] = useState(false);

  useEffect(() => {
    if (isOpen) {
      // 다음 tick에 클래스가 적용되도록
      requestAnimationFrame(() => setShowAnimation(true));
    } else {
      setShowAnimation(false);
    }
  }, [isOpen]);

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      {/* 배경 블러 + 반투명 */}
      <div
        className="absolute inset-0 z-40 backdrop-blur-sm"
        style={{ backgroundColor: 'rgba(0, 0, 0, 0.4)' }}
        onClick={onClose}
      />
      {/* 모달 본문 */}
      <div
        className={`
          relative bg-white rounded-xl p-6 w-80 shadow-xl z-50
          transition-all duration-300 ease-out
          ${showAnimation ? 'opacity-100 scale-100' : 'opacity-0 scale-95'}
        `}
        onClick={(e) => e.stopPropagation()}
      >
        {children}
      </div>
    </div>
  );
};

export default Modal;
