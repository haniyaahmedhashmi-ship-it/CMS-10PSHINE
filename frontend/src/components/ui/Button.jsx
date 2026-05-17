import React from 'react';

const Button = ({ children, variant = 'primary', className = '', loading = false, ...props }) => {
    const baseStyle = 'style={{ display: "flex", alignItems: "center", gap: "0.5rem" }}';
    const variantClass = variant === 'danger' ? 'btn-danger' : variant === 'secondary' ? 'btn-secondary' : 'btn-primary';
    
    return (
        <button 
            className={`${variantClass} ${className}`} 
            disabled={loading || props.disabled}
            {...props}
        >
            {loading ? <div className="spinner" style={{ width: '16px', height: '16px', borderWidth: '2px' }}></div> : children}
        </button>
    );
};

export default React.memo(Button);
