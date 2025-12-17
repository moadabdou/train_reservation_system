import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getMyBookings, cancelBooking, BookingSummary } from "../services/bookingService";
import { Ticket, Calendar, Users, X, ChevronLeft, ChevronRight, Eye, Map } from "lucide-react";
import BookingDetails from "./BookingDetails";
import ConfirmModal from "./ConfirmModal";
import "./MyBookings.css";

const MyBookings: React.FC = () => {
    const [bookings, setBookings] = useState<BookingSummary[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [cancellingId, setCancellingId] = useState<string | null>(null);
    const [selectedBookingRef, setSelectedBookingRef] = useState<string | null>(null);
    const [showCancelModal, setShowCancelModal] = useState(false);
    const [bookingToCancel, setBookingToCancel] = useState<string | null>(null);

    const fetchBookings = async (page: number = 0) => {
        setLoading(true);
        try {
            const response = await getMyBookings(page, 10);
            setBookings(response.content);
            setTotalPages(response.totalPages);
            setCurrentPage(response.number);
        } catch (err) {
            setError("Failed to load bookings");
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchBookings();
    }, []);

    const initiateCancel = (referenceCode: string) => {
        setBookingToCancel(referenceCode);
        setShowCancelModal(true);
    };

    const handleConfirmCancel = async () => {
        if (!bookingToCancel) return;

        setCancellingId(bookingToCancel);
        try {
            await cancelBooking(bookingToCancel);
            // Refresh bookings list
            await fetchBookings(currentPage);
            setShowCancelModal(false);
        } catch (err) {
            setError("Failed to cancel booking");
            console.error(err);
        } finally {
            setCancellingId(null);
            setBookingToCancel(null);
        }
    };

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleDateString("fr-FR", {
            day: "2-digit",
            month: "short",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit",
        });
    };

    const getStatusLabel = (status: string) => {
        switch (status) {
            case "CONFIRMED":
                return { label: "Confirmé", className: "status-confirmed" };
            case "CANCELLED":
                return { label: "Annulé", className: "status-cancelled" };
            case "COMPLETED":
                return { label: "Terminé", className: "status-completed" };
            default:
                return { label: status, className: "" };
        }
    };

    if (loading && bookings.length === 0) {
        return (
            <div className="my-bookings-container">
                <div className="loading-state">Chargement de vos réservations...</div>
            </div>
        );
    }

    return (
        <div className="my-bookings-container">
            <div className="my-bookings-header">
                <h1>
                    <Ticket size={28} />
                    Mes Réservations
                </h1>
                <Link to="/" className="back-home-btn">
                    Nouvelle réservation
                </Link>
            </div>

            {error && <div className="error-message">{error}</div>}

            {bookings.length === 0 ? (
                <div className="empty-state">
                    <Ticket size={64} className="empty-icon" />
                    <h2>Aucune réservation</h2>
                    <p>Vous n'avez pas encore effectué de réservation.</p>
                    <Link to="/" className="cta-button">
                        Réserver maintenant
                    </Link>
                </div>
            ) : (
                <>
                    <div className="bookings-list">
                        {bookings.map((booking) => {
                            const statusInfo = getStatusLabel(booking.status);
                            return (
                                <div key={booking.bookingId} className="booking-card">
                                    <div className="booking-header">
                                        <span className="reference-code">#{booking.referenceCode}</span>
                                        <span className={`status-badge ${statusInfo.className}`}>
                                            {statusInfo.label}
                                        </span>
                                    </div>

                                    <div className="booking-details">
                                        <div className="detail-item">
                                            <Calendar size={16} />
                                            <span>{formatDate(booking.bookingDate)}</span>
                                        </div>
                                        <div className="detail-item">
                                            <Users size={16} />
                                            <span>
                                                {booking.passengersCount} passager
                                                {booking.passengersCount > 1 ? "s" : ""}
                                            </span>
                                        </div>
                                    </div>

                                    <div className="booking-footer">
                                        <span className="total-price">{booking.totalPrice.toFixed(2)} dh</span>
                                        <div className="booking-actions">
                                            <button
                                                className="view-btn"
                                                onClick={() => setSelectedBookingRef(booking.referenceCode)}
                                            >
                                                <Eye size={16} />
                                                Détails
                                            </button>
                                            {booking.status === "CONFIRMED" && (
                                                <Link to={`/journey/${booking.scheduleId}`} className="track-btn">
                                                    <Map size={16} />
                                                    Suivre
                                                </Link>
                                            )}
                                            {(booking.status === "CONFIRMED" ||
                                                booking.status === "PENDING_PAYMENT") && (
                                                <button
                                                    className="cancel-btn"
                                                    onClick={() => initiateCancel(booking.referenceCode)}
                                                    disabled={cancellingId === booking.referenceCode}
                                                >
                                                    <X size={16} />
                                                    {cancellingId === booking.referenceCode
                                                        ? "Annulation..."
                                                        : "Annuler"}
                                                </button>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            );
                        })}
                    </div>

                    {totalPages > 1 && (
                        <div className="pagination">
                            <button
                                className="page-btn"
                                onClick={() => fetchBookings(currentPage - 1)}
                                disabled={currentPage === 0}
                            >
                                <ChevronLeft size={20} />
                            </button>
                            <span className="page-info">
                                Page {currentPage + 1} sur {totalPages}
                            </span>
                            <button
                                className="page-btn"
                                onClick={() => fetchBookings(currentPage + 1)}
                                disabled={currentPage >= totalPages - 1}
                            >
                                <ChevronRight size={20} />
                            </button>
                        </div>
                    )}
                </>
            )}

            {selectedBookingRef && (
                <BookingDetails
                    referenceCode={selectedBookingRef}
                    onClose={() => setSelectedBookingRef(null)}
                    bookingStatus={bookings.find((b) => b.referenceCode === selectedBookingRef)?.status}
                    onCancel={() => {
                        setSelectedBookingRef(null);
                        initiateCancel(selectedBookingRef);
                    }}
                />
            )}

            <ConfirmModal
                isOpen={showCancelModal}
                onClose={() => setShowCancelModal(false)}
                onConfirm={handleConfirmCancel}
                title="Annuler la réservation"
                message="Êtes-vous sûr de vouloir annuler cette réservation ? Cette action est irréversible."
                isLoading={cancellingId !== null}
            />
        </div>
    );
};

export default MyBookings;
