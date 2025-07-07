package test;

import filter.DepartureBeforeNowFilter;
import model.Flight;
import model.Segment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DepartureBeforeNowFilterTest {
    private DepartureBeforeNowFilter filter;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        filter = new DepartureBeforeNowFilter();
        now = LocalDateTime.now();
    }

    @Test
    void whenAllSegmentsInFuture_thenFlightNotFiltered() {
        // Перелёт с сегментами в будущем
        Segment futureSegment1 = new Segment(
                now.plusHours(1),
                now.plusHours(3)
        );
        Segment futureSegment2 = new Segment(
                now.plusHours(4),
                now.plusHours(6)
        );
        Flight futureFlight = new Flight(List.of(futureSegment1, futureSegment2));

        List<Flight> result = filter.filter(List.of(futureFlight));

        assertEquals(1, result.size());
        assertTrue(result.contains(futureFlight));
    }

    @Test
    void whenAnySegmentInPast_thenFlightFiltered() {
        // Перелёт с одним сегментом в прошлом
        Segment pastSegment = new Segment(
                now.minusHours(2),
                now.minusHours(1)
        );
        Segment futureSegment = new Segment(
                now.plusHours(1),
                now.plusHours(3)
        );
        Flight mixedFlight = new Flight(List.of(pastSegment, futureSegment));

        List<Flight> result = filter.filter(List.of(mixedFlight));

        assertTrue(result.isEmpty());
    }

    @Test
    void whenAllSegmentsInPast_thenFlightFiltered() {
        // Перелёт только с сегментами в прошлом
        Segment pastSegment1 = new Segment(
                now.minusDays(1),
                now.minusDays(1).plusHours(2)
        );
        Segment pastSegment2 = new Segment(
                now.minusHours(3),
                now.minusHours(1)
        );
        Flight pastFlight = new Flight(List.of(pastSegment1, pastSegment2));

        List<Flight> result = filter.filter(List.of(pastFlight));

        assertTrue(result.isEmpty());
    }

    @Test
    void whenFlightDepartsNow_thenFiltered() {
        // Граничный случай: вылет прямо сейчас
        Segment nowSegment = new Segment(now, now.plusHours(2));
        Flight nowFlight = new Flight(List.of(nowSegment));

        List<Flight> result = filter.filter(List.of(nowFlight));

        assertTrue(result.isEmpty(), "Flight departing exactly now should be filtered");
    }

    @Test
    void whenEmptyFlightList_thenReturnEmptyList() {
        List<Flight> result = filter.filter(List.of());

        assertTrue(result.isEmpty());
    }

    @Test
    void whenFlightWithSingleFutureSegment_thenNotFiltered() {
        Segment futureSegment = new Segment(
                now.plusMinutes(1),
                now.plusHours(2)
        );
        Flight flight = new Flight(List.of(futureSegment));

        List<Flight> result = filter.filter(List.of(flight));

        assertEquals(1, result.size());
    }

    @Test
    void whenFlightWithSinglePastSegment_thenFiltered() {
        Segment pastSegment = new Segment(
                now.minusHours(5),
                now.minusHours(3)
        );
        Flight flight = new Flight(List.of(pastSegment));

        List<Flight> result = filter.filter(List.of(flight));

        assertTrue(result.isEmpty());
    }
}