import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import MainLayout from "./components/MainLayout";
import Home from "./components/Home";
import Login from "./components/Login";
import Register from "./components/Register";
import MyBookings from "./components/MyBookings";
import JourneyTracking from "./components/JourneyTracking";
import LoyaltyProgram from "./components/LoyaltyProgram";
import AdminLayout from "./pages/admin/AdminLayout";
import Providers from "./pages/admin/Providers";
import Trains from "./pages/admin/Trains";
import Stations from "./pages/admin/Stations";
import AdminRoutesPage from "./pages/admin/Routes";
import ScheduleGenerator from "./pages/admin/ScheduleGenerator";
import Schedules from "./pages/admin/Schedules";
import AdminUsers from "./pages/admin/AdminUsers";
import AdminBookings from "./pages/admin/AdminBookings";
import AdminFinancialDashboard from "./components/admin/AdminFinancialDashboard";
import AdminPaymentValidation from "./components/admin/AdminPaymentValidation";
import AdminDashboard from "./components/admin/AdminDashboard";
import AdminLoyalty from "./components/admin/AdminLoyalty";
import AdminContent from "./components/admin/AdminContent";
import AdminPricing from "./components/admin/AdminPricing";
import "./App.css";

function App() {
    return (
        <AuthProvider>
            <Router>
                <div className="app">
                    <Routes>
                        {/* User Routes with Header & Smart Companion */}
                        <Route element={<MainLayout />}>
                            <Route path="/" element={<Home />} />
                            <Route path="/login" element={<Login />} />
                            <Route path="/register" element={<Register />} />
                            <Route path="/my-bookings" element={<MyBookings />} />
                            <Route path="/journey/:scheduleId" element={<JourneyTracking />} />
                            <Route path="/loyalty" element={<LoyaltyProgram />} />
                        </Route>

                        {/* Admin Routes without Header */}
                        <Route path="/admin" element={<AdminLayout />}>
                            <Route index element={<AdminDashboard />} />
                            <Route path="dashboard" element={<AdminDashboard />} />
                            <Route path="providers" element={<Providers />} />
                            <Route path="trains" element={<Trains />} />
                            <Route path="stations" element={<Stations />} />
                            <Route path="routes" element={<AdminRoutesPage />} />
                            <Route path="schedules" element={<Schedules />} />
                            <Route path="schedules/generate" element={<ScheduleGenerator />} />
                            <Route path="users" element={<AdminUsers />} />
                            <Route path="bookings" element={<AdminBookings />} />
                            <Route path="financials" element={<AdminFinancialDashboard />} />
                            <Route path="payments" element={<AdminPaymentValidation />} />
                            <Route path="loyalty" element={<AdminLoyalty />} />
                            <Route path="content" element={<AdminContent />} />
                            <Route path="pricing" element={<AdminPricing />} />
                        </Route>
                    </Routes>
                </div>
            </Router>
        </AuthProvider>
    );
}
export default App;
