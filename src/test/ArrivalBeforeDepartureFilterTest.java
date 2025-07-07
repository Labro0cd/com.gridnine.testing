package test;

import filter.ArrivalBeforeDepartureFilter;
import model.Flight;
import model.Segment;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArrivalBeforeDepartureFilterTest {
    @Test
    void whenAllSegmentsValid_thenFlightNotFiltered() {
        // Подготовка тестовых данных
        Segment validSegment1 = new Segment(
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2)
        );
        Segment validSegment2 = new Segment(
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(5)
        );
        Flight validFlight = new Flight(List.of(validSegment1, validSegment2));

        // Тестирование
        List<Flight> result = new ArrivalBeforeDepartureFilter().filter(List.of(validFlight));

        // Проверка
        assertEquals(1, result.size());
        assertTrue(result.contains(validFlight));
    }

    @Test
    void whenAnySegmentInvalid_thenFlightFiltered() {
        // Подготовка тестовых данных (один сегмент с arrival < departure)
        Segment validSegment = new Segment(
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1)
        );
        Segment invalidSegment = new Segment(
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(1) // Прилёт раньше вылета!
        );
        Flight invalidFlight = new Flight(List.of(validSegment, invalidSegment));

        // Тестирование
        List<Flight> result = new ArrivalBeforeDepartureFilter().filter(List.of(invalidFlight));

        // Проверка
        assertTrue(result.isEmpty());
    }

    @Test
    void whenEmptyFlightList_thenReturnEmptyList() {
        // Тестирование с пустым списком
        List<Flight> result = new ArrivalBeforeDepartureFilter().filter(List.of());

        // Проверка
        assertTrue(result.isEmpty());
    }

    @Test
    void whenFlightWithSingleValidSegment_thenNotFiltered() {
        // Подготовка тестовых данных
        Segment validSegment = new Segment(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30)
        );
        Flight flight = new Flight(List.of(validSegment));

        // Тестирование
        List<Flight> result = new ArrivalBeforeDepartureFilter().filter(List.of(flight));

        // Проверка
        assertEquals(1, result.size());
    }

    @Test
    void whenFlightWithSingleInvalidSegment_thenFiltered() {
        // Подготовка тестовых данных
        Segment invalidSegment = new Segment(
                LocalDateTime.now(),
                LocalDateTime.now().minusHours(1) // Прилёт раньше вылета
        );
        Flight flight = new Flight(List.of(invalidSegment));

        // Тестирование
        List<Flight> result = new ArrivalBeforeDepartureFilter().filter(List.of(flight));

        // Проверка
        assertTrue(result.isEmpty());
    }

}