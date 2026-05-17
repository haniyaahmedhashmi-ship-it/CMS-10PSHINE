import React, { useState, useContext, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { LogIn } from 'lucide-react';
import gsap from 'gsap';
import ThemeToggle from '../components/ThemeToggle';
import SEO from '../components/ui/SEO';
import Button from '../components/ui/Button';
import Input from '../components/ui/Input';

const LoginPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    
    const { login } = useContext(AuthContext);
    const navigate = useNavigate();

    useEffect(() => {
        gsap.fromTo(
            '.auth-card',
            { y: 50, opacity: 0 },
            { y: 0, opacity: 1, duration: 0.8, ease: 'power3.out' }
        );
        gsap.fromTo(
            '.form-group',
            { x: -20, opacity: 0 },
            { x: 0, opacity: 1, duration: 0.5, stagger: 0.1, delay: 0.3, ease: 'power2.out' }
        );
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            await login(email, password);
            navigate('/');
        } catch (err) {
            setError(err.response?.data?.error || 'Invalid email or password');
            setLoading(false);
        }
    };

    return (
        <div className="auth-layout">
            <SEO title="Login" />
            <div style={{ position: 'absolute', top: '2rem', right: '2rem' }}>
                <ThemeToggle />
            </div>
            <div className="auth-card">
                <div className="auth-header">
                    <h2 className="auth-title">Welcome Back</h2>
                    <p className="auth-subtitle">Sign in to manage your contacts</p>
                </div>

                {error && <div className="error-message" style={{ marginBottom: '1rem' }}>{error}</div>}

                <form onSubmit={handleSubmit}>
                    <Input 
                        label="Email Address"
                        type="email" 
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                        autoFocus
                    />
                    <Input 
                        label="Password"
                        type="password" 
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                    
                    <Button type="submit" loading={loading} style={{ marginTop: '1rem', width: '100%' }}>
                        <LogIn size={18} /> Sign In
                    </Button>
                </form>

                <div className="auth-footer">
                    Don't have an account? 
                    <Link to="/register" className="auth-link">Register here</Link>
                </div>
            </div>
        </div>
    );
};

export default LoginPage;
