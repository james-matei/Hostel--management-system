import view.LoginView;
import util.MpesaCallbackServer;
import javafx.application.Application;

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting Hostel Management System...");

        // Start M-Pesa callback server before launching the UI
        MpesaCallbackServer.start();

        // Launch JavaFX app — this blocks until the app closes
        Application.launch(LoginView.class, args);

        // Stop callback server when app exits
        MpesaCallbackServer.stop();
    }
}