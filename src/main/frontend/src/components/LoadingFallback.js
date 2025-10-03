import React from 'react';

const LoadingFallback = () => {
  return (
    <div className="App">
      <div className="container">
        <div style={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          minHeight: '60vh',
          textAlign: 'center'
        }}>
          <div className="spinner" style={{ 
            width: '60px', 
            height: '60px',
            marginBottom: '24px'
          }}></div>
          
          <h2 style={{ 
            color: '#fff', 
            marginBottom: '12px',
            textShadow: '0 2px 4px rgba(0,0,0,0.3)'
          }}>
            Loading Excel Comparator...
          </h2>
          
          <p style={{ 
            color: '#fff', 
            opacity: 0.9,
            textShadow: '0 1px 2px rgba(0,0,0,0.3)'
          }}>
            Please wait while the application initializes
          </p>
        </div>
      </div>
    </div>
  );
};

export default LoadingFallback;