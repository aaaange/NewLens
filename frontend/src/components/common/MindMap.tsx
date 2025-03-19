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

// 🎨 랜덤 배경색
const getRandomColor = () => {
  const colors = ['#ffcc00', '#7ed957', '#ff9900']; // 노랑, 초록, 주황
  return colors[Math.floor(Math.random() * colors.length)];
};

// 📌 커스텀 노드 (핸들 추가!)
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
        border: '2px solid rgba(0,0,0,0.1)',
        position: 'relative',
      }}
    >
      {data.label}
      <Handle
        type="source"
        position={Position.Bottom}
        id={`source-${id}`}
        style={{ opacity: 0 }}
      />
      <Handle
        type="target"
        position={Position.Top}
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
const centerY = 150;
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
      width: 60,
      height: 40,
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
      textColor: '#000',
    },
  })),
];

// 📌 엣지 데이터
const initialEdges: Edge[] = initialNodes.slice(1).map((node) => ({
  id: `e1-${node.id}`,
  source: '1',
  sourceHandle: `source-1`, // 🔥 중앙 노드의 source 핸들 ID
  target: node.id,
  targetHandle: `target-${node.id}`, // 🔥 각 노드의 target 핸들 ID
  type: 'straight',
  style: { stroke: '#000', strokeWidth: 3 },
}));

const MindMap = () => {
  const [nodes, setNodes] = useNodesState(initialNodes);
  const [edges, setEdges] = useEdgesState(initialEdges);

  useEffect(() => {
    console.log('📌 엣지 리스트:', edges);
  }, [edges]);

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
        width: '400px',
        height: '400px',
        backgroundColor: '#fff',
        border: '1px solid #ccc',
        borderRadius: '8px',
        overflow: 'hidden',
      }}
    >
      <ReactFlow
        nodes={nodes}
        edges={edges}
        onConnect={onConnect}
        fitView
        fitViewOptions={{ padding: 0.1 }}
        defaultEdgeOptions={{ type: 'straight' }}
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
  );
};

export default MindMap;
