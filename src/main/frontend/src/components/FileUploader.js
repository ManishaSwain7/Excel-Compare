import React, { useCallback, useState } from 'react';
import { useDropzone } from 'react-dropzone';
import { motion } from 'framer-motion';

const FileUploader = ({ onFilesSelected, isLoading }) => {
  const [files, setFiles] = useState({ fileA: null, fileB: null });
  const [draggedOver, setDraggedOver] = useState(null);

  const onDropA = useCallback((acceptedFiles) => {
    if (acceptedFiles.length > 0) {
      setFiles(prev => ({ ...prev, fileA: acceptedFiles[0] }));
      setDraggedOver(null);
    }
  }, []);

  const onDropB = useCallback((acceptedFiles) => {
    if (acceptedFiles.length > 0) {
      setFiles(prev => ({ ...prev, fileB: acceptedFiles[0] }));
      setDraggedOver(null);
    }
  }, []);

  const dropzoneA = useDropzone({
    accept: {
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': ['.xlsx'],
      'application/vnd.ms-excel': ['.xls']
    },
    maxFiles: 1,
    onDrop: onDropA,
    onDragEnter: () => setDraggedOver('fileA'),
    onDragLeave: () => setDraggedOver(null),
  });

  const dropzoneB = useDropzone({
    accept: {
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': ['.xlsx'],
      'application/vnd.ms-excel': ['.xls']
    },
    maxFiles: 1,
    onDrop: onDropB,
    onDragEnter: () => setDraggedOver('fileB'),
    onDragLeave: () => setDraggedOver(null),
  });

  const handleCompare = () => {
    if (files.fileA && files.fileB) {
      onFilesSelected(files.fileA, files.fileB);
    }
  };

  const removeFile = (fileType) => {
    setFiles({ ...files, [fileType]: null });
  };

  const renderDropzone = (dropzone, fileType, title, description) => {
    const file = files[fileType];
    const isDragActive = dropzone.isDragActive || draggedOver === fileType;

    return (
      <motion.div
        {...dropzone.getRootProps()}
        className={`dropzone ${isDragActive ? 'drag-active' : ''} ${file ? 'has-file' : ''}`}
        whileHover={{ scale: 1.02 }}
        whileTap={{ scale: 0.98 }}
        animate={{
          borderColor: isDragActive ? '#667eea' : file ? '#28a745' : '#dee2e6',
          backgroundColor: isDragActive ? '#f8f9ff' : file ? '#f8fff8' : '#ffffff',
        }}
        style={{
          border: '2px dashed',
          borderRadius: '12px',
          padding: '40px 20px',
          textAlign: 'center',
          cursor: 'pointer',
          transition: 'all 0.3s ease',
          minHeight: '200px',
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center',
        }}
      >
        <input {...dropzone.getInputProps()} />
        
        {file ? (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="file-selected"
          >
            <div style={{ fontSize: '48px', marginBottom: '16px' }}>📊</div>
            <h4 style={{ color: '#28a745', marginBottom: '8px' }}>{title}</h4>
            <div style={{ 
              background: '#e9ecef', 
              padding: '8px 16px', 
              borderRadius: '6px',
              marginBottom: '16px',
              wordBreak: 'break-all'
            }}>
              {file.name}
            </div>
            <div style={{ fontSize: '14px', color: '#6c757d', marginBottom: '16px' }}>
              Size: {(file.size / 1024 / 1024).toFixed(2)} MB
            </div>
            <button
              type="button"
              onClick={(e) => {
                e.stopPropagation();
                removeFile(fileType);
              }}
              className="btn btn-secondary"
              style={{ fontSize: '14px', padding: '8px 16px' }}
            >
              Remove File
            </button>
          </motion.div>
        ) : (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
          >
            <div style={{ fontSize: '48px', marginBottom: '16px' }}>
              {isDragActive ? '📤' : '📋'}
            </div>
            <h4 style={{ marginBottom: '8px', color: isDragActive ? '#667eea' : '#333' }}>
              {title}
            </h4>
            <p style={{ color: '#6c757d', marginBottom: '16px' }}>
              {description}
            </p>
            <p style={{ fontSize: '14px', color: '#6c757d' }}>
              {isDragActive 
                ? 'Drop the Excel file here' 
                : 'Drag & drop an Excel file here, or click to select'
              }
            </p>
            <p style={{ fontSize: '12px', color: '#adb5bd', marginTop: '8px' }}>
              Supports .xlsx and .xls files
            </p>
          </motion.div>
        )}
      </motion.div>
    );
  };

  return (
    <div className="file-uploader">
      <h2 style={{ textAlign: 'center', marginBottom: '32px', color: '#333' }}>
        Upload Excel Files for Comparison
      </h2>
      
      <div className="grid grid-2" style={{ marginBottom: '32px' }}>
        {renderDropzone(
          dropzoneA, 
          'fileA', 
          'Pre-Migration File (File A)',
          'Upload the Excel file containing pre-migration document metadata'
        )}
        
        {renderDropzone(
          dropzoneB, 
          'fileB', 
          'Post-Migration File (File B)', 
          'Upload the Excel file containing post-migration document metadata'
        )}
      </div>

      <motion.div 
        className="text-center"
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.3 }}
      >
        <button
          onClick={handleCompare}
          disabled={!files.fileA || !files.fileB || isLoading}
          className={`btn ${files.fileA && files.fileB ? 'btn-primary' : 'btn-secondary'}`}
          style={{ 
            fontSize: '18px', 
            padding: '16px 32px',
            opacity: (!files.fileA || !files.fileB || isLoading) ? 0.6 : 1,
            cursor: (!files.fileA || !files.fileB || isLoading) ? 'not-allowed' : 'pointer'
          }}
        >
          {isLoading ? (
            <>
              <span className="spinner" style={{ marginRight: '12px', width: '16px', height: '16px' }}></span>
              Comparing Files...
            </>
          ) : (
            'Compare Files'
          )}
        </button>
        
        {(!files.fileA || !files.fileB) && (
          <p style={{ marginTop: '16px', color: '#6c757d', fontSize: '14px' }}>
            Please select both files to proceed with comparison
          </p>
        )}
      </motion.div>
    </div>
  );
};

export default FileUploader;