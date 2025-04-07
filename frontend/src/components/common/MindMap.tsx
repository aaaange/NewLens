import React, { useState, useEffect, useCallback, useMemo } from 'react';
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

interface MindMapProps {
  onKeywordChange: (keyword: string) => void;
  category: string;
  period: number;
  mainKeyword: string;
  isKorea: boolean;
  keyword_mind: string;
  fetchWorldData: (keyword: string) => void; // 추가된 prop
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
  const [nodes, setNodes] = useNodesState([]);
  const [edges, setEdges] = useEdgesState([]);
  const [keyword, setKeyword] = useState('');
  const [selectedNodeId, setSelectedNodeId] = useState('');
  const nodeTypes = useMemo(() => ({ custom: CustomNode }), []);
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
        setKeyword(keyword_mind); // 키워드 상태도 동기화
      }
    }
  }, [keyword_mind, nodes]); // nodes 배열 변경시에도 재검색

  const onNodeClick = useCallback(
    (event: React.MouseEvent, node: Node) => {
      if (node.id === '1') return;

      if (selectedNodeId === node.id) {
        // 동일한 노드를 클릭한 경우 상태 초기화
        setKeyword('');
        setSelectedNodeId('');
        onKeywordChange('');
        // fetchWorldData('');
      } else {
        setKeyword(node.data.label);
        setSelectedNodeId(node.id);
        onKeywordChange(node.data.label);
        fetchWorldData(node.data.label);
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
            // label: truncateLabel(label),
            label: label,
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

      // API 응답이 없을 때 기본 데이터로 초기화
      const defaultNodes: Node[] = [
        {
          id: '1',
          type: 'custom',
          position: { x: 150 - 30, y: 140 - 20 },
          data: {
            label: 'it',
            backgroundColor: '#000',
            textColor: '#fff',
            width: 80,
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
              '티라노사우르스',
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

      const defaultEdges = defaultNodes.slice(1).map((node) => ({
        id: `e1-${node.id}`,
        source: '1',
        target: node.id,
        sourceHandle: `source-1`,
        targetHandle: `target-${node.id}`,
        type: 'straight',
        style: { stroke: '#e1e6ed', strokeWidth: 1 },
      }));

      setNodes(defaultNodes);
      setEdges(defaultEdges);
    }
  };
  useEffect(() => {
    fetchMindMapData();
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
          zoomOnScroll={true}
          zoomOnDoubleClick={false}
          elementsSelectable={false}
          nodesDraggable={false}
          nodeTypes={nodeTypes}
          proOptions={{ hideAttribution: true }}
          style={{ cursor: 'default' }}
        >
          {/* <Controls showZoom={true} showFitView={true} showInteractive={true} /> */}
        </ReactFlow>
      </div>
    </div>
  );
};

export default MindMap;
