import React from 'react';
import { Handle, Position, NodeProps } from 'reactflow';

const CustomNode: React.FC<NodeProps> = ({ data, id }) => {
  return (
    <div
      title={data.fullLabel}
      style={{
        backgroundColor: data.backgroundColor || '#000',
        color: data.textColor || '#fff',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        textAlign: 'center',
        padding: '5px',
        fontSize: data.isSelected ? '20px' : '14px',
        fontWeight: 'bold',
        borderRadius: '20px',
        width: data.isSelected ? 90 : 70,
        height: data.isSelected ? 50 : 40,
        position: 'relative',
        cursor: 'pointer',
        border: '2px solid #ccc', // 선택되지 않은 노드는 연한 테두리
        boxShadow: data.isSelected ? '0 0 10px rgba(0, 0, 0, 0.2)' : 'none', // 선택된 노드만 그림자 효과
        opacity: data.isSelected ? 1 : 0.8, // 선택되지 않은 노드는 투명도 낮춤
        transition: 'all 0.1s ease', // 부드러운 전환 효과
        whiteSpace: 'nowrap',
      }}
    >
      {data.label}
      {/* 중앙 노드에 정확히 중앙 핸들 추가 */}
      {id === '1' && (
        <Handle
          type="source"
          position={Position.Top}
          id={`source-${id}`}
          style={{
            left: '50%',
            top: '50%',
            transform: 'translate(-50%, -50%)',
            opacity: 0,
          }}
        />
      )}
      <Handle
        type="target"
        position={Position.Bottom}
        id={`target-${id}`}
        style={{ opacity: 0 }}
      />
    </div>
  );
};
export default CustomNode;
