import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

interface Props {
  text: string;
  type: string;
}

// toastify 구성 파일
export const autoClose = 800;

export const notify = ({ type, text }: Props) => {
  switch (type) {
    case 'default': // 텍스트만 나옴
      toast(text);
      break;
    case 'success': // 초록색 체크 아이콘
      toast.success(text);
      break;
    case 'warning': // 노랑
      toast.warning(text);
      break;
    case 'error': //빨강
      toast.error(text);
      break;
    case 'info': //파랑 아이콘
      toast.info(text);
      break;
  }
};

const Toast = () => {
  return (
    <ToastContainer
      position="top-center"
      autoClose={autoClose}
      hideProgressBar={false}
      newestOnTop={false}
      closeOnClick
      rtl={false}
      pauseOnFocusLoss={false}
      draggable
      pauseOnHover={true}
      theme="light"
      limit={2}
      toastClassName="toast"
    />
  );
};

export default Toast;
