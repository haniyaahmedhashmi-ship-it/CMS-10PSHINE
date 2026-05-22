import React, { useContext } from 'react';
import { AuthContext } from '../../context/AuthContext';
import { useNavigate, Link, useLocation } from 'react-router-dom';
import ThemeToggle from '../ThemeToggle';
import { User as UserIcon, LogOut, ChevronLeft } from 'lucide-react';

const Navbar = () => {
    const { user, logout } = useContext(AuthContext);
    const navigate = useNavigate();
    const location = useLocation();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const isProfilePage = location.pathname === '/profile';

    return (
        <nav className="navbar">
            <div className="nav-brand">
                <UserIcon size={24} color="var(--accent-color)" /> <span className="hide-on-mobile">Contact Manager</span>
            </div>
            <div className="nav-links">
                <ThemeToggle />
                {user && (
                    <>
                        <span className="hide-on-mobile" style={{ color: 'var(--text-secondary)' }}>Hello, {user.firstName}</span>
                        {isProfilePage ? (
                            <Link to="/" className="nav-link" title="Back to Dashboard">
                                <ChevronLeft size={18} /> <span className="hide-on-mobile">Dashboard</span>
                            </Link>
                        ) : (
                            <Link to="/profile" className="nav-link" title="Profile">
                                <UserIcon size={18} /> <span className="hide-on-mobile">Profile</span>
                            </Link>
                        )}
                        <button onClick={handleLogout} className="action-btn" title="Logout" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                            <LogOut size={18} /> <span className="hide-on-mobile">Logout</span>
                        </button>
                    </>
                )}
            </div>
        </nav>
    );
};

export default React.memo(Navbar);
