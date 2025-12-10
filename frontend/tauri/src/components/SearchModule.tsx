import React, { useEffect, useState } from 'react';
import { getStations } from '../services/stationService';
import { StationDTO } from '../types';
import { Search, MapPin, Calendar } from 'lucide-react';
import './SearchModule.css';

interface SearchModuleProps {
    onSearch: (from: number, to: number, date: string) => void;
}

const SearchModule: React.FC<SearchModuleProps> = ({ onSearch }) => {
    const [stations, setStations] = useState<StationDTO[]>([]);
    const [fromStation, setFromStation] = useState<string>('');
    const [toStation, setToStation] = useState<string>('');
    const [date, setDate] = useState<string>('');

    useEffect(() => {
        const fetchStations = async () => {
            try {
                console.log("Fetching stations...");
                const data = await getStations();
                console.log("Stations fetched:", data);
                setStations(data);
            } catch (error) {
                console.error("Failed to fetch stations", error);
            }
        };
        fetchStations();
    }, []);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (fromStation && toStation && date) {
            onSearch(Number(fromStation), Number(toStation), date);
        }
    };

    return (
        <div className="search-container">
            <div className="search-header">
                <h1>Réservez vos tickets d'autocar en moins de 2 minutes</h1>
                <p>Avec MarkoubClone, accédez à 100+ opérateurs, profitez des meilleurs prix.</p>
            </div>
            <div className="search-module">
                <form onSubmit={handleSubmit} className="search-form">
                    <div className="form-group">
                        <label>Ville de départ</label>
                        <div className="input-wrapper">
                            <MapPin size={18} className="input-icon" />
                            <select 
                                value={fromStation} 
                                onChange={(e) => setFromStation(e.target.value)}
                                required
                            >
                                <option value="">Ville de départ</option>
                                {stations.map(station => (
                                    <option key={station.id} value={station.id}>{station.name}</option>
                                ))}
                            </select>
                        </div>
                    </div>

                    <div className="form-group">
                        <label>Ville d'arrivée</label>
                        <div className="input-wrapper">
                            <MapPin size={18} className="input-icon" />
                            <select 
                                value={toStation} 
                                onChange={(e) => setToStation(e.target.value)}
                                required
                            >
                                <option value="">Ville d'arrivée</option>
                                {stations.map(station => (
                                    <option key={station.id} value={station.id}>{station.name}</option>
                                ))}
                            </select>
                        </div>
                    </div>

                    <div className="form-group">
                        <label>Date de départ</label>
                        <div className="input-wrapper">
                            <Calendar size={18} className="input-icon" />
                            <input 
                                type="date" 
                                value={date} 
                                onChange={(e) => setDate(e.target.value)}
                                required
                            />
                        </div>
                    </div>

                    <div className="button-wrapper">
                        <button type="submit" className="search-button">
                            <Search size={20} />
                            <span>Recherche</span>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default SearchModule;
