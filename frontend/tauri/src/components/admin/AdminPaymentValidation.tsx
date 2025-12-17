import React, { useEffect, useState } from "react";
import { adminService, Payment } from "../../services/adminService";
import "./AdminPaymentValidation.css";

const AdminPaymentValidation: React.FC = () => {
    const [payments, setPayments] = useState<Payment[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadPayments();
    }, []);

    const loadPayments = async () => {
        try {
            const data = await adminService.getAllPayments();
            setPayments(data);
        } catch (error) {
            console.error("Failed to load payments", error);
        } finally {
            setLoading(false);
        }
    };

    const handleValidate = async (id: number) => {
        try {
            await adminService.validatePayment(id);
            // Refresh list or update local state
            setPayments((prev) => prev.map((p) => (p.id === id ? { ...p, status: "COMPLETED" } : p)));
        } catch (error) {
            console.error("Failed to validate payment", error);
            alert("Failed to validate payment");
        }
    };

    if (loading) return <div>Loading payments...</div>;

    return (
        <div className="payment-validation">
            <h2>Payment Validation Queue</h2>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Booking ID</th>
                        <th>Amount</th>
                        <th>Method</th>
                        <th>Transaction ID</th>
                        <th>Date</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    {payments.map((payment) => (
                        <tr key={payment.id}>
                            <td>{payment.id}</td>
                            <td>{payment.bookingId}</td>
                            <td>{payment.amount.toFixed(2)} dh</td>
                            <td>{payment.paymentMethod}</td>
                            <td>{payment.transactionId}</td>
                            <td>{new Date(payment.paymentDate).toLocaleString()}</td>
                            <td>
                                <span className={`status-badge ${payment.status.toLowerCase()}`}>{payment.status}</span>
                            </td>
                            <td>
                                {payment.status === "PENDING" && (
                                    <button onClick={() => handleValidate(payment.id)}>Validate</button>
                                )}
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default AdminPaymentValidation;
