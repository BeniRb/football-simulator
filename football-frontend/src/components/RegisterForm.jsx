import React, { useState } from 'react';
import api from '../api/client';

export default function RegisterForm({ onRegisterSuccess }) {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const [loading, setLoading] = useState(false);

    const handleRegister = async (e) => {
        e.preventDefault();
        setErrorMessage('');
        setSuccessMessage('');

        if (password !== confirmPassword) {
            setErrorMessage("Passwords do not match.");
            return;
        }

        setLoading(true);

        try {
            // Changed body payload to null and added params configuration to match backend @RequestParam parameters
            const response = await api.post('/api/register', null, {
                params: {
                    username: username,
                    password: password,
                    email: email
                }
            });
            
            if (response.data && response.data.success) {
                setSuccessMessage('Account created successfully! Redirecting...');
                setTimeout(() => {
                    onRegisterSuccess();
                }, 2000);
            } else {
                setErrorMessage(response.data?.message || 'Registration failed.');
            }
        } catch (error) {
            setErrorMessage('Network error during registration. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <form onSubmit={handleRegister} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
            {errorMessage && (
                <div style={{ background: '#ff4d4d', color: '#fff', padding: '10px', borderRadius: '4px', fontSize: '0.9rem' }}>
                    {errorMessage}
                </div>
            )}
            {successMessage && (
                <div style={{ background: '#28a745', color: '#fff', padding: '10px', borderRadius: '4px', fontSize: '0.9rem' }}>
                    {successMessage}
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
                <label style={{ fontSize: '0.9rem', color: '#aaa' }}>Email Address</label>
                <input 
                    type="email" 
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
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

            <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                <label style={{ fontSize: '0.9rem', color: '#aaa' }}>Confirm Password</label>
                <input 
                    type="password" 
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    required
                    style={{ padding: '10px', borderRadius: '4px', border: '1px solid #333', background: '#2a2a2a', color: '#fff' }}
                />
            </div>

            <button 
                type="submit" 
                disabled={loading}
                style={{
                    padding: '12px',
                    background: loading ? '#555' : '#28a745',
                    color: '#fff',
                    border: 'none',
                    borderRadius: '4px',
                    cursor: loading ? 'not-allowed' : 'pointer',
                    fontWeight: 'bold',
                    marginTop: '5px'
                }}
            >
                {loading ? 'Creating Account...' : 'Register'}
            </button>
        </form>
    );
}