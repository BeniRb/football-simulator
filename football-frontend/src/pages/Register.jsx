import React from 'react';
import { useNavigate } from 'react-router-dom';
import RegisterForm from '../components/RegisterForm';

export default function Register() {
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
            <h2 style={{ textAlign: 'center', marginBottom: '25px' }}>Create Account</h2>
            
            <RegisterForm onRegisterSuccess={() => navigate('/login')} />
        </div>
    );
}