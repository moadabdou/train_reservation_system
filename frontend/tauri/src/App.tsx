import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import Header from "./components/Header";
import Home from "./components/Home";
import Login from "./components/Login";
import Register from "./components/Register";
import MyBookings from "./components/MyBookings";
import JourneyTracking from "./components/JourneyTracking";
import FloatingSmartCompanion from "./components/FloatingSmartCompanion";
import LoyaltyProgram from "./components/LoyaltyProgram";
import "./App.css";

function App() {
    return (
        <AuthProvider>
            <Router>
                <div className="app">
                    <Header />
                    <main>
                        <Routes>
                            <Route path="/" element={<Home />} />
                            <Route path="/login" element={<Login />} />
                            <Route path="/register" element={<Register />} />
                            <Route path="/my-bookings" element={<MyBookings />} />
                            <Route path="/journey/:scheduleId" element={<JourneyTracking />} />
                            <Route path="/loyalty" element={<LoyaltyProgram />} />
                        </Routes>
                    </main>
                    <FloatingSmartCompanion />
                </div>
            </Router>
        </AuthProvider>
    );
}

export default App;
