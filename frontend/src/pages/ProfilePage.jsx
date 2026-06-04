import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import Navbar from '../components/ui/Navbar';
import SEO from '../components/ui/SEO';
import Button from '../components/ui/Button';
import Input from '../components/ui/Input';
import gsap from 'gsap';
import { User as UserIcon, Lock, Check, Mail, Phone, Shield } from 'lucide-react';

const ProfilePage = () => {
    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);
    
    const [passwordData, setPasswordData] = useState({
        currentPassword: '',
        newPassword: ''
    });
    const [passwordMsg, setPasswordMsg] = useState({ type: '', text: '' });
    const [savingPassword, setSavingPassword] = useState(false);

    useEffect(() => {
        const fetchProfile = async () => {
            try {
                const res = await api.get('/auth/profile');
                setProfile(res.data);
            } catch (error) {
                console.error("Error fetching profile", error);
            } finally {
                setLoading(false);
            }
        };
        fetchProfile();
    }, []);

    useEffect(() => {
        if (!loading && profile) {
            gsap.fromTo(
                '.profile-anim',
                { y: 30, opacity: 0 },
                { y: 0, opacity: 1, duration: 0.6, stagger: 0.1, ease: 'power3.out' }
            );
        }
    }, [loading, profile]);

    const handlePasswordChange = async (e) => {
        e.preventDefault();
        setSavingPassword(true);
        setPasswordMsg({ type: '', text: '' });

        try {
            await api.post('/auth/change-password', passwordData);
            setPasswordMsg({ type: 'success', text: 'Password changed successfully!' });
            setPasswordData({ currentPassword: '', newPassword: '' });
        } catch (error) {
            setPasswordMsg({ type: 'error', text: error.response?.data?.error || 'Failed to change password' });
        } finally {
            setSavingPassword(false);
        }
    };

    if (loading) return <div className="loader-container"><div className="spinner"></div></div>;
    if (!profile) return null;

    return (
        <>
            <SEO title="Profile" />
            <Navbar />

            <main className="container">
                <section className="profile-card profile-anim" style={{ opacity: 0 }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem', marginBottom: '2rem' }}>
                        <div style={{
                            width: '80px', height: '80px', borderRadius: '50%',
                            background: 'var(--accent-color)', display: 'flex',
                            alignItems: 'center', justifyContent: 'center',
                            boxShadow: '0 0 20px rgba(59, 130, 246, 0.5)'
                        }}>
                            <UserIcon size={40} color="white" />
                        </div>
                        <div>
                            <h2 style={{ fontSize: '1.5rem', marginBottom: '0.25rem' }}>{profile.firstName} {profile.lastName}</h2>
                            <span style={{ color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                                <Shield size={14} /> ID: {profile.id}
                            </span>
                        </div>
                    </div>

                    <div className="profile-info">
                        <div className="info-group">
                            <div className="info-label" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}><UserIcon size={16} /> First Name</div>
                            <div className="info-value">{profile.firstName}</div>
                        </div>
                        <div className="info-group">
                            <div className="info-label" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}><UserIcon size={16} /> Last Name</div>
                            <div className="info-value">{profile.lastName || '-'}</div>
                        </div>
                        <div className="info-group">
                            <div className="info-label" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}><Mail size={16} /> Email Address</div>
                            <div className="info-value">{profile.email}</div>
                        </div>
                        <div className="info-group" style={{ borderBottom: 'none' }}>
                            <div className="info-label" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}><Phone size={16} /> Phone Number</div>
                            <div className="info-value">{profile.phoneNumber || '-'}</div>
                        </div>
                    </div>

                    <h3 className="profile-anim" style={{ opacity: 0, marginTop: '3rem', marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '0.5rem' }}>
                        <Lock size={20} /> Change Password
                    </h3>

                    {passwordMsg.text && (
                        <div className="error-message" style={{ 
                            backgroundColor: passwordMsg.type === 'success' ? 'rgba(16, 185, 129, 0.1)' : 'rgba(239, 68, 68, 0.1)',
                            color: passwordMsg.type === 'success' ? 'var(--success-color)' : 'var(--danger-color)',
                            marginBottom: '1rem'
                        }}>
                            {passwordMsg.text}
                        </div>
                    )}

                    <form onSubmit={handlePasswordChange} className="profile-anim" style={{ opacity: 0 }}>
                        <Input 
                            label="Current Password"
                            type="password" 
                            value={passwordData.currentPassword}
                            onChange={(e) => setPasswordData({...passwordData, currentPassword: e.target.value})}
                            required
                        />
                        <Input 
                            label="New Password"
                            type="password" 
                            value={passwordData.newPassword}
                            onChange={(e) => setPasswordData({...passwordData, newPassword: e.target.value})}
                            required
                            minLength={6}
                        />
                        <Button type="submit" loading={savingPassword} style={{ marginTop: '1rem' }}>
                            <Check size={18} /> Update Password
                        </Button>
                    </form>
                </section>
            </main>
        </>
    );
};

export default ProfilePage;
