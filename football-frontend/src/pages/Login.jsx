import React from 'react';
import { useNavigate } from 'react-router-dom';
import LoginForm from '../components/loginForm';

export default function Login() {
    const navigate = useNavigate();

    return (
        <div style={{
            maxWidth: '400px',
            margin: '60px auto',
            padding: '30px',
            background: '#1a1a1a',
            borderRadius: '8px',
            boxShadow: '0 4px 12px rgba(0,0,0,0.5)',
            color: '#fff'
        }}>
            <h2 style={{ textAlign: 'center', marginBottom: '25px' }}>Account Login</h2>
            
            <LoginForm onLoginSuccess={() => window.location.href = '/dashboard'} />
        </div>
    );
}