import React, { useEffect, useState } from "react";
import { adminService, BookingSummary } from "../../services/adminService";
import "./AdminLayout.css";

const AdminBookings: React.FC = () => {
    const [bookings, setBookings] = useState<BookingSummary[]>([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(false);

    const fetchBookings = async () => {
        setLoading(true);
        try {
            const data = await adminService.getAllBookings(page);
            setBookings(data.content);
            setTotalPages(data.totalPages);
        } catch (error) {
            console.error("Failed to fetch bookings", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchBookings();
    }, [page]);

    const handleCancel = async (id: number) => {
        if (window.confirm("Are you sure you want to cancel this booking? This action cannot be undone.")) {
            try {
                await adminService.cancelBooking(id);
                fetchBookings();
            } catch (error) {
                console.error("Failed to cancel booking", error);
            }
        }
    };

    return (
        <div className="admin-content">
            <h2>Booking Management</h2>
            {loading ? (
                <p>Loading...</p>
            ) : (
                <>
                    <table className="admin-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Ref Code</th>
                                <th>User Email</th>
                                <th>Date</th>
                                <th>Status</th>
                                <th>Passengers</th>
                                <th>Total Price</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {bookings.map((booking) => (
                                <tr key={booking.bookingId}>
                                    <td>{booking.bookingId}</td>
                                    <td>{booking.referenceCode}</td>
                                    <td>{booking.userEmail}</td>
                                    <td>{new Date(booking.bookingDate).toLocaleString()}</td>
                                    <td>
                                        <span className={`status-badge ${booking.status.toLowerCase()}`}>
                                            {booking.status}
                                        </span>
                                    </td>
                                    <td>{booking.passengersCount}</td>
                                    <td>{booking.totalPrice} dh</td>
                                    <td>
                                        {booking.status !== "CANCELLED" && (
                                            <button
                                                className="btn-danger"
                                                onClick={() => handleCancel(booking.bookingId)}
                                            >
                                                Cancel
                                            </button>
                                        )}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                    <div className="pagination">
                        <button disabled={page === 0} onClick={() => setPage((p) => p - 1)}>
                            Previous
                        </button>
                        <span>
                            Page {page + 1} of {totalPages}
                        </span>
                        <button disabled={page >= totalPages - 1} onClick={() => setPage((p) => p + 1)}>
                            Next
                        </button>
                    </div>
                </>
            )}
        </div>
    );
};

export default AdminBookings;
