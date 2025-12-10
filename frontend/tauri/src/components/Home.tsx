import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import SearchModule from './SearchModule';
import ScheduleList from './ScheduleList';
import BookingForm from './BookingForm';
import { searchSchedules } from '../services/scheduleService';
import { useAuth } from '../context/AuthContext';
import { ScheduleDTO } from '../types';

function Home() {
  const [schedules, setSchedules] = useState<ScheduleDTO[]>([]);
  const [loading, setLoading] = useState(false);
  const [hasSearched, setHasSearched] = useState(false);
  const [selectedSchedule, setSelectedSchedule] = useState<ScheduleDTO | null>(null);
  const [bookingSuccess, setBookingSuccess] = useState(false);
  const { isLoggedIn } = useAuth();
  const navigate = useNavigate();

  const handleSearch = async (from: number, to: number, date: string) => {
    setLoading(true);
    setHasSearched(true);
    setSelectedSchedule(null);
    setBookingSuccess(false);
    try {
      const results = await searchSchedules(from, to, date);
      setSchedules(results);
    } catch (error) {
      console.error("Error searching schedules:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleSelectSchedule = (schedule: ScheduleDTO) => {
    if (!isLoggedIn) {
      // Redirect to login if not authenticated
      navigate('/login');
      return;
    }
    setSelectedSchedule(schedule);
  };

  const handleBookingSuccess = () => {
    setBookingSuccess(true);
    setSelectedSchedule(null);
  };

  return (
    <>
      {!selectedSchedule && !bookingSuccess && (
        <>
          <SearchModule onSearch={handleSearch} />
          <div className="container">
            {hasSearched && (
              <div className="results-section">
                <h2 className="section-title">
                  {loading ? 'Recherche en cours...' : `${schedules.length} Voyages trouvés`}
                </h2>
                <ScheduleList 
                  schedules={schedules} 
                  loading={loading} 
                  onSelect={handleSelectSchedule}
                />
              </div>
            )}
          </div>
        </>
      )}

      {selectedSchedule && (
        <div className="container" style={{ marginTop: '40px' }}>
          <BookingForm 
            schedule={selectedSchedule} 
            onBack={() => setSelectedSchedule(null)}
            onSuccess={handleBookingSuccess}
          />
        </div>
      )}

      {bookingSuccess && (
        <div className="container" style={{ marginTop: '80px', textAlign: 'center' }}>
          <div className="success-card" style={{ padding: '40px', backgroundColor: 'white', borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
            <h2 style={{ color: '#28a745', marginBottom: '20px' }}>Réservation confirmée !</h2>
            <p>Votre réservation a été effectuée avec succès.</p>
            <button 
              onClick={() => { setBookingSuccess(false); setHasSearched(false); }}
              style={{ 
                marginTop: '20px', 
                padding: '10px 20px', 
                backgroundColor: '#0056b3', 
                color: 'white', 
                borderRadius: '4px',
                fontWeight: 'bold'
              }}
            >
              Retour à l'accueil
            </button>
          </div>
        </div>
      )}
    </>
  );
}

export default Home;
