import { GoogleGenAI } from "@google/genai";

const API_KEY = import.meta.env.VITE_GEMINI_API_KEY;

// Initialize the client
// We pass the apiKey explicitly because Vite env vars are not automatically picked up by the library as process.env
const ai = new GoogleGenAI({ apiKey: API_KEY });

export const getGeminiResponse = async (prompt: string) => {
    if (!API_KEY || API_KEY === "YOUR_GEMINI_API_KEY_HERE") {
        console.warn("Gemini API Key is missing.");
        return "I'm your Smart Companion! Please add a valid Gemini API key to start chatting. For now, I can tell you that Morocco is beautiful!";
    }

    try {
        const response = await ai.models.generateContent({
            model: "gemini-2.5-flash",
            contents: prompt,
        });

        return response.text || "I couldn't generate a response.";
    } catch (error) {
        console.error("Error calling Gemini API:", error);
        return "Sorry, I'm having trouble connecting to the AI service right now.";
    }
};

export const getSmartSuggestions = async (destination: string, weather: string) => {
    const prompt = `I am traveling to ${destination} by train. The weather is ${weather}. 
     Give me 3 short "Must Visit" suggestions for today. 
     Format the response as a JSON array of strings. Do not include markdown formatting.`;

    // For now, return mock data if no key, or try to call if key exists
    if (!API_KEY || API_KEY === "YOUR_GEMINI_API_KEY_HERE") {
        return [
            `Visit the historic Medina of ${destination}`,
            `Enjoy a traditional tea at a local cafe`,
            `Explore the nearby museums`,
        ];
    }

    try {
        const textResponse = await getGeminiResponse(prompt);
        // Attempt to parse JSON from the response
        // Clean up potential markdown code blocks
        const cleanJson = textResponse
            .replace(/```json/g, "")
            .replace(/```/g, "")
            .trim();
        return JSON.parse(cleanJson);
    } catch (e) {
        console.error("Failed to parse smart suggestions", e);
        return [
            `Visit the historic Medina of ${destination}`,
            `Enjoy a traditional tea at a local cafe`,
            `Explore the nearby museums`,
        ];
    }
};
