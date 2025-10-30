
# USER FEATURES AND DEV GUIDE

1.  **Viewing Trains & Schedules**
2.  **Account Registration & Login**
3.  **Booking a Ticket**
4.  **Viewing Booking History**
5.  **Cancelling a Booking**

Here’s a complete development guide for building these features using the vertical slice method you liked.

***

### Development Guide: User Features (Vertical Slice Method)

This guide breaks down each core feature into a self-contained "slice." For each slice, you'll work on the database, the back-end logic, and the front-end interface, ensuring you have a working piece of the application at the end of each step.

#### **Slice 0: The Foundation (Setup)**

Before writing any features, you need to set up your project. This isn't a user feature, but it's a critical first step.

* **Goal:** Get a basic, empty project running.
* **Tasks:**
    1.  **Choose Your Tech Stack:** JAVA / SPRING / ORACLE DB (BACK), REACTJS (FRONT) ? 
    2.  **Project Setup:** Create your project folder, initialize a Git repository, and install necessary libraries.
    3.  **Database Connection:** Configure your application to connect to your database.
    4.  **Basic Server:** Create a simple web server that can respond to a request (e.g., returns "Hello, World!").

---

#### **Slice 1: View Train Schedules**

This is the most fundamental feature. Users need to be able to see what trains are available.

* **🎯 Goal:** A visitor can search for trains between two stations on a specific date and see a list of results.
* **⚙️ How-to:**
    1.  **Database:**
        * Design and create your initial tables: `Stations` (id, name), `Trains` (id, name, total_seats), and `Schedules` (id, train_id, departure_station_id, arrival_station_id, departure_time, arrival_time, price, available_seats).
        * Manually add some sample data into these tables so you have something to display.
    2.  **Back-end:**
        * Create an API endpoint, for example, `GET /api/schedules`.
        * This endpoint should accept query parameters like `from`, `to`, and `date`.
        * Write the logic to query the `Schedules` table based on these parameters and return the matching trains as JSON data.
    3.  **Front-end:**
        * Build the main search page with input fields for "Departure Station," "Arrival Station," and "Date."
        * When the user clicks "Search," make an API call to your back-end endpoint.
        * Display the results neatly in a list, showing train name, times, duration, and price. 

---

#### **Slice 2: User Account Management**

Users need an account to manage their bookings.

* **🎯 Goal:** A new user can create an account. An existing user can log in and log out.
* **⚙️ How-to:**
    1.  **Database:**
        * Create a `Users` table with columns like `id`, `name`, `email`, and `password_hash`. **Crucially, never store plain-text passwords.** Always store a hashed and salted version.
    2.  **Back-end:**
        * Create a `POST /api/register` endpoint to handle new user sign-ups. It should validate the data, hash the password, and save the new user to the database.
        * Create a `POST /api/login` endpoint to authenticate users. It compares the provided password with the stored hash. If successful, it creates a session or a token (like a JWT) to keep the user logged in.
        * Create a `POST /api/logout` endpoint to invalidate the session/token.
    3.  **Front-end:**
        * Build the UI for the registration and login forms.
        * Connect these forms to your new API endpoints.
        * Update the UI to show whether a user is logged in or not (e.g., show "Login/Register" or "My Account/Logout").

---

#### **Slice 3: Ticket Booking**

This is the core transaction of your system.

* **🎯 Goal:** A logged-in user can select a train from the search results and book a ticket for one or more passengers.
* **⚙️ How-to:**
    1.  **Database:**
        * Create a `Bookings` table (e.g., `id`, `user_id`, `schedule_id`, `booking_date`, `status`).
        * Create a `Passengers` table (e.g., `id`, `booking_id`, `name`, `age`).
    2.  **Back-end:**
        * Create a `POST /api/bookings` endpoint. This is your most important piece of logic!
        * It must be a **secure** endpoint, meaning only logged-in users can access it.
        * The logic should:
            1.  Check if there are enough `available_seats` for the requested number of passengers.
            2.  If yes, create a new record in the `Bookings` table.
            3.  Create records for each passenger in the `Passengers` table.
            4.  **Decrement** the `available_seats` in the `Schedules` table for that train. This must be an atomic transaction to prevent overbooking.
            5.  Return a success message with the booking details.
    3.  **Front-end:**
        * When a user clicks "Book" on a train, take them to a page where they can enter passenger details.
        * On submitting this form, call the booking API.
        * After a successful booking, show a confirmation page with a unique booking ID.

---

#### **Slice 4: View Booking History**

Users need a way to see their tickets.

* **🎯 Goal:** A logged-in user can view a list of their past and upcoming bookings.
* **⚙️ How-to:**
    1.  **Database:** No new tables needed.
    2.  **Back-end:**
        * Create a secure endpoint like `GET /api/my-bookings`.
        * This endpoint should find the current user's ID from their session/token and retrieve all associated records from the `Bookings` table.
    3.  **Front-end:**
        * Create a "My Bookings" page in the user's profile area.
        * Call the `my-bookings` API and display the results in a clear list, separating upcoming trips from past ones.

---

#### **Slice 5: Cancel a Booking**

This feature completes the booking lifecycle.

* **🎯 Goal:** A user can cancel an upcoming booking from their history page.
* **⚙️ How-to:**
    1.  **Database:** You can simply add a `status` column to your `Bookings` table if you haven't already (e.g., with values like 'CONFIRMED', 'CANCELLED').
    2.  **Back-end:**
        * Create a secure endpoint like `POST /api/bookings/:id/cancel`.
        * The logic should:
            1.  Verify that the booking belongs to the logged-in user.
            2.  Change the booking's `status` to 'CANCELLED'.
            3.  **Increment** the `available_seats` on the corresponding `Schedules` record. This is the reverse of the booking process.
    3.  **Front-end:**
        * On the "My Bookings" page, add a "Cancel" button next to each upcoming trip.
        * When clicked, it should call the cancellation API and update the UI to show the trip as cancelled.



## Authentication

- All endpoints under `/api/bookings` require a valid JWT.
- Send header: Authorization: Bearer <token>
- Content-Type: application/json for requests with a body.

## POST /api/bookings

- Purpose: Create a booking for a schedule with a list of passengers. This is atomic and prevents overbooking.
- Auth: Required
- Request body:
  - scheduleId: number (required)
  - passengers: array of objects (required, non-empty)
    - name: string (not blank)
    - age: number (>= 0)
- Example request:
  {
    "scheduleId": 123,
    "passengers": [
      { "name": "Alice", "age": 30 },
      { "name": "Bob", "age": 28 }
    ]
  }
- Response: 200 OK
  - bookingId: number
  - referenceCode: string (random, 24 hex chars, unique)
  - scheduleId: number
  - bookingDate: string (ISO timestamp)
  - status: string ("CONFIRMED")
  - passengers: array of { name, age }
  - totalPrice: number (decimal as string in JSON)
- Errors:
  - 400 Bad Request
    - Validation failed (missing scheduleId, empty passengers, blank name, negative age)
    - Invalid scheduleId (not found)
  - 409 Conflict
    - Not enough available seats
  - 401 Unauthorized
    - Missing/invalid JWT
  - 500 Internal Server Error
    - Unexpected server error

Notes:
- Seats are decremented atomically in the database. If two users race, only one succeeds.

Request/Response example

GET /api/bookings?page=0&size=5
200 OK
JSON body is a Spring Data Page structure:
content: [ { bookingId, referenceCode, scheduleId, bookingDate, status, passengersCount, totalPrice }, ... ]
pageable: { ... }
totalElements, totalPages, size, number, first, last, numberOfElements, sort, empty
Notes and defaults


## GET /api/bookings/{referenceCode}

- Purpose: Fetch a single booking by its reference code (only your own).
- Auth: Required
- Path params:
  - referenceCode: string (24 hex)
- Response: 200 OK (BookingResponse)
  - bookingId: number
  - referenceCode: string
  - scheduleId: number
  - bookingDate: string (ISO timestamp)
  - status: string ("CONFIRMED" | "CANCELLED")
  - passengers: array of { name, age }
  - totalPrice: number (decimal as string in JSON)
- Errors:
  - 400 Bad Request
    - Booking not found (invalid or not owned by current user)
  - 401 Unauthorized
    - Missing/invalid JWT
  - 500 Internal Server Error

Note:
- We intentionally return “Booking not found” as 400 to avoid leaking info. If you’d prefer 404, we can adjust the handler.

## DELETE /api/bookings/{referenceCode}

- Purpose: Cancel a booking (only your own) and restore seats.
- Auth: Required
- Path params:
  - referenceCode: string (24 hex)
- Response: 204 No Content
  - Idempotent: If already cancelled, still returns 204.
- Errors:
  - 400 Bad Request
    - Booking not found (invalid or not owned by current user)
  - 401 Unauthorized
    - Missing/invalid JWT
  - 500 Internal Server Error

## Validation summary

- CreateBookingRequest:
  - scheduleId: @NotNull
  - passengers: @NotEmpty, @Valid
- PassengerDTO:
  - name: @NotBlank
  - age: @Min(0)
- Validation errors are returned as 400 with a structure:
  - {
      "error": "Validation failed",
      "details": {
        "passengers[0].name": "must not be blank",
        "scheduleId": "must not be null"
      }
    }
