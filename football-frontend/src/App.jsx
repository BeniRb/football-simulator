import React, { useEffect, useState } from 'react';
import { BrowserRouter, Routes, Route, NavLink } from 'react-router-dom';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Standings from './pages/Standings';
import Dashboard from './pages/Dashboard';
import Profile from './pages/Profile';
import ProtectedRoute from './components/ProtectedRoute';
import { translations } from './utils/Translations';
import './App.css';

function App() {
  const [lang, setLang] = useState(() => {
    return localStorage.getItem('lang') || 'EN';
  });

  const t = translations[lang];
  const isAuthenticated = !!localStorage.getItem('username');

  useEffect(() => {
    const savedTheme = localStorage.getItem('theme') || 'DARK';
    document.body.setAttribute('data-theme', savedTheme);
  }, []);

  useEffect(() => {
    localStorage.setItem('lang', lang);
  }, [lang]);

  const handleLogout = () => {
    localStorage.removeItem('username');
    window.location.href = '/';
  };

  const getNavClass = ({ isActive }) =>
    isActive ? 'nav-link active-nav-link' : 'nav-link';

  const getHomeNavClass = ({ isActive }) =>
    isActive ? 'nav-link-bold active-nav-link' : 'nav-link-bold';

  return (
    <BrowserRouter>
      <div dir={lang === 'HE' ? 'rtl' : 'ltr'} className="app-container">
        <nav className="nav-bar">
          <NavLink to="/" className={getHomeNavClass}>
            {t.home}
          </NavLink>

          {isAuthenticated && (
            <>
              <NavLink to="/standings" className={getNavClass}>
                {t.standings}
              </NavLink>

              <NavLink to="/dashboard" className={getNavClass}>
                {t.liveDashboard}
              </NavLink>
            </>
          )}

          <div className="auth-container">
            {isAuthenticated ? (
              <>
                <NavLink to="/profile" className="profile-icon" title={t.accountInfo}>
                  👤
                </NavLink>

                <button onClick={handleLogout} className="logout-btn">
                  {t.logout}
                </button>
              </>
            ) : (
              <NavLink to="/login" className="login-btn">
                Login
              </NavLink>
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