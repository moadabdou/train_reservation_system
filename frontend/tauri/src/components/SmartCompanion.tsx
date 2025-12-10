import React, { useState, useEffect, useRef } from "react";
import "./SmartCompanion.css";
import { getGeminiResponse, getSmartSuggestions } from "../services/geminiService";

interface SmartCompanionProps {
    destination?: string;
    currentStation?: string;
    className?: string;
}

interface Message {
    id: number;
    text: string;
    sender: "user" | "ai";
}

const SmartCompanion: React.FC<SmartCompanionProps> = ({ destination = "Morocco", currentStation, className = "" }) => {
    const [messages, setMessages] = useState<Message[]>([
        {
            id: 1,
            text: `Hi! I'm your AI travel companion. Ask me anything about ${destination} or your journey!`,
            sender: "ai",
        },
    ]);
    const [input, setInput] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [suggestions, setSuggestions] = useState<string[]>([]);
    const messagesEndRef = useRef<HTMLDivElement>(null);

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    };

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    useEffect(() => {
        const fetchSuggestions = async () => {
            // Mock weather for now, or fetch from an API if available
            const weather = "sunny and 25°C";
            const result = await getSmartSuggestions(destination, weather);
            setSuggestions(result);
        };
        fetchSuggestions();
    }, [destination]);

    const handleSend = async () => {
        if (!input.trim()) return;

        const userMessage: Message = {
            id: Date.now(),
            text: input,
            sender: "user",
        };

        setMessages((prev) => [...prev, userMessage]);
        setInput("");
        setIsLoading(true);

        const context = `I am currently on a train journey to ${destination}. ${currentStation ? `I am currently near ${currentStation}.` : ""}`;
        const prompt = `${context} User asks: ${input}`;

        const aiResponseText = await getGeminiResponse(prompt);

        const aiMessage: Message = {
            id: Date.now() + 1,
            text: aiResponseText,
            sender: "ai",
        };

        setMessages((prev) => [...prev, aiMessage]);
        setIsLoading(false);
    };

    const handleKeyPress = (e: React.KeyboardEvent) => {
        if (e.key === "Enter") {
            handleSend();
        }
    };

    return (
        <div className={`smart-companion-container ${className}`}>
            <div className="companion-header">
                <h2>🤖 Smart Companion</h2>
                <div className="eco-badge">🌱 You saved 12kg CO2</div>
            </div>

            <div className="suggestions-section">
                <h3>✨ Top Picks for {destination}</h3>
                <div className="suggestion-cards">
                    {suggestions.length > 0 ? (
                        suggestions.map((suggestion, index) => (
                            <div key={index} className="suggestion-card">
                                {suggestion}
                            </div>
                        ))
                    ) : (
                        <div className="suggestion-card">Loading smart suggestions...</div>
                    )}
                </div>
            </div>

            <div className="chat-section">
                <div className="chat-messages">
                    {messages.map((msg) => (
                        <div key={msg.id} className={`message ${msg.sender}`}>
                            {msg.text}
                        </div>
                    ))}
                    {isLoading && <div className="message ai">Thinking...</div>}
                    <div ref={messagesEndRef} />
                </div>
                <div className="chat-input-area">
                    <input
                        type="text"
                        value={input}
                        onChange={(e) => setInput(e.target.value)}
                        onKeyPress={handleKeyPress}
                        placeholder="Ask about food, landmarks, or history..."
                        disabled={isLoading}
                    />
                    <button onClick={handleSend} disabled={isLoading}>
                        Send
                    </button>
                </div>
            </div>
        </div>
    );
};

export default SmartCompanion;
