import React from 'react';

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { 
      hasError: false, 
      error: null, 
      errorInfo: null 
    };
  }

  static getDerivedStateFromError(error) {
    // Update state so the next render will show the fallback UI
    return { hasError: true };
  }

  componentDidCatch(error, errorInfo) {
    // Log the error to console for debugging
    console.error('ErrorBoundary caught an error:', error, errorInfo);
    
    this.setState({
      error: error,
      errorInfo: errorInfo
    });
  }

  render() {
    if (this.state.hasError) {
      // Fallback UI when there's an error
      return (
        <div className="App">
          <div className="container">
            <div className="card" style={{ 
              textAlign: 'center', 
              padding: '40px',
              marginTop: '40px',
              background: 'rgba(255, 255, 255, 0.95)',
              backdropFilter: 'blur(10px)'
            }}>
              <div style={{ fontSize: '48px', marginBottom: '20px' }}>⚠️</div>
              <h2 style={{ color: '#dc3545', marginBottom: '16px' }}>
                Something went wrong
              </h2>
              <p style={{ color: '#6c757d', marginBottom: '24px' }}>
                The application encountered an unexpected error. Please try refreshing the page.
              </p>
              
              <div style={{ display: 'flex', gap: '12px', justifyContent: 'center' }}>
                <button 
                  onClick={() => window.location.reload()}
                  className="btn btn-primary"
                >
                  Refresh Page
                </button>
                <button 
                  onClick={() => this.setState({ hasError: false, error: null, errorInfo: null })}
                  className="btn btn-secondary"
                >
                  Try Again
                </button>
              </div>

              {/* Show error details in development */}
              {process.env.NODE_ENV === 'development' && this.state.error && (
                <details style={{ 
                  marginTop: '24px', 
                  textAlign: 'left',
                  background: '#f8f9fa',
                  padding: '16px',
                  borderRadius: '8px',
                  border: '1px solid #dee2e6'
                }}>
                  <summary style={{ 
                    cursor: 'pointer', 
                    fontWeight: 'bold',
                    marginBottom: '8px',
                    color: '#495057'
                  }}>
                    Error Details (Development Mode)
                  </summary>
                  <pre style={{ 
                    fontSize: '12px', 
                    color: '#dc3545',
                    whiteSpace: 'pre-wrap',
                    wordBreak: 'break-word'
                  }}>
                    {this.state.error && this.state.error.toString()}
                    <br />
                    {this.state.errorInfo.componentStack}
                  </pre>
                </details>
              )}
            </div>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;