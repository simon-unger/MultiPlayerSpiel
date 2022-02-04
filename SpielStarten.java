import java.util.Scanner;

public class SpielStarten {
    public static void main(String[] args) {
        MyGameServer server = new MyGameServer(1234);

        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Enter IP client:");
        String clientIP = myObj.nextLine();  // Read user input
        System.out.println("Client IP is: " + clientIP);  // Output user input
        System.out.println("Enter IP server:");
        String serverIP = myObj.nextLine();  // Read user input
        System.out.println("Server IP is: " + serverIP);  // Output user input
        System.out.println("Enter InGameName:");
        String inGameName = myObj.nextLine();  // Read user input
        System.out.println("InGameName is: " + inGameName);  // Output user input

        MyNetworkClient client = new MyNetworkClient(clientIP, 1234);
        MyGame spiel = new MyGame()
    }
}
