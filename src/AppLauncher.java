import javax.swing.SwingUtilities;

public class AppLauncher {
    public static void main(String[] args) {
        //calling invokeLater() because it makes updates to the GUI more thread safe

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new WeatherApp().setVisible(true);
                //System.out.println(Backend.getLocationData("Tokyo"));
                //System.out.println(Backend.getCurrentTime());
            }
        });

    }
    
}
