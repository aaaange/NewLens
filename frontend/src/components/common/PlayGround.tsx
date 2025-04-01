import React, { useRef, useEffect } from 'react';
import Globe, { GlobeMethods } from 'react-globe.gl';

const PlayGround = () => {
  const globeRef = useRef<GlobeMethods>();

  useEffect(() => {
    if (globeRef.current) {
      globeRef.current.controls().autoRotate = true; // ✅ 자동 회전 활성화
      globeRef.current.controls().autoRotateSpeed = 2; // ✅ 회전 속도 조절 (기본값: 2)
      globeRef.current.pointOfView({ lat: 0, lng: 0, altitude: 2 });
    }
  }, []);

  return (
    <div style={{ width: '100vw', height: '100vh', background: '#011728' }}>
      <Globe
        ref={globeRef as React.MutableRefObject<GlobeMethods | undefined>}
        globeImageUrl="https://unpkg.com/three-globe/example/img/earth-blue-marble.jpg"
        backgroundColor="#011728"
        bumpImageUrl="https://raw.githubusercontent.com/vasturiano/three-globe/master/example/img/earth-topology.png"
      />
    </div>
  );
};

export default PlayGround;
