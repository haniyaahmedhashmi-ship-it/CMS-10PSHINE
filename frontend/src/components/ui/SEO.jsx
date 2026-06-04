import React from 'react';
import { Helmet } from 'react-helmet-async';

const SEO = ({ title, description }) => {
    return (
        <Helmet>
            <title>{title ? `${title} | Contact Manager` : 'Contact Manager'}</title>
            <meta name="description" content={description || 'A premium Contact Management System.'} />
        </Helmet>
    );
};

export default SEO;
