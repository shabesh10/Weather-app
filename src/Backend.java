import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

// retrieve weather data from API - this backend logic will fetch the latest weather
// data from the external API and return it and the GUI will display it to the user 

// to use weather API, we need latitude and logitudes. we get that from Geolocation API

public class Backend {
    // fetch weather data for given location
    public static JSONObject getWeatherData(String locationName) {
        // get location data from the geolocation API
        JSONArray locationData = getLocationData(locationName);

        //extract latitude and longitude from the location data
        JSONObject location  = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        //build API URL with latitude and longitude parameters
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
        "latitude="+latitude+"&longitude="+longitude+"&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=auto";

        try {
            // call api and get response
            HttpURLConnection conn = fetchApiResponse(urlString);

            //check for response status
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: could not connect to API");
                return null;
            }

            //else store the JSON data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            
            //read and store the result
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());  
            }

            scanner.close();
            conn.disconnect();

            //parse the JSON string into JSON object for better accessibility of our data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            //retreive hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            //getting the current hour's data
            //so we need to get the index of the current data
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            //get weather code
            JSONArray weathercode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));

            //get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);
            
            //get windspeed
            JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windspeedData.get(index);

            //build the weatherdata on our own JSON object to use in the frontend

            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);
            

            return weatherData;       
        }
        catch(Exception e) {
            e.printStackTrace();
        }

         
        return null;
    } 

    public static JSONArray getLocationData(String locationName) {
        //replace any whitespcae in loacation name to +, because we have to match the API format
        locationName = locationName.replaceAll(" ", "+");

        //build API URL with location parameter
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + locationName + "&count=10&language=en&format=json";

        try {
            HttpURLConnection conn = fetchApiResponse(urlString);

            //check response status
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: could not connect to API");
                return null;
            }
            else {
                //store the API results
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                //read and store the result
                while (scanner.hasNext()) {
                    resultJson.append(scanner.nextLine());
                }

                //closing the scanner
                scanner.close();

                //disconnecting the connection
                conn.disconnect();

                //parse the JSON string into JSON object for better accessibility of our data
                JSONParser parser = new JSONParser();
                JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                //get the list of location data the API generated from the location name
                JSONArray locationData = (JSONArray) resultJsonObj.get("results");
                return locationData;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            //attempt to create connection
            URL url = new URL(urlString);
            HttpURLConnection conn  = (HttpURLConnection) url.openConnection();

            //set request method to get
            conn.setRequestMethod("GET");

            //connect to our API
            conn.connect();
            return conn;
            
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    private static int findIndexOfCurrentTime(JSONArray timeList) {
        String currentTime = getCurrentTime();

        //iterate the time list and see which one mactches our current time
        for (int i = 0; i < timeList.size(); i++) {
            String time = (String) timeList.get(i);
            if (time.equalsIgnoreCase(currentTime)) {
                //return the idx
                return i;
            }
        }
        return 0;
    }

    public static String getCurrentTime() {
        //get current date and time
        LocalDateTime currenDateTime = LocalDateTime.now();

        //format date to be 2023-09-02T00:00 (this is how the api will return)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        //format and print the current date and time
        String formattedDateTime = currenDateTime.format(formatter);

        return formattedDateTime;
    }

    private static String convertWeatherCode(long weathercode) {
        String weatherCondition = "";
        if (weathercode == 0L) {
            weatherCondition = "Clear";
        }
        else if (weathercode > 0L && weathercode <= 3L) {
            weatherCondition = "Cloudy";
        }
        else if ((weathercode >= 51L && weathercode <= 67L || (weathercode >= 80L && weathercode <=99L))) {
            weatherCondition = "Rain";
        }
        else if (weathercode >= 71L && weathercode <= 77L) {
            weatherCondition = "Snow";
        }

        return weatherCondition;

    }
}
