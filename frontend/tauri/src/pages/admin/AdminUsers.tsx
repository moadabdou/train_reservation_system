import React, { useEffect, useState } from "react";
import { adminService, User } from "../../services/adminService";
import "./AdminLayout.css"; // Reuse admin layout styles

const AdminUsers: React.FC = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(false);

    const fetchUsers = async () => {
        setLoading(true);
        try {
            const data = await adminService.getAllUsers(page);
            setUsers(data.content);
            setTotalPages(data.totalPages);
        } catch (error) {
            console.error("Failed to fetch users", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchUsers();
    }, [page]);

    const handleBan = async (id: number) => {
        if (window.confirm("Are you sure you want to ban this user?")) {
            try {
                await adminService.banUser(id);
                fetchUsers();
            } catch (error) {
                console.error("Failed to ban user", error);
            }
        }
    };

    const handleUnban = async (id: number) => {
        if (window.confirm("Are you sure you want to unban this user?")) {
            try {
                await adminService.unbanUser(id);
                fetchUsers();
            } catch (error) {
                console.error("Failed to unban user", error);
            }
        }
    };

    return (
        <div className="admin-content">
            <h2>User Management</h2>
            {loading ? (
                <p>Loading...</p>
            ) : (
                <>
                    <table className="admin-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Role</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {users.map((user) => (
                                <tr key={user.id}>
                                    <td>{user.id}</td>
                                    <td>{user.name}</td>
                                    <td>{user.email}</td>
                                    <td>{user.role}</td>
                                    <td>
                                        <span className={`status-badge ${user.status.toLowerCase()}`}>
                                            {user.status}
                                        </span>
                                    </td>
                                    <td>
                                        {user.status === "ACTIVE" ? (
                                            <button className="btn-danger" onClick={() => handleBan(user.id)}>
                                                Ban
                                            </button>
                                        ) : (
                                            <button className="btn-success" onClick={() => handleUnban(user.id)}>
                                                Unban
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

export default AdminUsers;
