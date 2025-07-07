import filter.ArrivalBeforeDepartureFilter;
import filter.DepartureBeforeNowFilter;
import filter.FlightFilter;
import filter.GroundTimeExceedsTwoHoursFilter;
import model.Flight;
import util.FlightBuilder;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Flight> flights = FlightBuilder.createFlights();

        // Создаем фильтры
        FlightFilter departureBeforeNowFilter = new DepartureBeforeNowFilter();
        FlightFilter arrivalBeforeDepartureFilter = new ArrivalBeforeDepartureFilter();
        FlightFilter groundTimeFilter = new GroundTimeExceedsTwoHoursFilter();

        // Применяем фильтры
        List<Flight> filtered1 = departureBeforeNowFilter.filter(flights);
        List<Flight> filtered2 = arrivalBeforeDepartureFilter.filter(flights);
        List<Flight> filtered3 = groundTimeFilter.filter(flights);

        // Выводим результаты
        System.out.println("Исходный список перелётов:");
        printFlights(flights);

        System.out.println("\nПерелёты после исключения вылетов до текущего момента:");
        printFlights(filtered1);

        System.out.println("\nПерелёты после исключения сегментов с датой прилёта раньше даты вылета:");
        printFlights(filtered2);

        System.out.println("\nПерелёты после исключения перелётов с общим временем на земле > 2 часов:");
        printFlights(filtered3);
    }

    private static void printFlights(List<Flight> flights) {
        flights.forEach(flight -> {
            System.out.println(flight.getSegments());
        });
    }
}