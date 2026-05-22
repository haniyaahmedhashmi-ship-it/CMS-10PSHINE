import React, { useState, useEffect } from 'react';
import ContactModal from '../components/ContactModal';
import DeleteConfirmModal from '../components/DeleteConfirmModal';
import ImportModal from '../components/ImportModal';
import SEO from '../components/ui/SEO';
import Navbar from '../components/ui/Navbar';
import Button from '../components/ui/Button';
import Input from '../components/ui/Input';
import { useContacts } from '../hooks/useContacts';
import gsap from 'gsap';
import { Search, Plus, Edit2, Trash2, ChevronLeft, ChevronRight, UploadCloud, FileText, FileSpreadsheet } from 'lucide-react';

const DashboardPage = () => {
    const { 
        contacts, page, setPage, totalPages, totalElements, 
        query, setQuery, loading, fetchContacts, 
        saveContact, deleteContact, exportContacts 
    } = useContacts();

    const [isContactModalOpen, setIsContactModalOpen] = useState(false);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [isImportModalOpen, setIsImportModalOpen] = useState(false);
    const [selectedContact, setSelectedContact] = useState(null);
    const tableRef = React.useRef(null);

    useEffect(() => {
        fetchContacts(page, query);
    }, [page, query, fetchContacts]);

    useEffect(() => {
        if (!loading && contacts.length > 0) {
            gsap.fromTo(
                '.table-row-anim',
                { y: 20, opacity: 0 },
                { y: 0, opacity: 1, duration: 0.4, stagger: 0.05, ease: 'power2.out' }
            );
        }
    }, [contacts, loading]);

    const handleSearchChange = (e) => {
        setQuery(e.target.value);
        setPage(0);
    };

    const handleSaveContact = async (contactData) => {
        try {
            await saveContact(contactData, selectedContact);
            setIsContactModalOpen(false);
        } catch (error) {
            alert(error.response?.data?.error || 'Error saving contact');
        }
    };

    const handleDeleteConfirm = async () => {
        try {
            await deleteContact(selectedContact.id);
            setIsDeleteModalOpen(false);
        } catch (error) {
            console.error('Failed to delete contact', error);
        }
    };

    const openCreateModal = () => {
        setSelectedContact(null);
        setIsContactModalOpen(true);
    };

    const openEditModal = (contact) => {
        setSelectedContact(contact);
        setIsContactModalOpen(true);
    };

    const openDeleteModal = (contact) => {
        setSelectedContact(contact);
        setIsDeleteModalOpen(true);
    };

    return (
        <>
            <SEO title="Dashboard" />
            <Navbar />

            <main className="container">
                <section className="page-header">
                    <div className="search-container" style={{ margin: 0, padding: 0 }}>
                        <Input 
                            type="text" 
                            className="search-input" 
                            placeholder="Search by first or last name..." 
                            value={query}
                            onChange={handleSearchChange}
                            icon={<Search size={20} />}
                            wrapperStyle={{ marginBottom: 0 }}
                            style={{ margin: 0 }}
                        />
                    </div>
                    <div>
                        <Button variant="secondary" onClick={() => exportContacts('csv')} title="Export CSV" style={{ fontSize: '0.875rem', padding: '0.5rem 1rem' }}>
                            <FileText size={16} /> <span>CSV</span>
                        </Button>
                        <Button variant="secondary" onClick={() => exportContacts('excel')} title="Export Excel" style={{ fontSize: '0.875rem', padding: '0.5rem 1rem' }}>
                            <FileSpreadsheet size={16} /> <span>Excel</span>
                        </Button>
                        <Button variant="secondary" onClick={() => setIsImportModalOpen(true)} title="Import" style={{ fontSize: '0.875rem', padding: '0.5rem 1rem' }}>
                            <UploadCloud size={16} /> <span>Import</span>
                        </Button>
                        <Button onClick={openCreateModal}>
                            <Plus size={18} /> Add Contact
                        </Button>
                    </div>
                </section>

                <section className="table-container" ref={tableRef}>
                    <table className="table">
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Title</th>
                                <th>Primary Email</th>
                                <th>Primary Phone</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {loading ? (
                                <tr>
                                    <td colSpan="5" style={{ textAlign: 'center', padding: '2rem' }}>
                                        <div className="spinner" style={{ margin: '0 auto', width: '30px', height: '30px' }}></div>
                                    </td>
                                </tr>
                            ) : contacts.length > 0 ? (
                                contacts.map(contact => (
                                    <tr key={contact.id} className="table-row-anim" style={{ opacity: 0 }}>
                                        <td data-label="Name" style={{ fontWeight: 500 }}>{contact.firstName} {contact.lastName}</td>
                                        <td data-label="Title" style={{ color: 'var(--text-secondary)' }}>{contact.title || '-'}</td>
                                        <td data-label="Primary Email">{contact.emailAddresses[0] || '-'}</td>
                                        <td data-label="Primary Phone">{contact.phoneNumbers[0] || '-'}</td>
                                        <td data-label="Actions">
                                            <div className="table-actions">
                                                <button className="action-btn" onClick={() => openEditModal(contact)}><Edit2 size={18} /></button>
                                                <button className="action-btn delete" onClick={() => openDeleteModal(contact)}><Trash2 size={18} /></button>
                                            </div>
                                        </td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="5" style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-secondary)' }}>
                                        No contacts found.
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </table>

                    <div className="pagination">
                        <div className="page-info">
                            Showing {contacts.length} of {totalElements} contacts
                        </div>
                        <div className="page-controls">
                            <button className="page-btn" disabled={page === 0} onClick={() => setPage(page - 1)}>
                                <ChevronLeft size={18} />
                            </button>
                            <button className="page-btn" disabled={page >= totalPages - 1} onClick={() => setPage(page + 1)}>
                                <ChevronRight size={18} />
                            </button>
                        </div>
                    </div>
                </section>
            </main>

            <ContactModal 
                isOpen={isContactModalOpen} 
                onClose={() => setIsContactModalOpen(false)} 
                onSave={handleSaveContact}
                initialData={selectedContact}
            />

            <DeleteConfirmModal 
                isOpen={isDeleteModalOpen}
                onClose={() => setIsDeleteModalOpen(false)}
                onConfirm={handleDeleteConfirm}
                contactName={selectedContact ? `${selectedContact.firstName} ${selectedContact.lastName}` : ''}
            />

            <ImportModal 
                isOpen={isImportModalOpen}
                onClose={() => setIsImportModalOpen(false)}
                onImportSuccess={() => {
                    setPage(0);
                    fetchContacts(0, query);
                    alert("Contacts imported successfully!");
                }}
            />
        </>
    );
};

export default DashboardPage;
