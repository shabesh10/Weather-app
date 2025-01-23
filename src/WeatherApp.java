import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.json.simple.JSONObject;

public class WeatherApp extends JFrame {

    private JSONObject weatherData = null;

    public WeatherApp() {

        super("Weather App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 650);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        addGuiComponents();
    }

    private void addGuiComponents() {
        JTextField searchField = new JTextField();
        searchField.setBounds(15, 15, 350, 45);
        setFont(new Font("Dialog", Font.PLAIN, 24));
        add(searchField);

        //weather condition image
        JLabel weatherConditionImage = new JLabel(loadImage("C:\\Users\\LENOVO\\Desktop\\Weather-app\\src\\assests\\cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        //Displaying temperature
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        //center the text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        // adding the description for weather condition
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        add(weatherConditionDesc);

        //humidity image
        JLabel humidityImage = new JLabel(loadImage("C:\\Users\\LENOVO\\Desktop\\Weather-app\\src\\assests\\humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        //diplaying humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100% </html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        //add windspeed image and description
        JLabel windspeedImage = new JLabel(loadImage("C:\\Users\\LENOVO\\Desktop\\Weather-app\\src\\assests\\windspeed.png"));
        windspeedImage.setBounds(220, 500, 74, 66);
        add(windspeedImage);

        JLabel windspeedDesc = new JLabel("<html><b>Wind Speed</b> 15 km/h </html>");
        windspeedDesc.setBounds(310, 500, 85, 55);
        windspeedDesc.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedDesc);

        //search button
        JButton searchButton = new JButton(loadImage("C:\\Users\\LENOVO\\Desktop\\Weather-app\\src\\assests\\search.png"));

        //changing the cursor to hand hover when it hovers over the search icon
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                String userInput = searchField.getText();

                // validating the input - removing all the whitespaces
                if (userInput.replaceAll("\\s", "").length() <= 0) {
                    return;
                }

                //retrieving the weather data
                weatherData = Backend.getWeatherData(userInput);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                //updating the UI

                //update the weather image
                String weatherCondition = (String) weatherData.get("weather_condition");

                //update the weather condition image corresponding to the weather condition
                switch (weatherCondition) {
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("C:\\Users\\LENOVO\\Desktop\\Weather-app\\src\\assests\\clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("C:\\Users\\LENOVO\\Desktop\\Weather-app\\src\\assests\\cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("C:\\Users\\LENOVO\\Desktop\\Weather-app\\src\\assests\\rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("C:\\Users\\LENOVO\\Desktop\\Weather-app\\src\\assests\\snow.png"));
                        break;
                    case "Thunderstorm":
                        weatherConditionImage.setIcon(loadImage("C:\\Users\\LENOVO\\Desktop\\Weather-app\\src\\assests\\thunderstorm.png"));
                        break;
                    default:
                        weatherConditionImage.setIcon(loadImage("C:\\Users\\LENOVO\\Desktop\\Weather-app\\src\\assests\\clear.png"));
                        break;
                }

                //  update the temperature text
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + "C");

                //update the weather condition description
                weatherConditionDesc.setText(weatherCondition);

                //update the humidity text
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "% </html>");

                //update the windspeed text
                double windspeed = (double) weatherData.get("windspeed");
                windspeedDesc.setText("<html><b>Wind Speed</b> " + windspeed + " km/h </html>");

            }
        });
        add(searchButton);
    }

    private ImageIcon loadImage(String resourcePath) {
        //reads the image and then return a ImageIcon object for our container to render it
        try {
            BufferedImage image = ImageIO.read(new File(resourcePath));
            
            return new ImageIcon(image);            
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Couldnt find the resource");
        return null;
    }

}