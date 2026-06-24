import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/client';

export default function LoginForm({ onLoginSuccess }) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setErrorMessage('');
        setLoading(true);

        try {
            const response = await api.post('/api/login', { personalId: username, password: password });
            
            if (response.data && response.data.success) {
                localStorage.setItem('username', username);
                onLoginSuccess();
            } else {
                setErrorMessage(response.data?.message || 'Invalid username or password.');
            }
        } catch (error) {
            setErrorMessage('Network connection error. Please try again later.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
            {errorMessage && (
                <div style={{ background: '#ff4d4d', color: '#fff', padding: '10px', borderRadius: '4px', fontSize: '0.9rem' }}>
                    {errorMessage}
                </div>
            )}

            <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                <label style={{ fontSize: '0.9rem', color: '#aaa' }}>Username</label>
                <input 
                    type="text" 
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                    style={{ padding: '10px', borderRadius: '4px', border: '1px solid #333', background: '#2a2a2a', color: '#fff' }}
                />
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                <label style={{ fontSize: '0.9rem', color: '#aaa' }}>Password</label>
                <input 
                    type="password" 
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                    style={{ padding: '10px', borderRadius: '4px', border: '1px solid #333', background: '#2a2a2a', color: '#fff' }}
                />
            </div>

            {/* Navigation text trigger below the fields */}
            <div style={{ fontSize: '0.85rem', paddingLeft: '2px' }}>
                <span style={{ color: '#aaa' }}>New user? </span>
                <span 
                    onClick={() => navigate('/register')} 
                    style={{ color: '#28a745', cursor: 'pointer', textDecoration: 'underline' }}
                >
                    Register here
                </span>
            </div>  

            <button 
                type="submit" 
                disabled={loading}
                style={{
                    padding: '12px',
                    background: loading ? '#555' : '#007bff',
                    color: '#fff',
                    border: 'none',
                    borderRadius: '4px',
                    cursor: loading ? 'not-allowed' : 'pointer',
                    fontWeight: 'bold',
                    marginTop: '5px'
                }}
            >
                {loading ? 'Authenticating...' : 'Sign In'}
            </button>
        </form>
    );
}