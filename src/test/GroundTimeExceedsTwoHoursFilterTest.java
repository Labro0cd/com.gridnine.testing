package test;

import filter.GroundTimeExceedsTwoHoursFilter;
import model.Flight;
import model.Segment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroundTimeExceedsTwoHoursFilterTest {
    private GroundTimeExceedsTwoHoursFilter filter;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        filter = new GroundTimeExceedsTwoHoursFilter();
        now = LocalDateTime.now();
    }

    @Test
    void whenSingleSegment_thenNotFiltered() {
        // Перелёт с одним сегментом
        Flight flight = new Flight(List.of(
                new Segment(now, now.plusHours(2))
        ));

        List<Flight> result = filter.filter(List.of(flight));

        assertEquals(1, result.size());
    }

    @Test
    void whenGroundTimeLessThanTwoHours_thenNotFiltered() {
        // Общее время на земле = 1 час (30 мин + 30 мин)
        Flight flight = new Flight(List.of(
                new Segment(now, now.plusHours(1)),           // сегмент 1
                new Segment(now.plusHours(1).plusMinutes(30), // 30 мин на земле
                        now.plusHours(2)),                            // сегмент 2
                new Segment(now.plusHours(3),                 // 30 мин на земле
                        now.plusHours(4))                             // сегмент 3
        ));

        List<Flight> result = filter.filter(List.of(flight));

        assertEquals(1, result.size());
    }

    @Test
    void whenGroundTimeExactlyTwoHours_thenNotFiltered() {
        // Общее время на земле ровно 2 часа
        Flight flight = new Flight(List.of(
                new Segment(now, now.plusHours(1)),     // сегмент 1
                new Segment(now.plusHours(3),           // 2 часа на земле
                        now.plusHours(5))                       // сегмент 2
        ));

        List<Flight> result = filter.filter(List.of(flight));

        assertEquals(1, result.size());
    }

    @Test
    void whenGroundTimeExceedsTwoHours_thenFiltered() {
        // Общее время на земле = 3 часа (1 + 2)
        Flight flight = new Flight(List.of(
                new Segment(now, now.plusHours(1)),     // сегмент 1
                new Segment(now.plusHours(2),           // 1 час на земле
                        now.plusHours(3)),                      // сегмент 2
                new Segment(now.plusHours(5),           // 2 часа на земле
                        now.plusHours(6))                      // сегмент 3
        ));

        List<Flight> result = filter.filter(List.of(flight));

        assertTrue(result.isEmpty());
    }

    @Test
    void whenMultipleFlights_thenFilterCorrectly() {
        // Тест с несколькими перелётами
        Flight validFlight = new Flight(List.of(    // общее время на земле 1 час
                new Segment(now, now.plusHours(1)),
                new Segment(now.plusHours(2), now.plusHours(3))
        ));

        Flight invalidFlight = new Flight(List.of(  // общее время на земле 3 часа
                new Segment(now.plusHours(4), now.plusHours(5)),
                new Segment(now.plusHours(8), now.plusHours(9))
        ));

        List<Flight> result = filter.filter(List.of(validFlight, invalidFlight));

        assertEquals(1, result.size());
        assertTrue(result.contains(validFlight));
    }

    @Test
    void whenEmptyFlightList_thenReturnEmptyList() {
        List<Flight> result = filter.filter(List.of());

        assertTrue(result.isEmpty());
    }

    @Test
    void whenConsecutiveSegmentsWithNoGroundTime_thenNotFiltered() {
        // Сегменты без времени на земле (прилёт = вылет следующего)
        Flight flight = new Flight(List.of(
                new Segment(now, now.plusHours(1)),
                new Segment(now.plusHours(1), now.plusHours(2))
        ));

        List<Flight> result = filter.filter(List.of(flight));

        assertEquals(1, result.size());
    }

    @Test
    void whenArrivalAfterNextDeparture_thenNegativeGroundTime() {
        // Некорректные данные (прилёт после следующего вылета)
        Flight flight = new Flight(List.of(
                new Segment(now, now.plusHours(3)),
                new Segment(now.plusHours(1), now.plusHours(4)) // вылет до прилёта предыдущего
        ));

        List<Flight> result = filter.filter(List.of(flight));

        // Фильтр должен пропустить такие перелёты (их должна отсеивать другая проверка)
        assertEquals(1, result.size());
    }
}