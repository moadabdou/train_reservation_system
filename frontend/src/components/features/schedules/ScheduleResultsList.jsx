import { Card, CardContent, CardHeader, CardTitle, CardDescription} from 'components/ui/card';
import { Button } from "components/ui/button";
import { Train, Clock, MapPin, Users, DollarSign , MapPinOff} from "lucide-react";
import { format } from "date-fns";

export function ScheduleResultsList({ schedules = [], loading = false, error = null, onRetry }) {
  if (loading) {
    return (
      <div className="max-w-2xl mx-auto mt-6">
        <Card>
          <CardContent className="p-6 text-center text-muted-foreground">Loading schedules…</CardContent>
        </Card>
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-2xl mx-auto mt-6 space-y-3">
        <div className="p-3 rounded-md bg-destructive/10 text-destructive text-sm">{String(error)}</div>
        {onRetry && (
          <Button variant="outline" onClick={onRetry}>Try again</Button>
        )}
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto mt-6 space-y-4">
      <h2 className="text-2xl font-semibold">Available Train Services</h2>
      {!schedules.length && (
        <div className="pt-2">
          <Card className="m-0 border-dashed">
            <CardHeader className="flex items-center gap-3 px-4 py-3">
              <MapPinOff className="h-5 w-5 text-muted-foreground" />
              <div>
                <CardTitle className="text-base">No scheduled services available</CardTitle>
                <CardDescription>for the selected itinerary</CardDescription>
              </div>
            </CardHeader>
          </Card>
        </div>
      )}
      {schedules.map((s) => {
        const depart = new Date(s.departureTime);
        const arrive = new Date(s.arrivalTime);
        const minutes = Math.round((arrive - depart) / (1000 * 60));
        const hours = Math.floor(minutes / 60);
        const mins = minutes % 60;
        const durationLabel = hours ? `${hours}h ${mins}m` : `${mins}m`;

        return (
          <Card key={s.id} className="hover:shadow-lg transition-shadow">
            <CardContent className="pt-6">
              <div className="flex flex-col space-y-4">
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-2">
                    <Train className="h-5 w-5 text-primary" />
                    <h3 className="text-lg font-semibold">{s.trainName}</h3>
                  </div>
                  <div className="flex items-center space-x-1 text-lg font-bold text-primary">
                    <DollarSign className="h-5 w-5" />
                    <span>MAD {s.price}</span>
                  </div>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <div className="flex items-start space-x-2">
                    <MapPin className="h-5 w-5 text-muted-foreground mt-0.5" />
                    <div>
                      <p className="text-sm text-muted-foreground">Departure</p>
                      <p className="font-medium">{s.departureStationName}</p>
                      <p className="text-sm text-muted-foreground">{format(depart, "HH:mm")}</p>
                    </div>
                  </div>
                  <div className="flex items-start space-x-2">
                    <MapPin className="h-5 w-5 text-muted-foreground mt-0.5" />
                    <div>
                      <p className="text-sm text-muted-foreground">Destination</p>
                      <p className="font-medium">{s.arrivalStationName}</p>
                      <p className="text-sm text-muted-foreground">{format(arrive, "HH:mm")}</p>
                    </div>
                  </div>
                </div>

                <div className="flex items-center justify-between pt-2 border-t">
                  <div className="flex items-center space-x-1 text-sm text-muted-foreground">
                    <Clock className="h-4 w-4" />
                    <span>Duration: {durationLabel}</span>
                  </div>
                  <div className="flex items-center space-x-1 text-sm">
                    <Users className="h-4 w-4 text-muted-foreground" />
                    <span className={s.availableSeats > 50 ? "text-green-600" : "text-orange-600"}>
                      Seats remaining: {s.availableSeats}
                    </span>
                  </div>
                </div>

                <Button className="w-full mt-2">Proceed to booking</Button>
              </div>
            </CardContent>
          </Card>
        );
      })}
    </div>
  );
}
