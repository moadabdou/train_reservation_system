import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

function App() {
    const [message, setMessage] = useState('Loading...');

    useEffect(() => {
        // The proxy in package.json will forward this request to the Spring Boot backend
        axios.get('/api/v1/greeting')
            .then(response => {
                setMessage(response.data.message);
            })
            .catch(error => {
                console.error("There was an error fetching the data!", error);
                setMessage('Failed to load message from backend.');
            });
    }, []);

    return (
        <div className="App">
            <header className="App-header">
                <h1>{message}</h1>
            </header>
        </div>
    );
}

export default App;