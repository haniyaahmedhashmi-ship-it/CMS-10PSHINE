import React, { useRef, useMemo, useContext } from 'react';
import { Canvas, useFrame } from '@react-three/fiber';
import { Float, Points, PointMaterial } from '@react-three/drei';
import * as random from 'maath/random/dist/maath-random.esm';
import { ThemeContext } from '../context/ThemeContext';

function ParticleField() {
  const ref = useRef();
  const { theme } = useContext(ThemeContext);

  // Reduce particles on mobile for better performance
  const particleCount = typeof window !== 'undefined' && window.innerWidth < 768 ? 2000 : 5000;
  
  // Generate random points inside a sphere
  const sphere = useMemo(() => random.inSphere(new Float32Array(particleCount), { radius: 10 }), [particleCount]);

  useFrame((state, delta) => {
    // Slowly rotate the particle field
    if (ref.current) {
      ref.current.rotation.x -= delta / 10;
      ref.current.rotation.y -= delta / 15;
    }
  });

  return (
    <group rotation={[0, 0, Math.PI / 4]}>
      <Points ref={ref} positions={sphere} stride={3} frustumCulled={false}>
        <PointMaterial
          transparent
          color={theme === 'dark' ? '#3b82f6' : '#60a5fa'}
          size={0.05}
          sizeAttenuation={true}
          depthWrite={false}
          opacity={0.4}
        />
      </Points>
    </group>
  );
}

const Background3D = () => {
  return (
    <div id="canvas-container">
      <Canvas camera={{ position: [0, 0, 5] }} dpr={[1, 1.5]}>
        <Float speed={1} rotationIntensity={1} floatIntensity={2}>
          <ParticleField />
        </Float>
      </Canvas>
    </div>
  );
};

export default Background3D;
