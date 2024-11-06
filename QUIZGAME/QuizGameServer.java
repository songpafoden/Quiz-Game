import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class QuizGameServer {
    private ExecutorService pool = Executors.newFixedThreadPool(20);

    public static void main(String[] args) {
        new QuizGameServer().startServer();
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(9999)) {
            System.out.println("QuizGame Server is running...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                pool.execute(new QuizHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class QuizHandler implements Runnable {
        private final Socket socket;
    
        QuizHandler(Socket socket) {
            this.socket = socket;
        }
    
        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                
                int totalscore = 10*QuizData.questions.length;
                int score = 0;

                for (int i = 0; i < QuizData.questions.length; i++) {
                    out.println(i+1 + ". " + QuizData.questions[i]); 
                    String clientAnswer = in.readLine();
    
                    if (clientAnswer != null && clientAnswer.equals(QuizData.answers[i])) {
                        out.println("Correct!");
                        score += 10;
                    } else {
                        out.println("Incorrect!");
                    }
                }
                out.println("Quiz finished! Your score: " + score + "/" + totalscore);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error closing socket: " + e.getMessage());
                }
            }
        }
    }
}