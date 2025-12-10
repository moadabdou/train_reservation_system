# USER FEATURES AND DEV GUIDE

1.  **Viewing Trains & Schedules**
2.  **Account Registration & Login**
3.  **Booking a Ticket**
4.  **Viewing Booking History**
5.  **Cancelling a Booking**

Here’s a complete development guide for building these features using the vertical slice method you liked.

---

### Development Guide: User Features (Vertical Slice Method)

This guide breaks down each core feature into a self-contained "slice." For each slice, you'll work on the database, the back-end logic, and the front-end interface, ensuring you have a working piece of the application at the end of each step.

#### **Slice 0: The Foundation (Setup)**

Before writing any features, you need to set up your project. This isn't a user feature, but it's a critical first step.

- **Goal:** Get a basic, empty project running.
- **Tasks:**
    1.  **Choose Your Tech Stack:** JAVA / SPRING / ORACLE DB (BACK), REACTJS (FRONT) ?
    2.  **Project Setup:** Create your project folder, initialize a Git repository, and install necessary libraries.
    3.  **Database Connection:** Configure your application to connect to your database.
    4.  **Basic Server:** Create a simple web server that can respond to a request (e.g., returns "Hello, World!").

---

#### **Slice 1: View Train Schedules**

This is the most fundamental feature. Users need to be able to see what trains are available.

- **🎯 Goal:** A visitor can search for trains between two stations on a specific date and see a list of results.
- **⚙️ How-to:**
    1.  **Database:**
        - Design and create your initial tables: `Stations` (id, name), `Trains` (id, name, total_seats), and `Schedules` (id, train_id, departure_station_id, arrival_station_id, departure_time, arrival_time, price, available_seats).
        - Manually add some sample data into these tables so you have something to display.
    2.  **Back-end:**
        - Create an API endpoint, for example, `GET /api/schedules`.
        - This endpoint should accept query parameters like `from`, `to`, and `date`.
        - Write the logic to query the `Schedules` table based on these parameters and return the matching trains as JSON data.
    3.  **Front-end:**
        - Build the main search page with input fields for "Departure Station," "Arrival Station," and "Date."
        - When the user clicks "Search," make an API call to your back-end endpoint.
        - Display the results neatly in a list, showing train name, times, duration, and price.

---

#### **Slice 2: User Account Management**

Users need an account to manage their bookings.

- **🎯 Goal:** A new user can create an account. An existing user can log in and log out.
- **⚙️ How-to:**
    1.  **Database:**
        - Create a `Users` table with columns like `id`, `name`, `email`, and `password_hash`. **Crucially, never store plain-text passwords.** Always store a hashed and salted version.
    2.  **Back-end:**
        - Create a `POST /api/register` endpoint to handle new user sign-ups. It should validate the data, hash the password, and save the new user to the database.
        - Create a `POST /api/login` endpoint to authenticate users. It compares the provided password with the stored hash. If successful, it creates a session or a token (like a JWT) to keep the user logged in.
        - Create a `POST /api/logout` endpoint to invalidate the session/token.
    3.  **Front-end:**
        - Build the UI for the registration and login forms.
        - Connect these forms to your new API endpoints.
        - Update the UI to show whether a user is logged in or not (e.g., show "Login/Register" or "My Account/Logout").

---

#### **Slice 3: Ticket Booking**

This is the core transaction of your system.

- **🎯 Goal:** A logged-in user can select a train from the search results and book a ticket for one or more passengers.
- **⚙️ How-to:**
    1.  **Database:**
        - Create a `Bookings` table (e.g., `id`, `user_id`, `schedule_id`, `booking_date`, `status`).
        - Create a `Passengers` table (e.g., `id`, `booking_id`, `name`, `age`).
    2.  **Back-end:**
        - Create a `POST /api/bookings` endpoint. This is your most important piece of logic!
        - It must be a **secure** endpoint, meaning only logged-in users can access it.
        - The logic should:
            1.  Check if there are enough `available_seats` for the requested number of passengers.
            2.  If yes, create a new record in the `Bookings` table.
            3.  Create records for each passenger in the `Passengers` table.
            4.  **Decrement** the `available_seats` in the `Schedules` table for that train. This must be an atomic transaction to prevent overbooking.
            5.  Return a success message with the booking details.
    3.  **Front-end:**
        - When a user clicks "Book" on a train, take them to a page where they can enter passenger details.
        - On submitting this form, call the booking API.
        - After a successful booking, show a confirmation page with a unique booking ID.

---

#### **Slice 4: View Booking History**

Users need a way to see their tickets.

- **🎯 Goal:** A logged-in user can view a list of their past and upcoming bookings.
- **⚙️ How-to:**
    1.  **Database:** No new tables needed.
    2.  **Back-end:**
        - Create a secure endpoint like `GET /api/my-bookings`.
        - This endpoint should find the current user's ID from their session/token and retrieve all associated records from the `Bookings` table.
    3.  **Front-end:**
        - Create a "My Bookings" page in the user's profile area.
        - Call the `my-bookings` API and display the results in a clear list, separating upcoming trips from past ones.

---

#### **Slice 5: Cancel a Booking**

This feature completes the booking lifecycle.

- **🎯 Goal:** A user can cancel an upcoming booking from their history page.
- **⚙️ How-to:**
    1.  **Database:** You can simply add a `status` column to your `Bookings` table if you haven't already (e.g., with values like 'CONFIRMED', 'CANCELLED').
    2.  **Back-end:**
        - Create a secure endpoint like `POST /api/bookings/:id/cancel`.
        - The logic should:
            1.  Verify that the booking belongs to the logged-in user.
            2.  Change the booking's `status` to 'CANCELLED'.
            3.  **Increment** the `available_seats` on the corresponding `Schedules` record. This is the reverse of the booking process.
    3.  **Front-end:**
        - On the "My Bookings" page, add a "Cancel" button next to each upcoming trip.
        - When clicked, it should call the cancellation API and update the UI to show the trip as cancelled.

---

#### **Slice 6: Payments & Receipts (COMPLETED)**

Secure the booking process with actual payments and provide proof of purchase.

- **🎯 Goal:** Users pay for their tickets to confirm the booking and receive a digital receipt. Refunds are processed automatically upon cancellation.
- **⚙️ How-to:**
    1.  **Database:**
        - Create a `Payments` table (`id`, `booking_id`, `amount`, `payment_method`, `transaction_id`, `payment_date`, `status`).
        - Create a `Refunds` table (`id`, `payment_id`, `amount`, `refund_date`, `reason`).
    2.  **Back-end:**
        - Integrate a mock payment service (or a sandbox gateway like Stripe/PayPal).
        - Update `POST /api/bookings` to initially set status to 'PENDING_PAYMENT'.
        - Create `POST /api/payments/process`:
            - Validates payment details.
            - Updates `Bookings` status to 'CONFIRMED'.
            - Records the transaction in `Payments`.
        - Update the Cancellation logic:
            - If a booking is cancelled, trigger a refund logic to calculate the refund amount (e.g., 100% if >24h, 50% if <24h).
            - Record in `Refunds` table.
        - Create `GET /api/bookings/:id/receipt` to generate a PDF or JSON receipt.
    3.  **Front-end:**
        - Add a Payment step after the passenger details form.
        - Display a "Download Receipt" button on the Booking Confirmation and History pages.
        - Show refund status on cancelled bookings.

- **🔄 User Flow:**
    1.  **Booking:** After selecting a train and adding passengers, the user proceeds to the payment section.
    2.  **Payment:** The user enters payment details (Method, Card Number, Expiry, CVV) and clicks "Pay & Confirm".
    3.  **Confirmation:** Upon successful payment, the booking is confirmed, and the user is redirected to the home page.
    4.  **Receipt:** The user can view their bookings in "My Bookings". For confirmed bookings, a "Download Receipt" button is available, which generates a PDF receipt.
    5.  **Cancellation & Refund:** If the user cancels a confirmed booking, a refund is automatically processed (mocked), and the booking status changes to "CANCELLED".

---

#### **Slice 7: Interactive Journey Tracking (COMPLETED)**

Enhance the travel experience with real-time context.

- **🎯 Goal:** Users can follow the path of their journey, view details about intermediate stations, and stay informed with real-time updates.
- **⚙️ How-to:**
    1.  **Database:**
        - Update `Stations` to include `latitude`, `longitude`, `description`, and `photos` (fun facts, gallery URLs).
        - Create `RouteStops` table to define the sequence of stations and travel times between them for a specific schedule.
    2.  **Back-end:**
        - Create `GET /api/schedules/:id/route`: Returns the full list of stops with estimated arrival times.
        - Create a simulation service (or WebSocket) that estimates the train's current position based on departure time and current time.
        - Create `GET /api/stations/:id/info`: Returns rich content (info and photos) about the station/city.
        - Implement a notification system for journey events (station reached, delays, etc.).
    3.  **Front-end:**
        - **Map View:** Integrate a map library (like Leaflet or Google Maps) to allow users to follow the path of the journey and see the train's current location.
        - **Station Details:** Allow users to click on intermediate stations to view info and photos.
        - **Timeline:** A visual timeline showing passed stations (dimmed) and upcoming stations (highlighted), including indicators for how much time has passed and how much is left.
        - **Notifications:** Use browser notifications or in-app toasts to alert the user when a station is reached, the destination is reached, or if something happens during the journey (e.g., delays).

- **🧪 Testing Strategy (Dev Phase):**
    - **Time Warp Mode:** Implement a "Dev Mode" toggle that speeds up the journey simulation (e.g., 1 real second = 1 journey minute) so you don't have to wait hours to test the full flow.
    - **Manual Event Triggers:** Create a hidden developer panel or API endpoints to manually trigger events like "Train Delayed", "Arrived at Station X", or "Emergency Stop" to verify notifications and UI updates immediately.
    - **Mock Location Provider:** Instead of relying on complex GPS logic, create a mock service that emits a pre-defined list of coordinates along the route to verify the map marker moves correctly.

---

#### **Slice 8: The "Smart Companion" (Market Differentiator)**

Make the app more than just a booking tool; make it a travel partner.

- **🎯 Goal:** Provide AI-driven destination guides and an optional "Train Mode" for social interaction and onboard services.
- **⚙️ How-to:**
    1.  **Database:**
        - `DestinationGuides` (tips, weather_api_ref, events).
        - `OnboardServices` (menu items for dining car, wifi credentials).
    2.  **Back-end:**
        - **Smart Itinerary:** Endpoint that aggregates weather forecasts and top 3 "Must Visit" places for the destination city upon booking.
        - **Carbon Tracker:** Calculate and store the CO2 saved by this trip vs. driving/flying.
        - **Digital Seat Map (Social):** (Optional) Allow users to opt-in to share their profile (e.g., "Open to chat", "Business networking") with neighbors.
    3.  **Front-end:**
        - **"Trip Dashboard":** A special view active only during the journey.
        - **Eco-Badge:** Display "You saved X kg of CO2!" on the receipt and profile.
        - **Smart Suggestions:** "It's raining in London today, here are 3 indoor museums near the station."

---

#### **Slice 9: Loyalty & Gamification**

Turn occasional travelers into frequent flyers.

- **🎯 Goal:** Users earn points for trips, which can be redeemed for discounts or free upgrades.
- **⚙️ How-to:**
    1.  **Database:**
        - `LoyaltyPoints` table (`user_id`, `points_balance`, `tier_level`).
        - `Rewards` table (`id`, `cost_in_points`, `description`).
    2.  **Back-end:**
        - Update `POST /api/bookings` to award points based on ticket price (e.g., 1 point per $1).
        - Create `POST /api/rewards/redeem` to exchange points for voucher codes.
    3.  **Front-end:**
        - **Progress Bar:** "You are 500 points away from Gold Tier!"
        - **Leaderboards:** (Optional) "You traveled more than 80% of users this month."
        - **Referral System:** "Invite a friend and you both get 10% off."
