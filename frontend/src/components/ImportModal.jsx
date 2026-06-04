import React, { useState, useRef } from 'react';
import { X, Upload, FileSpreadsheet } from 'lucide-react';
import api from '../api/axios';

const ImportModal = ({ isOpen, onClose, onImportSuccess }) => {
    const [file, setFile] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const fileInputRef = useRef(null);

    if (!isOpen) return null;

    const handleFileChange = (e) => {
        const selectedFile = e.target.files[0];
        if (selectedFile) {
            const validTypes = ['text/csv', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'application/vnd.ms-excel'];
            if (validTypes.includes(selectedFile.type) || selectedFile.name.endsWith('.csv') || selectedFile.name.endsWith('.xlsx')) {
                setFile(selectedFile);
                setError('');
            } else {
                setError('Please select a valid CSV or Excel file.');
                setFile(null);
            }
        }
    };

    const handleImport = async () => {
        if (!file) {
            setError('Please select a file to import.');
            return;
        }

        setLoading(true);
        setError('');

        const formData = new FormData();
        formData.append('file', file);

        try {
            await api.post('/contacts/upload', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });
            onImportSuccess();
            handleClose();
        } catch (err) {
            setError(err.response?.data?.error || 'Failed to import contacts.');
        } finally {
            setLoading(false);
        }
    };

    const handleClose = () => {
        setFile(null);
        setError('');
        onClose();
    };

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <div className="modal-header">
                    <h2 className="modal-title">Import Contacts</h2>
                    <button className="close-btn" onClick={handleClose}><X size={20} /></button>
                </div>

                <div className="modal-body" style={{ textAlign: 'center' }}>
                    <div 
                        style={{ 
                            border: '2px dashed var(--border-color)', 
                            borderRadius: '1rem', 
                            padding: '3rem 2rem',
                            cursor: 'pointer',
                            backgroundColor: 'rgba(255,255,255,0.02)',
                            transition: 'var(--transition)'
                        }}
                        onClick={() => fileInputRef.current?.click()}
                    >
                        <FileSpreadsheet size={48} color="var(--accent-color)" style={{ marginBottom: '1rem' }} />
                        <h3 style={{ marginBottom: '0.5rem' }}>Click to upload</h3>
                        <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
                            CSV or Excel (.xlsx) files supported
                        </p>
                        
                        <input 
                            type="file" 
                            accept=".csv, .xlsx, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel, text/csv" 
                            style={{ display: 'none' }} 
                            ref={fileInputRef}
                            onChange={handleFileChange}
                        />
                    </div>

                    {file && (
                        <div style={{ marginTop: '1rem', padding: '1rem', backgroundColor: 'rgba(59, 130, 246, 0.1)', borderRadius: '0.5rem', color: 'var(--accent-color)', fontWeight: 500 }}>
                            Selected File: {file.name}
                        </div>
                    )}

                    {error && <div className="error-message" style={{ marginTop: '1rem' }}>{error}</div>}
                    
                    <div style={{ marginTop: '1.5rem', textAlign: 'left', backgroundColor: 'var(--bg-primary)', padding: '1rem', borderRadius: '0.5rem' }}>
                        <h4 style={{ marginBottom: '0.5rem', fontSize: '0.875rem' }}>Format Guide:</h4>
                        <p style={{ color: 'var(--text-secondary)', fontSize: '0.75rem', lineHeight: '1.5' }}>
                            Your file must include headers in the first row. Valid headers: <strong>firstName, lastName, email, phone, title</strong>.
                            You can separate multiple emails or phones using commas.
                        </p>
                    </div>
                </div>

                <div className="modal-footer">
                    <button className="btn-secondary" onClick={handleClose}>Cancel</button>
                    <button className="btn-primary" onClick={handleImport} disabled={loading || !file} style={{ width: 'auto' }}>
                        {loading ? 'Importing...' : <><Upload size={18} /> Import Data</>}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ImportModal;
