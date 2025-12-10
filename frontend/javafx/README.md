# TrainReserve JavaFX Frontend

This JavaFX app consumes the Spring Boot backend API to search train schedules and list stations.

## Features
- Load stations from `/api/stations`
- Search schedules with `/api/schedules?from=..&to=..&date=YYYY-MM-DD`
- Simple, responsive table UI

## Run
- From this folder:

```pwsh
mvn clean package
mvn javafx:run
```

If your backend runs on a different host/port, set the API base URL:

```pwsh
mvn javafx:run -Dapi.baseUrl=http://localhost:8080/api
```

Alternatively set an environment variable before launching:

```pwsh
$env:API_BASE_URL = "http://localhost:8080/api"
mvn javafx:run
```

## Project structure
- `me.ensah.model` – DTOs (`Station`, `Schedule`)
- `me.ensah.net.ApiClient` – HttpClient + Jackson
- `me.ensah.services` – `StationService`, `ScheduleService`
- `me.ensah.ui.MainController` – UI logic
- `resources/fxml/MainView.fxml` – UI layout
- `resources/styles/main.css` – styles

## Notes
- Requires Java 11+ and JavaFX 25 (managed by Maven).
- Uses Jackson JSR-310 module to handle `LocalDateTime` fields.