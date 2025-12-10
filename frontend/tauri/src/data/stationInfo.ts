import { StationDTO } from "../types";

export interface ExtendedStationInfo extends Partial<StationDTO> {
    galleryImages?: string[];
    longDescription?: string;
    attractions?: string[];
}

export const STATION_INFO: Record<string, ExtendedStationInfo> = {
    "Tanger Ville": {
        imageUrl: "https://images.unsplash.com/photo-1580674684081-7617fbf3d745?q=80&w=1000&auto=format&fit=crop",
        funFact:
            "Tangier is the only city where you can swim in both the Atlantic Ocean and the Mediterranean Sea on the same day!",
        longDescription:
            "Tangier, a Moroccan port on the Strait of Gibraltar, has been a strategic gateway between Africa and Europe since Phoenician times. Its whitewashed hillside medina is home to the Dar el Makhzen, a palace of the sultans that's now a museum of Moroccan artifacts.",
        galleryImages: [
            "https://images.unsplash.com/photo-1580674684081-7617fbf3d745?q=80&w=1000&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1584286595398-a59f21d313f5?q=80&w=1000&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1552832230-c0197dd311b5?q=80&w=1000&auto=format&fit=crop",
        ],
        attractions: ["Hercules Caves", "Cap Spartel", "The Kasbah Museum"],
    },
    Kenitra: {
        imageUrl: "https://images.unsplash.com/photo-1532105956626-9569c03602f6?q=80&w=1000&auto=format&fit=crop",
        funFact:
            "Kenitra is home to the Mehdia beach, a popular surfing destination and the site of the first American landing in North Africa during WWII.",
        longDescription:
            "Kenitra is a city in northern Morocco, formerly known as Port Lyautey. It is a port on the Sebou River, has a population in 2014 of 431,282, is one of the three main cities of the Rabat-Salé-Kénitra region and the capital of the Kenitra Province.",
        galleryImages: [
            "https://images.unsplash.com/photo-1532105956626-9569c03602f6?q=80&w=1000&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1474487548417-781cb71495f3?q=80&w=1000&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1539020140153-e479b8c22e70?q=80&w=1000&auto=format&fit=crop", // Placeholder
        ],
        attractions: ["Mehdia Beach", "Lake Sidi Boughaba", "Thamusida Ruins"],
    },
    "Rabat Agdal": {
        imageUrl: "https://images.unsplash.com/photo-1539020140153-e479b8c22e70?q=80&w=1000&auto=format&fit=crop",
        funFact:
            "Rabat is one of the four Imperial Cities of Morocco and its medina is listed as a World Heritage Site.",
        longDescription:
            "Rabat, Morocco's capital, rests along the shores of the Bouregreg River and the Atlantic Ocean. It's known for landmarks that speak to its Islamic and French-colonial heritage, including the Kasbah of the Udayas.",
        galleryImages: [
            "https://images.unsplash.com/photo-1539020140153-e479b8c22e70?q=80&w=1000&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1553531384-cc64ac80f931?q=80&w=1000&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1535202468728-6f605d10d686?q=80&w=1000&auto=format&fit=crop",
        ],
        attractions: ["Hassan Tower", "Kasbah of the Udayas", "Chellah"],
    },
    "Casa Voyageurs": {
        imageUrl: "https://images.unsplash.com/photo-1577147443647-81856d5151af?q=80&w=1000&auto=format&fit=crop",
        funFact: "Casablanca is home to the Hassan II Mosque, which has the second tallest minaret in the world!",
        longDescription:
            "Casablanca is a port city and commercial hub in western Morocco, fronting the Atlantic Ocean. The city's French colonial legacy is seen in its downtown Mauresque architecture, a blend of Moorish style and European art deco.",
        galleryImages: [
            "https://images.unsplash.com/photo-1577147443647-81856d5151af?q=80&w=1000&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1534445967799-76c736307469?q=80&w=1000&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1569336415962-a4bd9f69cd83?q=80&w=1000&auto=format&fit=crop",
        ],
        attractions: ["Hassan II Mosque", "Rick's Café", "The Corniche"],
    },
    // Fallback for generic stations
    default: {
        imageUrl: "https://images.unsplash.com/photo-1474487548417-781cb71495f3?q=80&w=1000&auto=format&fit=crop",
        funFact: "Train travel is one of the most eco-friendly ways to explore the country.",
        longDescription:
            "Experience the beauty of Morocco by train. Relax and enjoy the changing landscapes as you travel between cities.",
        galleryImages: [
            "https://images.unsplash.com/photo-1474487548417-781cb71495f3?q=80&w=1000&auto=format&fit=crop",
        ],
        attractions: ["Local Markets", "City Center", "Historic Sites"],
    },
};

export const getStationInfo = (stationName: string) => {
    // Try to find exact match or partial match
    const key = Object.keys(STATION_INFO).find((k) => stationName.includes(k));
    return key ? STATION_INFO[key] : STATION_INFO["default"];
};
