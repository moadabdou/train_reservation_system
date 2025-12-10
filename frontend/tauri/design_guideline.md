## Design System: "Velocity" Rail UI
**Core Philosophy:** Efficiency, Clarity, and Momentum.

The visual language of the **Velocity** reservation system balances the high-energy urgency of travel with the calm reliability of a robust scheduling platform. The aesthetic is clean and flat-modern, utilizing whitespace to reduce cognitive load during the complex booking process.



### 1. Color Palette: "Solar Flare"
The color scheme is designed to guide user attention immediately to Call-to-Action (CTA) elements while keeping content readable.

| Usage | Color Name | Hex Code | Description |
| :--- | :--- | :--- | :--- |
| **Primary** | **Vivid Tangerine** | `#FF6B35` | Used for primary CTAs (Search, Book), active states, and key highlights. |
| **Secondary** | **Deep Slate** | `#293241` | Used for headers, primary text, and navigation bars to ground the orange. |
| **Background** | **Vapor White** | `#F4F5F7` | A very light grey-blue off-white to reduce eye strain compared to pure white. |
| **Accent** | **Steel Blue** | `#98C1D9` | Used for secondary information, info icons, and non-urgent tags. |

### 2. Typography
We utilize a single sans-serif typeface family to maintain a strictly modern and digital-first feel.

* **Font Family:** *Inter* or *Roboto* (Google Fonts).
* **Headings:** Bold weight, Deep Slate color. High contrast for route names (e.g., **London → Paris**).
* **Body:** Regular weight, high legibility.
* **Data points:** Monospaced font (e.g., *Roboto Mono*) for times, prices, and train numbers to ensure alignment and readability.

### 3. Iconography & Shape Language
* **Shapes:** Elements feature **8px - 12px rounded corners** to soften the industrial nature of trains, creating a friendly, approachable interface.
* **Icons:** Minimalist line icons (1.5px stroke) in dark grey. Active icons fill with Primary Orange.
* **Shadows:** Soft, diffused drop shadows (`box-shadow: 0 4px 12px rgba(0,0,0,0.08)`) are used strictly on "Ticket Cards" to make them appear floatable and clickable.

### 4. Key UI Components

**The "Journey Card"**
The central element of the UI. A white card on a light grey background containing:
* **Left:** Departure Time (Big/Bold) + Station Code.
* **Center:** A horizontal line with a train icon indicating duration and stops.
* **Right:** Price in **Primary Orange**.
* **Interaction:** The entire card elevates slightly on hover.

**The Search Module**
A sticky or prominent top bar. Input fields have no top/side borders, only a solid bottom border that turns Orange when active. The "Search Trains" button is a full-width block of Primary Orange with white text.

> **Accessibility Note:** All white text placed on Orange backgrounds must be bolded to ensure a contrast ratio compliant with WCAG AA standards.
