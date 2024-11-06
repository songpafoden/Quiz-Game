import java.io.*;
import java.net.*;

public class QuizGameClient {
    public static void main(String[] args) {
        try {
            String serverIp = InetAddress.getLocalHost().getHostAddress();
            try (Socket socket = new Socket(serverIp, 9999)) {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true); 
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in)); 

                String response;
                while ((response = in.readLine()) != null) {
                    if (response.startsWith("Quiz finished")) {
                        System.out.println(response);
                        break;
                    } else {
                        System.out.println("Question: " + response);
                        System.out.print("Your answer: ");
                        String answer = userInput.readLine();
                        out.println(answer);
                        System.out.println(in.readLine());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
