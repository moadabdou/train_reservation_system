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
                            <Route path="providers" element={<Providers />} />
                            <Route path="trains" element={<Trains />} />
                            <Route path="stations" element={<Stations />} />
                        </Route>
                    </Routes>
                </div>
            </Router>
        </AuthProvider>
    );
}
export default App;
