// No package declaration since it's at root

import view.LoginView;
import javafx.application.Application;

public class Main {

    public static void main(String[] args) {

        System.out.println("Starting Hostel Management System...");

        Application.launch(LoginView.class, args);

    }

}