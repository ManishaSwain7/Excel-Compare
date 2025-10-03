import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import FileUploader from './components/FileUploader';
import StatsDashboard from './components/StatsDashboard';
import ComparisonResults from './components/ComparisonResults';
import { excelComparison } from './services/api';
import './App.css';

function App() {
  const [isLoading, setIsLoading] = useState(false);
  const [comparisonResult, setComparisonResult] = useState(null);
  const [comparisonId, setComparisonId] = useState(null);
  const [error, setError] = useState(null);
  const [currentStep, setCurrentStep] = useState('upload'); // upload, results

  const handleFilesSelected = async (fileA, fileB) => {
    setIsLoading(true);
    setError(null);
    
    try {
      const response = await excelComparison.compareFiles(fileA, fileB);
      setComparisonResult(response.result);
      setComparisonId(response.comparisonId);
      setCurrentStep('results');
    } catch (error) {
      console.error('Error comparing files:', error);
      setError(error.response?.data?.error || 'An error occurred while comparing files. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleStartOver = () => {
    setComparisonResult(null);
    setComparisonId(null);
    setError(null);
    setCurrentStep('upload');
  };

  const pageVariants = {
    initial: { opacity: 0, x: -100 },
    in: { opacity: 1, x: 0 },
    out: { opacity: 0, x: 100 }
  };

  const pageTransition = {
    type: 'tween',
    ease: 'anticipate',
    duration: 0.5
  };

  return (
    <div className="App">
      <div className="container">
        {/* Header */}
        <motion.header
          initial={{ opacity: 0, y: -50 }}
          animate={{ opacity: 1, y: 0 }}
          className="text-center"
          style={{ marginBottom: '40px' }}
        >
          <h1 style={{ 
            fontSize: '3rem', 
            fontWeight: 'bold', 
            background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent',
            marginBottom: '16px'
          }}>
            Excel Comparator
          </h1>
          <p style={{ 
            fontSize: '1.2rem', 
            color: '#fff', 
            textShadow: '0 2px 4px rgba(0,0,0,0.3)' 
          }}>
            Compare Excel files for document migration analysis
          </p>
          
          {currentStep === 'results' && (
            <motion.button
              initial={{ opacity: 0, scale: 0.8 }}
              animate={{ opacity: 1, scale: 1 }}
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={handleStartOver}
              className="btn btn-secondary"
              style={{ marginTop: '16px' }}
            >
              ← Start New Comparison
            </motion.button>
          )}
        </motion.header>

        {/* Error Message */}
        <AnimatePresence>
          {error && (
            <motion.div
              initial={{ opacity: 0, y: -20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -20 }}
              className="alert alert-error"
              style={{ marginBottom: '24px' }}
            >
              <strong>Error:</strong> {error}
              <button
                onClick={() => setError(null)}
                style={{ 
                  float: 'right', 
                  background: 'none', 
                  border: 'none', 
                  fontSize: '20px',
                  cursor: 'pointer',
                  color: '#721c24'
                }}
              >
                ×
              </button>
            </motion.div>
          )}
        </AnimatePresence>

        {/* Main Content */}
        <AnimatePresence mode="wait">
          {currentStep === 'upload' && (
            <motion.div
              key="upload"
              initial="initial"
              animate="in"
              exit="out"
              variants={pageVariants}
              transition={pageTransition}
            >
              <div className="card">
                <FileUploader 
                  onFilesSelected={handleFilesSelected} 
                  isLoading={isLoading}
                />
              </div>
            </motion.div>
          )}

          {currentStep === 'results' && comparisonResult && (
            <motion.div
              key="results"
              initial="initial"
              animate="in"
              exit="out"
              variants={pageVariants}
              transition={pageTransition}
            >
              <StatsDashboard comparisonResult={comparisonResult} />
              <ComparisonResults 
                comparisonResult={comparisonResult} 
                comparisonId={comparisonId}
              />
            </motion.div>
          )}
        </AnimatePresence>

        {/* Loading Overlay */}
        <AnimatePresence>
          {isLoading && (
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              style={{
                position: 'fixed',
                top: 0,
                left: 0,
                right: 0,
                bottom: 0,
                backgroundColor: 'rgba(0, 0, 0, 0.7)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                zIndex: 9999
              }}
            >
              <motion.div
                initial={{ scale: 0.8 }}
                animate={{ scale: 1 }}
                className="card text-center"
                style={{ padding: '40px', maxWidth: '400px' }}
              >
                <div className="spinner" style={{ margin: '0 auto 20px' }}></div>
                <h3 style={{ marginBottom: '16px', color: '#333' }}>Processing Files</h3>
                <p style={{ color: '#6c757d', margin: 0 }}>
                  Analyzing and comparing your Excel files...
                </p>
              </motion.div>
            </motion.div>
          )}
        </AnimatePresence>

        {/* Footer */}
        <motion.footer
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 1 }}
          className="text-center"
          style={{ 
            marginTop: '60px', 
            padding: '20px', 
            color: '#fff',
            textShadow: '0 1px 2px rgba(0,0,0,0.3)'
          }}
        >
          <p style={{ fontSize: '14px', margin: 0 }}>
            Built with Spring Boot, React, and Framer Motion
          </p>
        </motion.footer>
      </div>
    </div>
  );
}

export default App;