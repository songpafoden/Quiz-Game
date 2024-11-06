import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class QuizGameClientGUI extends JFrame {
    private JTextArea questionArea;
    private JTextField answerField;
    private JButton submitButton;
    private JLabel feedbackLabel;
    private JLabel scoreLabel;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private int score = 0;
    private int currentQuestionIndex = 0;

    public QuizGameClientGUI() {
        setTitle("Quiz Game Client");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Font largeFont = new Font("SansSerif", Font.BOLD, 18);
        Font mediumFont = new Font("SansSerif", Font.PLAIN, 16);

        questionArea = new JTextArea();
        questionArea.setEditable(false);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionArea.setFont(largeFont);
        add(new JScrollPane(questionArea), BorderLayout.CENTER);
        

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(3, 1));

        answerField = new JTextField();
        answerField.setFont(mediumFont);
        submitButton = new JButton("Submit Answer");
        submitButton.setFont(mediumFont);

        feedbackLabel = new JLabel("Feedback: ");
        feedbackLabel.setFont(mediumFont);
        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        scoreLabel.setFont(largeFont);

        bottomPanel.add(answerField);
        bottomPanel.add(submitButton);
        bottomPanel.add(feedbackLabel);
        add(bottomPanel, BorderLayout.SOUTH);
        add(scoreLabel, BorderLayout.NORTH);

        submitButton.addActionListener(new SubmitButtonListener());

        String serverIp = "localhost";
        int serverPort = 9999;
        try {
            socket = new Socket(serverIp, serverPort);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            displayNextQuestion();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayNextQuestion() {
        try {
            if (currentQuestionIndex < QuizData.questions.length) {
                questionArea.setText(in.readLine());
            } else {
                showFinalScore();
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error displaying question.", "Display Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class SubmitButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String answer = answerField.getText().trim().toLowerCase();
            out.println(answer);
            answerField.setText("");

            try {
                String feedback = in.readLine();
                feedbackLabel.setText("Feedback: " + feedback);
                if (feedback.equals("Correct!")) {
                    score += 10;
                    scoreLabel.setText("Score: " + score);
                }

                currentQuestionIndex++;
                if (currentQuestionIndex < QuizData.questions.length) {
                    displayNextQuestion();
                } else {
                    showFinalScore();
                }

            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(QuizGameClientGUI.this, "Error reading from server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showFinalScore() {
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>"
                + "<span style='font-size:18pt;'>Quiz Finished!</span><br>"
                + "<span style='font-size:18pt;'>Your final score: " + score + "</span>"
                + "</div></html>");

        JOptionPane.showMessageDialog(this,
                messageLabel,
                "Quiz Complete",
                JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                QuizGameClientGUI client = new QuizGameClientGUI();
                client.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error initializing GUI.", "Initialization Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
