import React, { useRef, useEffect } from 'react';
import Globe, { GlobeMethods } from 'react-globe.gl';

const PlayGround = () => {
  const globeRef = useRef<GlobeMethods>(); // ✅ undefined 초기화

  useEffect(() => {
    if (globeRef.current) {
      globeRef.current.pointOfView({ lat: 0, lng: 0, altitude: 2 });
    }
  }, []);

  return (
    <div style={{ width: '100vw', height: '100vh', background: '#000' }}>
      <Globe
        ref={globeRef as React.MutableRefObject<GlobeMethods | undefined>} // ✅ 타입 캐스팅
        globeImageUrl="//unpkg.com/three-globe/example/img/earth-blue-marble.jpg"
        bumpImageUrl="//unpkg.com/three-globe/example/img/earth-topology.png"
      />
    </div>
  );
};

export default PlayGround;
