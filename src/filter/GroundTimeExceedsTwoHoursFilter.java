package filter;

import model.Flight;
import model.Segment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class GroundTimeExceedsTwoHoursFilter implements FlightFilter {
    @Override
    public List<Flight> filter(List<Flight> flights) {
        return flights.stream()
                .filter(flight -> {
                    List<Segment> segments = flight.getSegments();
                    if (segments.size() <= 1) return true;

                    long totalGroundTime = 0;
                    for (int i = 0; i < segments.size() - 1; i++) {
                        LocalDateTime currentArrival = segments.get(i).getArrivalDate();
                        LocalDateTime nextDeparture = segments.get(i+1).getDepartureDate();
                        totalGroundTime += java.time.Duration.between(currentArrival, nextDeparture).toHours();
                    }
                    return totalGroundTime <= 2;
                })
                .collect(Collectors.toList());
    }
}
