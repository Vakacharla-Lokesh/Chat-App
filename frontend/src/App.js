import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Chat from './components/Chat';
import Login from './components/Login';
import Register from './components/Register';

function App() {
  const [username, setUsername] = useState('');
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check for saved auth token
    const token = localStorage.getItem('token');
    const savedUsername = localStorage.getItem('username');
    
    if (token && savedUsername) {
      setUsername(savedUsername);
      setIsAuthenticated(true);
    }
    
    setLoading(false);
  }, []);

  const handleLogin = (user, token) => {
    setUsername(user.username);
    setIsAuthenticated(true);
    localStorage.setItem('token', token);
    localStorage.setItem('username', user.username);
  };

  const handleLogout = () => {
    setUsername('');
    setIsAuthenticated(false);
    localStorage.removeItem('token');
    localStorage.removeItem('username');
  };

  if (loading) {
    return <div className="flex justify-center items-center h-screen">Loading...</div>;
  }

  return (
    <Router>
      <Routes>
        <Route 
          path="/" 
          element={
            isAuthenticated ? 
              <Navigate to="/chat" /> : 
              <Login onLogin={handleLogin} />
          } 
        />
        <Route 
          path="/register" 
          element={
            isAuthenticated ? 
              <Navigate to="/chat" /> : 
              <Register onRegister={handleLogin} />
          } 
        />
        <Route 
          path="/chat" 
          element={
            isAuthenticated ? 
              <Chat username={username} onLogout={handleLogout} /> : 
              <Navigate to="/" />
          } 
        />
      </Routes>
    </Router>
  );
}

export default App;