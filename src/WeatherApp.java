import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class WeatherApp {
    private static final String baseGeocode = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    private static final String googleKey = "AIzaSyAXOUJZME49BZfMWw3XGCqdrZ0-2MvQj6U";
    private static final String baseWeather = "https://api.darksky.net/forecast/";
    private static final String weatherKey = "6695e2cfcb5944be3b28b2bcbe4cce57";
    private static final String divider = "*****************************************************************************************";

    public static void main(String[] args) {
        System.out.println(divider);
        System.out.println("Welcome to Peter Stanton's CSS490 Weather application!");
        System.out.println("Powered by Google and Dark Sky");
        System.out.println("https://darksky.net/poweredby/");
        Scanner in = new Scanner(System.in);
        while(true) {
            System.out.println("Please enter the address, zip code, or city name you want weather for below, " +
                    "and \"Exit\" to quit:");
            String input = in.nextLine();
            if(input.isEmpty()) {
                continue;
            }
            if(input.equalsIgnoreCase("Exit")) {
                in.close();
                System.exit(0);
            }
            String[] parsed = input.split("\\W+");
            URL googleURL = processGeoURL(parsed);
            String geoResult = processResponse(googleURL);
            ArrayList<String> geoSplit = new ArrayList<>(Arrays.asList(geoResult.split("\n")));
            int geoLatIndex = -1;
            int geoLongIndex = -1;
            for (int i = 0; i < geoSplit.size(); i++) {
                if (geoSplit.get(i).contains("location")) {
                    geoLatIndex = i + 1;
                    geoLongIndex = i + 2;
                    break;
                }
            }
            if (geoLatIndex == -1) {
                return;
            }
            double geoLat = Double.parseDouble(geoSplit.get(geoLatIndex).split(":")[1].replaceAll(",", ""));
            double geoLong = Double.parseDouble(geoSplit.get(geoLongIndex).split(":")[1].replaceAll(",", ""));
            URL darkSkyURL = processWeatherURL(geoLat, geoLong);
            String weatherResult = processResponse(darkSkyURL);
            printResults(weatherResult, input);
        }
    }

    private static URL processGeoURL(String[] parsed) {
        String joined = String.join("+", parsed);
        joined += "&key=" + googleKey;
        try {
            return new URL(baseGeocode + joined);
        } catch (MalformedURLException e) { }
        return null;
    }

    private static String processResponse(URL inURL) { //credit to https://stackoverflow.com/a/7467629/4864069
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inURL.openStream()));
            String outResult;
            StringBuilder builder = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            try {
                while ((read = reader.read(chars)) != -1)
                    builder.append(chars, 0, read);

                outResult = builder.toString();
                return outResult;
            } catch (IOException e) { }
        } catch (IOException e) { }
        return null;
    }

    private static URL processWeatherURL(double geoLat, double geoLong) {
        String joined = baseWeather + weatherKey + '/' + geoLat + ',' + geoLong;
        try {
            return new URL(joined);
        } catch (MalformedURLException e) { }
        return null;
    }

    private static void printResults(String weatherResult, String locale) {
        //stuff here.
        String[] results = weatherResult.split("\\{");
        StringBuilder output = new StringBuilder();
        List<String> curWeather = new ArrayList<>();
        List<String> minutelyWeather = new ArrayList<>();
        List<String> hourlyWeather = new ArrayList<>();
        List<String> alerts = new ArrayList<>();
        List<String> getURL = new ArrayList<>();
        URL warningURL = null;
        output.append('\n' + "Weather for: " + locale + "\n");
        for (int i = 0; i < results.length; i++) {
            String cur = results[i];
            if (cur.contains("currently")) {
                output.append("Timezone: " +
                        cur.split(",")[2].split(":")[1].replace("\"", "") + "\n");
                output.append(divider + '\n');
                curWeather = Arrays.asList(results[i + 1].split(","));
            } else if (cur.contains("minutely")) {
                minutelyWeather = Arrays.asList(results[i + 1].split(","));
            } else if (cur.contains("hourly")) {
                hourlyWeather = Arrays.asList(results[i + 1].split(","));
            } else if (cur.contains("alerts")) {
                alerts = Arrays.asList(results[i + 1].split(":"));
                getURL = Arrays.asList(results[i + 1].split(","));
                break;
            }
        }
        for (String checker : getURL) {
            if (checker.contains("uri")) {
                try {
                    warningURL = new URL(checker.split("\"")[3].replace("\"", ""));
                } catch (MalformedURLException e) {
                }
            }
        }
        double preIntense = -1.0;
        double preChance = -1.0;
        double temp = -10000.00;
        double windSpeed = -1.0;
        double visibility = -1.0;


        for (String curWeatherChecker : curWeather) {
            if (curWeatherChecker.contains("precipIntensity")) {
                preIntense = Double.parseDouble(curWeatherChecker.split(":")[1]);
            } else if (curWeatherChecker.contains("precipProbability")) {
                preChance = Double.parseDouble(curWeatherChecker.split(":")[1]);
            } else if (curWeatherChecker.contains("temperature")) {
                temp = Double.parseDouble(curWeatherChecker.split(":")[1]);
            } else if (curWeatherChecker.contains("windSpeed")) {
                windSpeed = Double.parseDouble(curWeatherChecker.split(":")[1]);
            } else if (curWeatherChecker.contains("visibility")) {
                visibility = Double.parseDouble(curWeatherChecker.split(":")[1]);
            }
        }
        if(preIntense >= 0.0) {
            output.append("Precipitation intensity is " + preIntense + " millimeters per hour " + '\n');
        }
        if(preChance >= 0.0) {
            output.append("Chance of continuing precipitation is " + preChance + '\n');
        }
        if(temp >= -10000) {
            output.append("Current local temperature is " + temp + " degrees Fahrenheit" + '\n');
        }
        if(windSpeed >= 0.0) {
            output.append("Current windSpeed is " + windSpeed + " meters per second" + '\n');
        }
        if(visibility >= 0.0) {
            output.append("Current visibility is " + visibility + " kilometers" + '\n');
        }
        output.append(divider + '\n');

        String rightNow = "";
        for (String minWeatherChecker : minutelyWeather) {
            if (minWeatherChecker.contains("summary")) {
                rightNow = minWeatherChecker.split(":")[1].replace("\"", "");
            }
        }
        if (!rightNow.isEmpty()) {
            output.append("Right now the weather is: " + rightNow + '\n');
        }
        String inFuture = "";
        for (String hourWeatherChecker : hourlyWeather) {
            if (hourWeatherChecker.contains("summary")) {
                inFuture = hourWeatherChecker.split(":")[1].replace("\"", "");
            }
        }
        if (!inFuture.isEmpty()) {
            output.append("In the near future it will be: " + inFuture + '\n');
        }
        if (!alerts.isEmpty()) {
            String aTitle = "";
            String regions = "";
            String advisoryType = "";
            String aDescription = "";

            for (int i = 0; i < alerts.size(); i++) {
                String alertChecker = alerts.get(i);
                if (alertChecker.contains("title")) {
                    aTitle = alerts.get(i + 1).split(",")[0].replace("\"", "");
                    regions = alerts.get(i + 2).replace("severity", "");
                    advisoryType = alerts.get(i + 3).split(",")[0].replace("\"", "");
                } else if (alertChecker.contains("description")) {
                    aDescription = alerts.get(i + 1);
                }
            }
            output.append(divider + '\n');
            output.append(aTitle + '\n');
            output.append("Affected regions:" + '\n');
            output.append(regions + '\n');
            output.append("The government has classified this alert as a: " + advisoryType + '\n');
            output.append("Official warning follows" + '\n');
            output.append(aDescription + '\n');
            output.append(warningURL);
            output.append('\n');
        }
        output.append(divider + '\n');
        System.out.println(output.toString());
    }
}
