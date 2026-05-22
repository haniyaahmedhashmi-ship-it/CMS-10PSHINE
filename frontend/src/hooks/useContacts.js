import { useState, useCallback } from 'react';
import api from '../api/axios';

export const useContacts = () => {
    const [contacts, setContacts] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const [query, setQuery] = useState('');
    const [loading, setLoading] = useState(false);

    const fetchContacts = useCallback(async (currentPage, currentQuery) => {
        setLoading(true);
        try {
            const endpoint = currentQuery 
                ? `/contacts/search?query=${currentQuery}&page=${currentPage}&size=10` 
                : `/contacts?page=${currentPage}&size=10`;
            const response = await api.get(endpoint);
            setContacts(response.data.content);
            setTotalPages(response.data.totalPages);
            setTotalElements(response.data.totalElements);
        } catch (error) {
            console.error('Failed to fetch contacts', error);
        } finally {
            setLoading(false);
        }
    }, []);

    const saveContact = async (contactData, selectedContact) => {
        if (selectedContact) {
            await api.put(`/contacts/${selectedContact.id}`, contactData);
        } else {
            await api.post('/contacts', contactData);
        }
        await fetchContacts(page, query);
    };

    const deleteContact = async (id) => {
        await api.delete(`/contacts/${id}`);
        await fetchContacts(page, query);
    };

    const exportContacts = async (format) => {
        try {
            const response = await api.get(`/contacts/export/${format}`, {
                responseType: 'blob',
            });
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', `contacts.${format === 'excel' ? 'xlsx' : 'csv'}`);
            document.body.appendChild(link);
            link.click();
            link.remove();
        } catch (error) {
            console.error(`Failed to export ${format}`, error);
            throw new Error(`Failed to export ${format.toUpperCase()}`);
        }
    };

    return {
        contacts,
        page,
        setPage,
        totalPages,
        totalElements,
        query,
        setQuery,
        loading,
        fetchContacts,
        saveContact,
        deleteContact,
        exportContacts
    };
};
