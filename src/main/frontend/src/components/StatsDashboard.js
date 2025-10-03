import React from 'react';
import { motion } from 'framer-motion';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, PieChart, Pie, Cell, ResponsiveContainer } from 'recharts';

const StatsDashboard = ({ comparisonResult }) => {
  if (!comparisonResult || !comparisonResult.statistics) {
    return null;
  }

  const { statistics } = comparisonResult;

  // Prepare data for charts
  const migrationData = [
    {
      name: 'Successful Migrations',
      value: statistics.successfulMigrations,
      color: '#28a745'
    },
    {
      name: 'Failed Migrations', 
      value: statistics.failedMigrations,
      color: '#dc3545'
    }
  ];

  const mismatchData = Object.entries(statistics.mismatchTypesCounts || {}).map(([field, count]) => ({
    field: field.charAt(0).toUpperCase() + field.slice(1),
    count
  }));

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1
      }
    }
  };

  const itemVariants = {
    hidden: { y: 20, opacity: 0 },
    visible: { y: 0, opacity: 1 }
  };

  return (
    <motion.div 
      className="stats-dashboard"
      variants={containerVariants}
      initial="hidden"
      animate="visible"
    >
      <motion.h2 variants={itemVariants} style={{ textAlign: 'center', marginBottom: '32px', color: '#333' }}>
        Comparison Results Dashboard
      </motion.h2>

      {/* Summary Cards */}
      <motion.div className="grid grid-3" variants={itemVariants} style={{ marginBottom: '32px' }}>
        <div className="card text-center">
          <h3 style={{ color: '#667eea', marginBottom: '16px' }}>Total Documents</h3>
          <div style={{ display: 'flex', justifyContent: 'space-around' }}>
            <div>
              <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#333' }}>
                {statistics.totalDocumentsA}
              </div>
              <div style={{ fontSize: '14px', color: '#6c757d' }}>Pre-Migration</div>
            </div>
            <div>
              <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#333' }}>
                {statistics.totalDocumentsB}
              </div>
              <div style={{ fontSize: '14px', color: '#6c757d' }}>Post-Migration</div>
            </div>
          </div>
        </div>

        <div className="card text-center">
          <h3 style={{ color: '#28a745', marginBottom: '16px' }}>Migration Status</h3>
          <div style={{ display: 'flex', justifyContent: 'space-around' }}>
            <div>
              <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#28a745' }}>
                {statistics.successfulMigrations}
              </div>
              <div style={{ fontSize: '14px', color: '#6c757d' }}>Successful</div>
            </div>
            <div>
              <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#dc3545' }}>
                {statistics.failedMigrations}
              </div>
              <div style={{ fontSize: '14px', color: '#6c757d' }}>Failed</div>
            </div>
          </div>
        </div>

        <div className="card text-center">
          <h3 style={{ color: '#ffc107', marginBottom: '16px' }}>Issues Found</h3>
          <div style={{ display: 'flex', justifyContent: 'space-around' }}>
            <div>
              <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#ffc107' }}>
                {statistics.metadataMismatches}
              </div>
              <div style={{ fontSize: '14px', color: '#6c757d' }}>Mismatches</div>
            </div>
            <div>
              <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#dc3545' }}>
                {statistics.unmatchedDocuments}
              </div>
              <div style={{ fontSize: '14px', color: '#6c757d' }}>Unmatched</div>
            </div>
          </div>
        </div>
      </motion.div>

      {/* Charts */}
      <div className="grid grid-2">
        {/* Migration Status Pie Chart */}
        <motion.div className="card" variants={itemVariants}>
          <h3 style={{ marginBottom: '20px', color: '#333' }}>Migration Success Rate</h3>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={migrationData}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={({ name, value, percent }) => `${name}: ${value} (${(percent * 100).toFixed(1)}%)`}
                outerRadius={80}
                fill="#8884d8"
                dataKey="value"
              >
                {migrationData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </motion.div>

        {/* Mismatch Types Bar Chart */}
        <motion.div className="card" variants={itemVariants}>
          <h3 style={{ marginBottom: '20px', color: '#333' }}>Mismatch Types</h3>
          {mismatchData.length > 0 ? (
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={mismatchData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis 
                  dataKey="field" 
                  angle={-45}
                  textAnchor="end"
                  height={80}
                  interval={0}
                />
                <YAxis />
                <Tooltip />
                <Bar dataKey="count" fill="#667eea" />
              </BarChart>
            </ResponsiveContainer>
          ) : (
            <div style={{ 
              height: '300px', 
              display: 'flex', 
              alignItems: 'center', 
              justifyContent: 'center',
              color: '#6c757d'
            }}>
              No metadata mismatches found
            </div>
          )}
        </motion.div>
      </div>

      {/* Success Rate Summary */}
      <motion.div className="card" variants={itemVariants} style={{ marginTop: '20px' }}>
        <h3 style={{ marginBottom: '16px', color: '#333' }}>Migration Summary</h3>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <strong>Overall Success Rate: </strong>
            <span style={{ 
              color: statistics.successfulMigrations > statistics.failedMigrations ? '#28a745' : '#dc3545',
              fontSize: '18px',
              fontWeight: 'bold'
            }}>
              {statistics.totalDocumentsB > 0 
                ? ((statistics.successfulMigrations / statistics.totalDocumentsB) * 100).toFixed(1)
                : 0
              }%
            </span>
          </div>
          <div>
            <strong>Data Quality: </strong>
            <span style={{ 
              color: statistics.metadataMismatches === 0 ? '#28a745' : '#ffc107',
              fontSize: '18px',
              fontWeight: 'bold'
            }}>
              {statistics.metadataMismatches === 0 ? 'Excellent' : 
               statistics.metadataMismatches < 10 ? 'Good' : 'Needs Attention'}
            </span>
          </div>
        </div>
      </motion.div>
    </motion.div>
  );
};

export default StatsDashboard;