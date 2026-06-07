import React from 'react';

const Input = React.forwardRef(({ label, error, className = '', id, icon, wrapperStyle, style, ...props }, ref) => {
    const inputId = id || `input-${Math.random().toString(36).substring(2, 9)}`;
    
    return (
        <div className="form-group" style={wrapperStyle}>
            {label && <label htmlFor={inputId} className="form-label">{label}</label>}
            <div style={{ position: 'relative', display: 'flex', alignItems: 'center' }}>
                {icon && (
                    <span style={{
                        position: 'absolute',
                        left: '0.85rem',
                        top: '50%',
                        transform: 'translateY(-50%)',
                        color: 'var(--text-secondary)',
                        display: 'inline-flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        pointerEvents: 'none',
                        lineHeight: 0,
                    }}>
                        {icon}
                    </span>
                )}
                <input 
                    id={inputId}
                    ref={ref}
                    className={`form-input ${className}`} 
                    style={{
                        paddingLeft: icon ? '2.75rem' : undefined,
                        ...style,
                    }}
                    {...props} 
                />
            </div>
            {error && <div className="error-message">{error}</div>}
        </div>
    );
});

Input.displayName = 'Input';

export default React.memo(Input);
