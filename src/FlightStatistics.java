
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class FlightStatistics {
    public static void main(String[] args) {

        List<Long> durationsOfFlightsList = getDurationsList();

        System.out.println( getAvarageFlightDuration (durationsOfFlightsList));
        System.out.println( getPercentileDuration(durationsOfFlightsList, 90));
    }


    private static List<Long> getDurationsList () {

        List<Long> durationsList = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();

        try {
            JSONObject ticketObject = (JSONObject) jsonParser.parse(new FileReader("tickets.json"));
            JSONArray ticketsList = (JSONArray) ticketObject.get("tickets");

            for (Object ticket:ticketsList) {
                durationsList.add(getDurationOfFlight((JSONObject) ticket));
            }

        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return durationsList;
    }


    private static long getDurationOfFlight (JSONObject ticket) {

        long durationMinutes = 0;

        String departure = (String) ticket.get("departure_time");
        String arrival = (String) ticket.get("arrival_time");

        DateTimeFormatter df = DateTimeFormatter.ofPattern("H:mm");

        LocalTime timeDeparture = LocalTime.parse(departure, df);
        LocalTime timeArrival = LocalTime.parse(arrival, df);

        durationMinutes = Duration.between(timeDeparture, timeArrival).toMinutes();

        return durationMinutes;

    }


    private static long getAvarageFlightDuration (List<Long> durationsOfFlightsList) {

        long avarageDuration = 0;

        for(Long duration:durationsOfFlightsList) {
            avarageDuration += duration;
        }

        avarageDuration = avarageDuration / durationsOfFlightsList.size();

        return avarageDuration;
    }



    private static long getPercentileDuration (List<Long> durationsOfFlightsList, int percentile) {

        long duration = 0;

        List<Long> listOfDurationsSorted = durationsOfFlightsList.stream().sorted().collect(Collectors.toList());

        double k = (double) percentile / 100 * listOfDurationsSorted.size();
        int i = (int) Math.ceil(k) - 1;

        duration = listOfDurationsSorted.get(i);

        return duration;
    }

}
