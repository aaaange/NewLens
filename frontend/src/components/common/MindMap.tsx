import React, { useCallback, useEffect } from 'react';
import ReactFlow, {
  Controls,
  useEdgesState,
  useNodesState,
  addEdge,
  Connection,
  Edge,
  Node,
  NodeProps,
  Handle,
  Position,
} from 'reactflow';
import 'reactflow/dist/style.css';

const getRandomColor = () => {
  const colors = [
    '#7ed957', // 초록
    '#ffb74d', // 연한 주황
    '#81d4fa', // 연한 파랑
    '#ff8a80', // 연한 빨강
    '#ba68c8', // 연한 보라
    '#4db6ac', // 민트
    '#f06292', // 연한 핑크
    '#64b5f6', // 연한 파란색
    '#a5d6a7', // 연한 초록색
    '#d1c4e9', // 라벤더
    '#ffcc80', // 연한 오렌지
    '#c5e1a5', // 연한 초록색
    '#c2185b', // 연한 붉은색
    '#ffca28', // 부드러운 노랑
    '#80deea', // 연한 청록색
    '#b39ddb', // 연한 보라색
  ];
  return colors[Math.floor(Math.random() * colors.length)];
};

const CustomNode: React.FC<NodeProps> = ({ data, id }) => {
  return (
    <div
      style={{
        backgroundColor: data.backgroundColor || '#000',
        color: data.textColor || '#fff',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        textAlign: 'center',
        padding: '5px',
        fontSize: '14px',
        fontWeight: 'bold',
        borderRadius: '20px',
        width: data.width || 70,
        height: data.height || 40,
        position: 'relative',
      }}
    >
      {data.label}
      {/* ✅ 중앙 노드에 정확히 중앙 핸들 추가 */}
      {id === '1' && (
        <Handle
          type="source"
          position={Position.Top} // 🔥 중앙 노드의 정확한 중앙에서 출발
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
        position={Position.Bottom} // 🔥 다른 노드는 기존처럼 위쪽
        id={`target-${id}`}
        style={{ opacity: 0 }}
      />
    </div>
  );
};

// 🔹 노드 타입 설정
const nodeTypes = { custom: CustomNode };

// 📌 초기 데이터
const keyword = 'it';
const centerX = 150;
const centerY = 140;
const radius = 100; // 원형 배치 반지름

// 🎯 원형 배치 함수
const getCirclePosition = (index: number, total: number) => {
  const angle = (index / total) * (2 * Math.PI);
  return {
    x: centerX + radius * Math.cos(angle) - 35,
    y: centerY + radius * Math.sin(angle) - 20,
  };
};

// 🔹 노드 데이터 설정 (중앙 + 원형 배치)
const initialNodes: Node[] = [
  {
    id: '1',
    type: 'custom',
    position: { x: centerX - 30, y: centerY - 20 },
    data: {
      label: keyword,
      backgroundColor: '#000',
      textColor: '#fff',
      width: 70,
      height: 50,
    },
  },
  ...Array.from({ length: 10 }, (_, i) => ({
    id: `${i + 2}`,
    type: 'custom',
    position: getCirclePosition(i, 10),
    data: {
      label: [
        '산업',
        '전략',
        '교육',
        '시장',
        '글로벌',
        '한국',
        '기업',
        '투자',
        '운영',
        '전문가',
      ][i],
      backgroundColor: getRandomColor(),
      textColor: '#fff',
    },
  })),
];

const initialEdges: Edge[] = initialNodes.slice(1).map((node) => ({
  id: `e1-${node.id}`,
  source: '1',
  target: node.id,
  sourceHandle: `source-1`,
  targetHandle: `target-${node.id}`,
  type: 'straight',
  style: { stroke: '#e1e6ed', strokeWidth: 1 },
}));

const MindMap = () => {
  const [nodes, setNodes] = useNodesState(initialNodes);
  const [edges, setEdges] = useEdgesState(initialEdges);

  const onConnect = useCallback(
    (params: Connection) =>
      setEdges((eds) =>
        addEdge({ ...params, style: { stroke: '#000', strokeWidth: 3 } }, eds)
      ),
    [setEdges]
  );

  return (
    <div
      style={{
        width: '305px',
        height: '100%',
        backgroundColor: '#fff',
        borderRadius: '20px',
        boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
      }}
    >
      <div style={{ paddingLeft: '20px', paddingTop: '10px', color: 'black' }}>
        <div className="headline-small">연관어</div>
        <div className="text-tetiary-500 caption-small">
          추천 연관어를 선택하여 검색해보세요!
        </div>
      </div>

      <div
        style={{
          width: '305px',
          height: '200px',
          backgroundColor: '#fff',
          overflow: 'hidden',
          borderRadius: '20px',
          //   margin: '0 auto',
        }}
      >
        <ReactFlow
          nodes={nodes}
          edges={edges}
          onConnect={onConnect}
          fitView
          fitViewOptions={{ padding: 0.1 }}
          panOnDrag={false}
          zoomOnScroll={false}
          zoomOnDoubleClick={false}
          elementsSelectable={false}
          nodesDraggable={false}
          nodeTypes={nodeTypes}
          proOptions={{ hideAttribution: true }}
        >
          <Controls
            showZoom={false}
            showFitView={false}
            showInteractive={false}
          />
        </ReactFlow>
      </div>
    </div>
  );
};

export default MindMap;
