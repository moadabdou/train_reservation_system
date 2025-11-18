# Database Seeder Documentation

## Overview

The `DataInitializer` class is a comprehensive database seeder for the TrainLink application. It automatically populates the database with realistic test data when the application starts.

## What Gets Seeded

### 1. **Users** (6 accounts)
- **1 Admin Account**
  - Email: `admin@trainlink.ma`
  - Password: `admin123`
  - Role: Admin

- **5 Client Accounts**
  - `mohamed.alami@gmail.com`
  - `fatima.zahra@gmail.com`
  - `ahmed.bennis@gmail.com`
  - `salma.idrissi@gmail.com`
  - `youssef.tazi@gmail.com`
  - Password for all: `password123`
  - Role: Client

### 2. **Stations** (17 locations)
Major Moroccan cities and stations:
- Casablanca Voyageurs
- Casablanca Port
- Rabat Agdal
- Rabat Ville
- Marrakech
- Tanger Ville
- Fès Ville
- Meknès
- Oujda
- Kenitra
- El Jadida
- Safi
- Mohammedia
- Settat
- Khouribga
- Benguerir
- Sidi Kacem

### 3. **Trains** (13 trains)
Different types of trains with varying capacities:

**High-Speed (TGV) - 3 trains**
- Al Boraq TGV-001 (250 seats)
- Al Boraq TGV-002 (250 seats)
- Al Boraq TGV-003 (250 seats)

**Express - 4 trains**
- Atlas Express (350 seats)
- Chems Express (350 seats)
- Rif Express (300 seats)
- Sahara Express (300 seats)

**Regional - 3 trains**
- TNR Régional 101 (400 seats)
- TNR Régional 102 (400 seats)
- TNR Régional 103 (450 seats)

**Inter-city - 3 trains**
- Intercity Rabat-Casa (320 seats)
- Intercity Fès-Meknès (280 seats)
- Intercity Tanger-Nord (300 seats)

### 4. **Schedules** (~300-400 schedules)
- **15 popular routes** across Morocco
- **7 days** of scheduling (from current date)
- **3-5 trips per route per day** at different times
- Schedules spread throughout the day (6 AM - 10 PM)
- Realistic journey times and pricing
- Variable seat availability (70-100% of train capacity)
- Price variations (±10% from base price)

## Popular Routes & Base Prices

| Route | Journey Time | Base Price |
|-------|-------------|------------|
| Casablanca → Rabat | 1h | 50 MAD |
| Casablanca → Marrakech | 3.5h | 120 MAD |
| Casablanca → Tanger | 5h | 180 MAD |
| Rabat → Tanger | 4h | 150 MAD |
| Rabat → Fès | 3.5h | 130 MAD |
| Casablanca → Fès | 4h | 140 MAD |
| Marrakech → Casablanca | 3.5h | 120 MAD |
| Tanger → Casablanca | 5h | 180 MAD |
| Fès → Meknès | 1h | 40 MAD |
| Casablanca → Kenitra | 1.5h | 60 MAD |
| Rabat → Casablanca Port | 1h | 45 MAD |
| Kenitra → Tanger | 3h | 110 MAD |
| Casablanca → El Jadida | 1.5h | 55 MAD |
| Rabat → Meknès | 2h | 80 MAD |
| Settat → Marrakech | 2h | 90 MAD |

## Features

### Smart Seeding
- ✅ Only runs if database is empty (checks `stationRepository.count()`)
- ✅ Prevents duplicate data on application restart
- ✅ Seeds data in correct order (Users → Stations → Trains → Schedules)

### Realistic Data
- 🎲 Randomized available seats (70-100% capacity)
- 💰 Price variations (±10% from base price)
- ⏰ Time slots distributed throughout the day
- 📅 Multiple departures per day for popular routes

### Detailed Console Output
```
============================================================
🌱 Starting database seeding process...
============================================================

👥 Seeding users...
   ✓ Created 6 users (1 admin, 5 clients)

🚉 Seeding stations...
   ✓ Created 17 stations across Morocco

🚄 Seeding trains...
   ✓ Created 13 trains
      - TGV: 3 trains (250 seats each)
      - Express: 4 trains (300-350 seats)
      - Regional: 3 trains (400-450 seats)
      - Inter-city: 3 trains (280-320 seats)

📅 Seeding schedules...
   ✓ Created 315 schedules
      - 15 unique routes
      - 7 days of scheduling
      - 3-5 trips per route per day

============================================================
✅ Database seeding completed successfully!
============================================================

📊 Seeding Summary:
   • Users: 6
   • Stations: 17
   • Trains: 13
   • Schedules: 315

💡 Test Credentials:
   Admin:  admin@trainlink.ma / admin123
   Client: mohamed.alami@gmail.com / password123
   (or any other seeded user with password123)
```

## How It Works

### 1. Dependency Injection
```java
public DataInitializer(
    StationRepository stationRepository, 
    TrainRepository trainRepository, 
    ScheduleRepository scheduleRepository,
    UserRepository userRepository,
    PasswordEncoder passwordEncoder)
```

### 2. CommandLineRunner Interface
Implements Spring Boot's `CommandLineRunner` to run after application context is loaded.

### 3. Conditional Execution
```java
if (stationRepository.count() == 0) {
    // Seed data
}
```

### 4. Methodical Seeding
- `seedUsers()` - Creates admin and test users
- `seedStations()` - Creates Moroccan train stations
- `seedTrains()` - Creates different train types
- `seedSchedules()` - Generates schedules for 7 days

## Customization

### Add More Routes
Edit the `routes` list in `seedSchedules()`:
```java
new RouteInfo("Departure Station", "Arrival Station", journeyHours, basePrice)
```

### Change Date Range
Modify the loop in `seedSchedules()`:
```java
for (int day = 0; day < 7; day++) { // Change 7 to desired days
```

### Adjust Schedules Per Day
Change the random range:
```java
int schedulesPerDay = 3 + random.nextInt(3); // Currently 3-5, adjust as needed
```

### Add More Users
Add to the users list in `seedUsers()`:
```java
new User(null, "Name", "email@example.com", 
    passwordEncoder.encode("password"), "client")
```

## Security

- ✅ Passwords are encrypted using BCrypt via `PasswordEncoder`
- ✅ Never stores plain text passwords
- ✅ Test credentials clearly documented
- ⚠️ For production: Change default credentials and use environment variables

## Testing the Seeder

1. **Clean Database**
   ```bash
   # Drop and recreate database
   mysql -u root -p
   DROP DATABASE trainlink;
   CREATE DATABASE trainlink;
   ```

2. **Run Application**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

3. **Verify Data**
   - Check console output for seeding summary
   - Login with test credentials
   - Browse available schedules

## Troubleshooting

### Seeder Not Running
- Ensure `@Component` annotation is present
- Check that `CommandLineRunner` interface is implemented
- Verify database connection is working

### Duplicate Data
- Seeder checks if stations exist before running
- If you see duplicates, check the condition logic

### Missing Data
- Check console logs for errors
- Verify all repositories are properly injected
- Ensure database schema is up to date

## Benefits

✅ **Instant Development Setup** - No manual data entry needed
✅ **Consistent Test Data** - Same data across all environments
✅ **Realistic Scenarios** - Actual Moroccan routes and cities
✅ **Easy Testing** - Multiple test accounts ready to use
✅ **Production Ready** - Can be disabled via configuration
✅ **Well Documented** - Clear console output and code comments

## Future Enhancements

- [ ] Add booking history for test users
- [ ] Include sample passenger records
- [ ] Generate past schedules for reporting tests
- [ ] Add configuration to enable/disable via application.properties
- [ ] Support for seeding from JSON/CSV files
- [ ] Add seasonal pricing variations
- [ ] Include holiday schedules
