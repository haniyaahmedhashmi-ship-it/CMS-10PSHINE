import React, { useState, useEffect } from 'react';
import { X, Plus, Trash2 } from 'lucide-react';

const ContactModal = ({ isOpen, onClose, onSave, initialData }) => {
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        title: '',
        emailAddresses: [''],
        phoneNumbers: ['']
    });

    useEffect(() => {
        if (initialData) {
            setFormData({
                ...initialData,
                emailAddresses: initialData.emailAddresses?.length ? initialData.emailAddresses : [''],
                phoneNumbers: initialData.phoneNumbers?.length ? initialData.phoneNumbers : ['']
            });
        } else {
            setFormData({
                firstName: '',
                lastName: '',
                title: '',
                emailAddresses: [''],
                phoneNumbers: ['']
            });
        }
    }, [initialData, isOpen]);

    if (!isOpen) return null;

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleArrayChange = (index, field, value) => {
        const newArray = [...formData[field]];
        newArray[index] = value;
        setFormData({ ...formData, [field]: newArray });
    };

    const addArrayItem = (field) => {
        setFormData({ ...formData, [field]: [...formData[field], ''] });
    };

    const removeArrayItem = (index, field) => {
        const newArray = formData[field].filter((_, i) => i !== index);
        setFormData({ ...formData, [field]: newArray.length ? newArray : [''] });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        // Clean empty array items
        const cleanedData = {
            ...formData,
            emailAddresses: formData.emailAddresses.filter(e => e.trim() !== ''),
            phoneNumbers: formData.phoneNumbers.filter(p => p.trim() !== '')
        };
        onSave(cleanedData);
    };

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <div className="modal-header">
                    <h2 className="modal-title">{initialData ? 'Edit Contact' : 'Create Contact'}</h2>
                    <button className="close-btn" onClick={onClose}><X size={24} /></button>
                </div>
                <form onSubmit={handleSubmit}>
                    <div className="modal-body">
                        <div style={{ display: 'flex', gap: '1rem', marginBottom: '1rem' }}>
                            <div className="form-group" style={{ flex: 1, marginBottom: 0 }}>
                                <label className="form-label">First Name</label>
                                <input type="text" name="firstName" className="form-input" value={formData.firstName} onChange={handleChange} required />
                            </div>
                            <div className="form-group" style={{ flex: 1, marginBottom: 0 }}>
                                <label className="form-label">Last Name</label>
                                <input type="text" name="lastName" className="form-input" value={formData.lastName} onChange={handleChange} required />
                            </div>
                        </div>

                        <div className="form-group">
                            <label className="form-label">Title / Company</label>
                            <input type="text" name="title" className="form-input" value={formData.title} onChange={handleChange} />
                        </div>

                        <div className="form-group">
                            <label className="form-label">Email Addresses</label>
                            {formData.emailAddresses.map((email, index) => (
                                <div key={index} className="array-input-group">
                                    <input type="email" className="form-input" value={email} onChange={(e) => handleArrayChange(index, 'emailAddresses', e.target.value)} placeholder="Email address" />
                                    <button type="button" className="btn-danger" onClick={() => removeArrayItem(index, 'emailAddresses')}><Trash2 size={16} /></button>
                                </div>
                            ))}
                            <button type="button" className="btn-secondary" style={{ marginTop: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }} onClick={() => addArrayItem('emailAddresses')}>
                                <Plus size={16} /> Add Email
                            </button>
                        </div>

                        <div className="form-group">
                            <label className="form-label">Phone Numbers</label>
                            {formData.phoneNumbers.map((phone, index) => (
                                <div key={index} className="array-input-group">
                                    <input type="text" className="form-input" value={phone} onChange={(e) => handleArrayChange(index, 'phoneNumbers', e.target.value)} placeholder="Phone number" />
                                    <button type="button" className="btn-danger" onClick={() => removeArrayItem(index, 'phoneNumbers')}><Trash2 size={16} /></button>
                                </div>
                            ))}
                            <button type="button" className="btn-secondary" style={{ marginTop: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }} onClick={() => addArrayItem('phoneNumbers')}>
                                <Plus size={16} /> Add Phone
                            </button>
                        </div>
                    </div>
                    <div className="modal-footer">
                        <button type="button" className="btn-secondary" onClick={onClose}>Cancel</button>
                        <button type="submit" className="btn-primary" style={{ width: 'auto' }}>Save Contact</button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default ContactModal;
