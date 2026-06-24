import React, { useState } from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Standings from './pages/Standings';
import Dashboard from './pages/Dashboard';
import Profile from './pages/Profile';
import ProtectedRoute from './components/ProtectedRoute';
import { translations } from './utils/Translations';
import './App.css'; // Import your newly separated stylesheet

function App() {
  const [lang, setLang] = useState('EN');
  const t = translations[lang];
  const isAuthenticated = !!localStorage.getItem('username');

  const handleLogout = () => {
    localStorage.removeItem('username');
    window.location.href = '/';
  };

  return (
    <BrowserRouter>
      <div dir={lang === 'HE' ? 'rtl' : 'ltr'} className="app-container">
        
        <nav className="nav-bar">
          <Link to="/" className="nav-link-bold">{t.home}</Link>
          
          {isAuthenticated && (
            <>
              <Link to="/standings" className="nav-link">{t.standings}</Link>
              <Link to="/dashboard" className="nav-link">{t.liveDashboard}</Link>
            </>
          )}
          
          <div className="auth-container">
            {isAuthenticated ? (
              <>
                <Link to="/profile" className="profile-icon" title={t.accountInfo}>
                  👤
                </Link>
                <button onClick={handleLogout} className="logout-btn">
                  {t.logout}
                </button>
              </>
            ) : (
              <Link to="/login" className="login-btn">
                Login
              </Link>
            )}
          </div>
        </nav>

        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          
          <Route 
            path="/standings" 
            element={
              <ProtectedRoute>
                <Standings globalLang={lang} />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/dashboard" 
            element={
              <ProtectedRoute>
                <Dashboard globalLang={lang} />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/profile" 
            element={
              <ProtectedRoute>
                <Profile globalLang={lang} setGlobalLang={setLang} />
              </ProtectedRoute>
            } 
          />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;