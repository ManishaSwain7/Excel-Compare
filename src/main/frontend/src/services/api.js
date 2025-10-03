import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000, // 30 second timeout
});

// Add request interceptor for logging
api.interceptors.request.use(
  (config) => {
    console.log(`API Request: ${config.method?.toUpperCase()} ${config.url}`);
    return config;
  },
  (error) => {
    console.error('API Request Error:', error);
    return Promise.reject(error);
  }
);

// Add response interceptor for error handling
api.interceptors.response.use(
  (response) => {
    console.log(`API Response: ${response.status} ${response.config.url}`);
    return response;
  },
  (error) => {
    console.error('API Response Error:', error);
    
    // Handle network errors
    if (!error.response) {
      error.code = 'NETWORK_ERROR';
      error.message = 'Network Error: Unable to connect to the server';
    }
    
    return Promise.reject(error);
  }
);

export const excelComparison = {
  // Upload and compare files
  compareFiles: async (fileA, fileB) => {
    const formData = new FormData();
    formData.append('fileA', fileA);
    formData.append('fileB', fileB);
    
    const response = await api.post('/compare/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  // Get comparison result
  getComparisonResult: async (comparisonId) => {
    const response = await api.get(`/compare/result/${comparisonId}`);
    return response.data;
  },

  // Get filtered mismatches
  getFilteredMismatches: async (comparisonId, fieldName = null, migrationStatus = null) => {
    const params = new URLSearchParams();
    if (fieldName) params.append('fieldName', fieldName);
    if (migrationStatus) params.append('migrationStatus', migrationStatus);
    
    const response = await api.get(`/compare/result/${comparisonId}/filter?${params}`);
    return response.data;
  },

  // Export CSV
  exportCsv: async (comparisonId) => {
    const response = await api.get(`/compare/export/csv/${comparisonId}`, {
      responseType: 'blob',
    });
    return response.data;
  },

  // Export detailed CSV
  exportDetailedCsv: async (comparisonId, fieldName = null) => {
    const params = fieldName ? `?fieldName=${fieldName}` : '';
    const response = await api.get(`/compare/export/csv/detailed/${comparisonId}${params}`, {
      responseType: 'blob',
    });
    return response.data;
  },

  // Export HTML
  exportHtml: async (comparisonId) => {
    const response = await api.get(`/compare/export/html/${comparisonId}`);
    return response.data;
  },

  // Health check
  healthCheck: async () => {
    const response = await api.get('/compare/health');
    return response.data;
  },
};

export default api;