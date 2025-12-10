import React from 'react';
import { X, AlertTriangle } from 'lucide-react';
import './ConfirmModal.css';

interface ConfirmModalProps {
    isOpen: boolean;
    onClose: () => void;
    onConfirm: () => void;
    title: string;
    message: string;
    isLoading?: boolean;
}

const ConfirmModal: React.FC<ConfirmModalProps> = ({ 
    isOpen, 
    onClose, 
    onConfirm, 
    title, 
    message,
    isLoading = false 
}) => {
    if (!isOpen) return null;

    return (
        <div className="modal-overlay">
            <div className="modal-content confirm-modal">
                <button className="modal-close-btn" onClick={onClose} disabled={isLoading}>
                    <X size={20} />
                </button>
                
                <div className="confirm-icon">
                    <AlertTriangle size={48} />
                </div>
                
                <h3>{title}</h3>
                <p>{message}</p>
                
                <div className="confirm-actions">
                    <button className="btn-cancel" onClick={onClose} disabled={isLoading}>
                        Non, garder
                    </button>
                    <button className="btn-confirm" onClick={onConfirm} disabled={isLoading}>
                        {isLoading ? 'Traitement...' : 'Oui, annuler'}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ConfirmModal;