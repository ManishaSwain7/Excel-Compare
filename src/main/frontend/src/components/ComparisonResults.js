import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { excelComparison } from '../services/api';

const ComparisonResults = ({ comparisonResult, comparisonId }) => {
  const [activeTab, setActiveTab] = useState('mismatches');
  const [selectedField, setSelectedField] = useState('');
  const [filteredData, setFilteredData] = useState(null);
  const [isExporting, setIsExporting] = useState(false);

  if (!comparisonResult) {
    return null;
  }

  const { metadataMismatches, missingInA, missingInB, matchedDocuments } = comparisonResult;

  const handleFilter = async () => {
    if (selectedField) {
      try {
        const data = await excelComparison.getFilteredMismatches(comparisonId, selectedField);
        setFilteredData(data);
      } catch (error) {
        console.error('Error filtering data:', error);
      }
    } else {
      setFilteredData(null);
    }
  };

  const handleExport = async (exportType) => {
    setIsExporting(true);
    try {
      let blob;
      let filename;

      switch (exportType) {
        case 'csv':
          blob = await excelComparison.exportCsv(comparisonId);
          filename = 'comparison_report.csv';
          break;
        case 'detailed-csv':
          blob = await excelComparison.exportDetailedCsv(comparisonId, selectedField);
          filename = 'detailed_mismatches.csv';
          break;
        case 'html':
          const htmlContent = await excelComparison.exportHtml(comparisonId);
          blob = new Blob([htmlContent], { type: 'text/html' });
          filename = 'comparison_report.html';
          break;
        default:
          return;
      }

      // Create download link
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.style.display = 'none';
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch (error) {
      console.error('Error exporting data:', error);
    } finally {
      setIsExporting(false);
    }
  };

  const renderMismatches = () => {
    const mismatches = filteredData ? filteredData.mismatches : metadataMismatches;
    
    if (!mismatches || mismatches.length === 0) {
      return (
        <div className="text-center" style={{ padding: '40px', color: '#6c757d' }}>
          <div style={{ fontSize: '48px', marginBottom: '16px' }}>✅</div>
          <h4>No metadata mismatches found!</h4>
          <p>All matching documents have consistent metadata between files.</p>
        </div>
      );
    }

    return (
      <div className="table-responsive">
        <table className="table table-striped">
          <thead>
            <tr>
              <th>Policy No</th>
              <th>Field Name</th>
              <th>Pre-Migration Value</th>
              <th>Post-Migration Value</th>
              <th>Document Name</th>
            </tr>
          </thead>
          <tbody>
            {mismatches.map((mismatch, index) => (
              <motion.tr
                key={index}
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: index * 0.05 }}
              >
                <td><strong>{mismatch.policyNo}</strong></td>
                <td>
                  <span className="badge badge-warning">{mismatch.fieldName}</span>
                </td>
                <td style={{ maxWidth: '200px', wordBreak: 'break-word' }}>
                  {mismatch.valueA || '<empty>'}
                </td>
                <td style={{ maxWidth: '200px', wordBreak: 'break-word' }}>
                  {mismatch.valueB || '<empty>'}
                </td>
                <td style={{ maxWidth: '200px', wordBreak: 'break-word' }}>
                  {mismatch.documentA?.documentName || 'N/A'}
                </td>
              </motion.tr>
            ))}
          </tbody>
        </table>
      </div>
    );
  };

  const renderMissingDocuments = (documents, title, emptyMessage) => {
    if (!documents || documents.length === 0) {
      return (
        <div className="text-center" style={{ padding: '40px', color: '#6c757d' }}>
          <div style={{ fontSize: '48px', marginBottom: '16px' }}>✅</div>
          <h4>{emptyMessage}</h4>
        </div>
      );
    }

    return (
      <div>
        <h4 style={{ marginBottom: '20px', color: '#333' }}>{title} ({documents.length})</h4>
        <div className="table-responsive">
          <table className="table table-striped">
            <thead>
              <tr>
                <th>Policy No</th>
                <th>Document Name</th>
                <th>Claim No</th>
                <th>Document Type</th>
                <th>Status</th>
                <th>Migration Status</th>
              </tr>
            </thead>
            <tbody>
              {documents.map((doc, index) => (
                <motion.tr
                  key={index}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: index * 0.05 }}
                >
                  <td><strong>{doc.policyNo}</strong></td>
                  <td style={{ maxWidth: '200px', wordBreak: 'break-word' }}>
                    {doc.documentName || 'N/A'}
                  </td>
                  <td>{doc.claimNo || 'N/A'}</td>
                  <td>{doc.documentType || 'N/A'}</td>
                  <td>{doc.status || 'N/A'}</td>
                  <td>
                    <span className={`badge ${doc.migration === 'Y' ? 'badge-success' : 'badge-danger'}`}>
                      {doc.migration === 'Y' ? 'Migrated' : 'Not Migrated'}
                    </span>
                  </td>
                </motion.tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    );
  };

  const uniqueFields = [...new Set(metadataMismatches.map(m => m.fieldName))];

  return (
    <motion.div 
      className="comparison-results"
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
    >
      <div className="card">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
          <h2 style={{ color: '#333', margin: 0 }}>Detailed Results</h2>
          
          <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
            <select
              value={selectedField}
              onChange={(e) => setSelectedField(e.target.value)}
              className="form-control"
              style={{ width: '200px' }}
            >
              <option value="">All Fields</option>
              {uniqueFields.map(field => (
                <option key={field} value={field}>{field}</option>
              ))}
            </select>
            
            <button onClick={handleFilter} className="btn btn-secondary">
              Filter
            </button>
          </div>
        </div>

        {/* Export Buttons */}
        <div style={{ display: 'flex', gap: '12px', marginBottom: '24px', flexWrap: 'wrap' }}>
          <button 
            onClick={() => handleExport('csv')} 
            disabled={isExporting}
            className="btn btn-success"
          >
            {isExporting ? 'Exporting...' : 'Export Summary CSV'}
          </button>
          <button 
            onClick={() => handleExport('detailed-csv')} 
            disabled={isExporting}
            className="btn btn-success"
          >
            Export Detailed CSV
          </button>
          <button 
            onClick={() => handleExport('html')} 
            disabled={isExporting}
            className="btn btn-success"
          >
            Export HTML Report
          </button>
        </div>

        {/* Tabs */}
        <div style={{ borderBottom: '2px solid #e9ecef', marginBottom: '24px' }}>
          <div style={{ display: 'flex', gap: '24px' }}>
            {[
              { key: 'mismatches', label: `Metadata Mismatches (${metadataMismatches.length})` },
              { key: 'missing-b', label: `Missing in Post-Migration (${missingInB.length})` },
              { key: 'missing-a', label: `Missing in Pre-Migration (${missingInA.length})` },
              { key: 'matched', label: `Perfect Matches (${matchedDocuments.length})` }
            ].map(tab => (
              <button
                key={tab.key}
                onClick={() => setActiveTab(tab.key)}
                className={`btn ${activeTab === tab.key ? 'btn-primary' : 'btn-secondary'}`}
                style={{ 
                  borderRadius: '8px 8px 0 0',
                  borderBottom: activeTab === tab.key ? '2px solid #667eea' : 'none'
                }}
              >
                {tab.label}
              </button>
            ))}
          </div>
        </div>

        {/* Tab Content */}
        <div>
          {activeTab === 'mismatches' && renderMismatches()}
          
          {activeTab === 'missing-b' && renderMissingDocuments(
            missingInB,
            'Documents Missing in Post-Migration File',
            'All documents from pre-migration file were found in post-migration file!'
          )}
          
          {activeTab === 'missing-a' && renderMissingDocuments(
            missingInA,
            'Documents Missing in Pre-Migration File',
            'No additional documents found in post-migration file!'
          )}
          
          {activeTab === 'matched' && renderMissingDocuments(
            matchedDocuments,
            'Documents with Perfect Metadata Match',
            'No documents have perfect metadata matches!'
          )}
        </div>
      </div>
    </motion.div>
  );
};

export default ComparisonResults;