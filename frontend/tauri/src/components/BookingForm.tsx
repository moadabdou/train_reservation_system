import React, { useState } from 'react';
import { ScheduleDTO } from '../types';
import { createBooking, processPayment } from '../services/bookingService';
import { ArrowLeft, User, Plus, Trash2, CreditCard, Calendar, Lock } from 'lucide-react';
import './BookingForm.css';

interface BookingFormProps {
    schedule: ScheduleDTO;
    onBack: () => void;
    onSuccess: () => void;
}

interface Passenger {
    name: string;
    age: string;
}

interface PaymentDetails {
    cardNumber: string;
    expiryDate: string;
    cvv: string;
    cardHolder: string;
}

const BookingForm: React.FC<BookingFormProps> = ({ schedule, onBack, onSuccess }) => {
    const [step, setStep] = useState(1);
    const [passengers, setPassengers] = useState<Passenger[]>([{ name: '', age: '' }]);
    const [paymentDetails, setPaymentDetails] = useState<PaymentDetails>({
        cardNumber: '',
        expiryDate: '',
        cvv: '',
        cardHolder: ''
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const addPassenger = () => {
        setPassengers([...passengers, { name: '', age: '' }]);
    };

    const removePassenger = (index: number) => {
        if (passengers.length > 1) {
            const newPassengers = [...passengers];
            newPassengers.splice(index, 1);
            setPassengers(newPassengers);
        }
    };

    const updatePassenger = (index: number, field: keyof Passenger, value: string) => {
        const newPassengers = [...passengers];
        newPassengers[index] = { ...newPassengers[index], [field]: value };
        setPassengers(newPassengers);
    };

    const updatePayment = (field: keyof PaymentDetails, value: string) => {
        setPaymentDetails({ ...paymentDetails, [field]: value });
    };

    const handleNextStep = (e: React.FormEvent) => {
        e.preventDefault();
        // Validate passengers
        const isValid = passengers.every(p => p.name.trim() && p.age);
        if (!isValid) {
            setError("Veuillez remplir tous les champs passagers");
            return;
        }
        setError(null);
        setStep(2);
    };

    const handleBack = () => {
        if (step === 2) {
            setStep(1);
        } else {
            onBack();
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        
        // Validate payment
        if (!paymentDetails.cardNumber || !paymentDetails.expiryDate || !paymentDetails.cvv || !paymentDetails.cardHolder) {
            setError("Veuillez remplir tous les détails de paiement");
            return;
        }

        setLoading(true);
        setError(null);

        try {
            const bookingRequest = {
                scheduleId: schedule.id,
                passengers: passengers.map(p => ({
                    name: p.name,
                    age: parseInt(p.age) || 0
                }))
            };

            const booking = await createBooking(bookingRequest);
            
            await processPayment({
                bookingId: booking.bookingId,
                paymentMethod: 'CREDIT_CARD',
                amount: booking.totalPrice,
                cardNumber: paymentDetails.cardNumber,
                expiryDate: paymentDetails.expiryDate,
                cvv: paymentDetails.cvv
            });

            onSuccess();
        } catch (err) {
            console.error(err);
            setError("Échec de la réservation. Veuillez réessayer.");
        } finally {
            setLoading(false);
        }
    };

    const totalPrice = schedule.price * passengers.length;

    return (
        <div className="booking-container">
            <button onClick={handleBack} className="back-button">
                <ArrowLeft size={20} />
                <span>{step === 1 ? 'Retour aux résultats' : 'Retour aux passagers'}</span>
            </button>

            <div className="booking-steps">
                <div className={`step ${step >= 1 ? 'active' : ''}`}>1. Passagers</div>
                <div className="step-line"></div>
                <div className={`step ${step >= 2 ? 'active' : ''}`}>2. Paiement</div>
            </div>

            <div className="booking-layout">
                <div className="booking-main">
                    {step === 1 ? (
                        <div className="section-card">
                            <h2>Détails des passagers</h2>
                            <form id="passenger-form" onSubmit={handleNextStep}>
                                {passengers.map((passenger, index) => (
                                    <div key={index} className="passenger-row">
                                        <div className="passenger-header">
                                            <span className="passenger-label">Passager {index + 1}</span>
                                            {passengers.length > 1 && (
                                                <button 
                                                    type="button" 
                                                    onClick={() => removePassenger(index)}
                                                    className="remove-btn"
                                                >
                                                    <Trash2 size={16} />
                                                </button>
                                            )}
                                        </div>
                                        <div className="form-row">
                                            <div className="form-group">
                                                <label>Nom complet</label>
                                                <div className="input-wrapper">
                                                    <User size={18} className="input-icon" />
                                                    <input 
                                                        type="text" 
                                                        placeholder="Ex: Ahmed Alami"
                                                        value={passenger.name}
                                                        onChange={(e) => updatePassenger(index, 'name', e.target.value)}
                                                        required
                                                    />
                                                </div>
                                            </div>
                                            <div className="form-group age-group">
                                                <label>Âge</label>
                                                <div className="input-wrapper">
                                                    <input 
                                                        type="number" 
                                                        placeholder="25"
                                                        value={passenger.age}
                                                        onChange={(e) => updatePassenger(index, 'age', e.target.value)}
                                                        required
                                                        min="0"
                                                        max="120"
                                                    />
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                                
                                <button type="button" onClick={addPassenger} className="add-passenger-btn">
                                    <Plus size={18} />
                                    Ajouter un passager
                                </button>
                            </form>
                        </div>
                    ) : (
                        <div className="section-card">
                            <h2>Paiement sécurisé</h2>
                            <form id="payment-form" onSubmit={handleSubmit}>
                                <div className="payment-form">
                                    <div className="form-group">
                                        <label>Titulaire de la carte</label>
                                        <div className="input-wrapper">
                                            <User size={18} className="input-icon" />
                                            <input 
                                                type="text" 
                                                placeholder="Nom sur la carte"
                                                value={paymentDetails.cardHolder}
                                                onChange={(e) => updatePayment('cardHolder', e.target.value)}
                                                required
                                            />
                                        </div>
                                    </div>

                                    <div className="form-group">
                                        <label>Numéro de carte</label>
                                        <div className="input-wrapper">
                                            <CreditCard size={18} className="input-icon" />
                                            <input 
                                                type="text" 
                                                placeholder="0000 0000 0000 0000"
                                                value={paymentDetails.cardNumber}
                                                onChange={(e) => updatePayment('cardNumber', e.target.value)}
                                                maxLength={19}
                                                required
                                            />
                                        </div>
                                    </div>

                                    <div className="form-row">
                                        <div className="form-group">
                                            <label>Date d'expiration</label>
                                            <div className="input-wrapper">
                                                <Calendar size={18} className="input-icon" />
                                                <input 
                                                    type="text" 
                                                    placeholder="MM/YY"
                                                    value={paymentDetails.expiryDate}
                                                    onChange={(e) => updatePayment('expiryDate', e.target.value)}
                                                    maxLength={5}
                                                    required
                                                />
                                            </div>
                                        </div>
                                        <div className="form-group">
                                            <label>CVV</label>
                                            <div className="input-wrapper">
                                                <Lock size={18} className="input-icon" />
                                                <input 
                                                    type="text" 
                                                    placeholder="123"
                                                    value={paymentDetails.cvv}
                                                    onChange={(e) => updatePayment('cvv', e.target.value)}
                                                    maxLength={3}
                                                    required
                                                />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                    )}
                </div>

                <div className="booking-sidebar">
                    <div className="summary-card">
                        <h3>Récapitulatif</h3>
                        <div className="summary-row">
                            <span>Train</span>
                            <span>{schedule.trainName}</span>
                        </div>
                        <div className="summary-row">
                            <span>Départ</span>
                            <span>{schedule.departureTime.split('T')[1].substring(0, 5)}</span>
                        </div>
                        <div className="summary-row">
                            <span>Arrivée</span>
                            <span>{schedule.arrivalTime.split('T')[1].substring(0, 5)}</span>
                        </div>
                        <div className="divider"></div>
                        <div className="summary-row">
                            <span>Prix par personne</span>
                            <span>{schedule.price} MAD</span>
                        </div>
                        <div className="summary-row">
                            <span>Passagers</span>
                            <span>x {passengers.length}</span>
                        </div>
                        <div className="divider"></div>
                        <div className="summary-total">
                            <span>Total à payer</span>
                            <span>{totalPrice.toFixed(2)} MAD</span>
                        </div>

                        {error && <div className="error-message">{error}</div>}

                        <button 
                            type="submit" 
                            form={step === 1 ? "passenger-form" : "payment-form"}
                            className="confirm-btn"
                            disabled={loading}
                        >
                            {loading ? 'Traitement...' : (step === 1 ? 'Continuer vers le paiement' : 'Payer et Confirmer')}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default BookingForm;
