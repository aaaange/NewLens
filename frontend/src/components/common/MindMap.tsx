import React, {
  useState,
  useEffect,
  useCallback,
  useMemo,
  useRef,
} from 'react';
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
import { getMindMapApi } from '../../services/api/worldService';
import CustomNode from './CustomNode';

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

interface MindMapProps {
  onKeywordChange: (keyword: string) => void;
  category: string;
  period: number;
  mainKeyword: string;
  isKorea: boolean;
  keyword_mind: string;
  fetchWorldData: (keyword: string, mind: string) => void; // 추가된 prop
}

const MindMap = ({
  onKeywordChange,
  category,
  period,
  mainKeyword,
  isKorea,
  keyword_mind,
  fetchWorldData,
}: MindMapProps) => {
  const nodeTypes = useMemo(() => ({ custom: CustomNode }), []);

  const [nodes, setNodes] = useNodesState([]);
  const [edges, setEdges] = useEdgesState([]);
  const [selectedNodeId, setSelectedNodeId] = useState('');
  const onConnect = useCallback(
    (params: Connection) =>
      setEdges((eds) =>
        addEdge({ ...params, style: { stroke: '#000', strokeWidth: 3 } }, eds)
      ),
    [setEdges]
  );
  useEffect(() => {
    if (keyword_mind) {
      const targetNode = nodes.find((node) => node.data.label === keyword_mind);
      if (targetNode) {
        setSelectedNodeId(targetNode.id); // 노드 ID 업데이트
      }
    }
  }, [keyword_mind, nodes]);

  useEffect(() => {
    setSelectedNodeId('');
  }, [mainKeyword]);

  const onNodeClick = useCallback(
    (event: React.MouseEvent, node: Node) => {
      if (node.id === '1') return;

      if (selectedNodeId === node.id) {
        setSelectedNodeId('');
        onKeywordChange('');
        fetchWorldData('', '');
      } else {
        setSelectedNodeId(node.id);
        onKeywordChange(node.data.fullLabel);
        fetchWorldData('', node.data.fullLabel);
      }
    },
    [selectedNodeId, onKeywordChange, fetchWorldData]
  );

  const fetchMindMapData = async () => {
    try {
      const params = {
        category: category,
        period: period,
        keyword: mainKeyword,
        is_korea: isKorea,
      };
      const response = await getMindMapApi(params);
      const { keyword: mainKeywordLabel, relatedKeywords } = response.data;

      const truncateLabel = (label: string, maxLength: number = 4) => {
        return label.length > maxLength
          ? label.slice(0, maxLength) + '…'
          : label;
      };
      const newNodes: Node[] = [
        {
          id: '1',
          type: 'custom',
          position: { x: 150 - 30, y: 140 - 20 },
          data: {
            label: mainKeywordLabel,
            backgroundColor: '#000',
            textColor: '#fff',
            width: 70,
            height: 50,
          },
        },
        ...relatedKeywords.map((label: string, index: number) => ({
          id: `${index + 2}`,
          type: 'custom',
          position: getCirclePosition(index, relatedKeywords.length),
          data: {
            label: truncateLabel(label),
            fullLabel: label,
            backgroundColor: getRandomColor(),
            textColor: '#fff',
          },
        })),
      ];

      const newEdges = newNodes.slice(1).map((node) => ({
        id: `e1-${node.id}`,
        source: '1',
        target: node.id,
        sourceHandle: `source-1`,
        targetHandle: `target-${node.id}`,
        type: 'straight',
        style: { stroke: '#e1e6ed', strokeWidth: 1 },
      }));
      setNodes(newNodes);
      setEdges(newEdges);
    } catch (error) {
      console.error('마인드맵 데이터 가져오기 실패:', error);
    }
  };

  useEffect(() => {
    setTimeout(() => {
      if (mainKeyword.length > 1) {
        fetchMindMapData();
      }
    }, 1000);
  }, [category, period, mainKeyword]);

  const centerX = 150;
  const centerY = 140;
  const radius = 100; // 원형 배치 반지름

  const getCirclePosition = (index: number, total: number) => {
    const angle = (index / total) * (2 * Math.PI);
    return {
      x: centerX + radius * Math.cos(angle) - 35,
      y: centerY + radius * Math.sin(angle) - 20,
    };
  };

  const updatedNodes = nodes.map((node) => ({
    ...node,
    data: {
      ...node.data,
      isSelected: node.id === selectedNodeId,
    },
  }));

  return (
    <div
      style={{
        width: '305px',
        height: '275px',
        backgroundColor: '#fff',
        borderRadius: '20px',
        boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
      }}
    >
      <div style={{ paddingLeft: '20px', paddingTop: '20px', color: 'black' }}>
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
          nodes={updatedNodes}
          edges={edges}
          onConnect={onConnect}
          onNodeClick={onNodeClick}
          fitView
          fitViewOptions={{ padding: 0.5 }} // 패딩을 줄여서 덜 축소되게 함
          minZoom={0.8} // 최소 줌 제한
          maxZoom={2} // 최대 줌 제한
          panOnDrag={false}
          zoomOnScroll={false}
          zoomOnDoubleClick={false}
          elementsSelectable={false}
          nodesDraggable={false}
          nodeTypes={nodeTypes}
          proOptions={{ hideAttribution: false }}
          style={{ cursor: 'default' }}
        >
          {/* <Controls showZoom={true} showFitView={true} showInteractive={true} /> */}
        </ReactFlow>
      </div>
    </div>
  );
};

export default MindMap;
