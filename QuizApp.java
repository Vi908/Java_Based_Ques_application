import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class QuizApp {
    private static final String URL = "jdbc:mysql://localhost:3306/quiz_app";
    private static final String USER = "root";
    private static final String PASS = "Vik@s0505";

    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QuizApp().createMainScreen());
    }

    /**
     * @return 
     * @throws SQLException 
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    private void createMainScreen() {
        frame = new JFrame("Online Quiz Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(3, 1, 15, 15));
        ((JPanel) frame.getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton userLoginBtn = new JButton("User Login");
        JButton adminLoginBtn = new JButton("Admin Login");
        JButton registerBtn = new JButton("Register");

        frame.add(userLoginBtn);
        frame.add(adminLoginBtn);
        frame.add(registerBtn);

        userLoginBtn.addActionListener(e -> showLoginScreen(false));
        adminLoginBtn.addActionListener(e -> showLoginScreen(true));
        registerBtn.addActionListener(e -> showRegisterScreen());

        frame.setLocationRelativeTo(null); 
        frame.setVisible(true);
    }

    private void showLoginScreen(boolean isAdminLogin) {
        frame.dispose(); 
        frame = new JFrame(isAdminLogin ? "Admin Login" : "User Login");
        frame.setSize(400, 250);
        frame.setLayout(new GridLayout(4, 2, 10, 10));
        ((JPanel) frame.getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        frame.add(new JLabel("Username:"));
        usernameField = new JTextField();
        frame.add(usernameField);

        frame.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        frame.add(passwordField);

        frame.add(new JLabel()); 
        frame.add(new JLabel()); 

        JButton loginBtn = new JButton("Login");
        JButton backBtn = new JButton("Back");
        frame.add(loginBtn);
        frame.add(backBtn);

        loginBtn.addActionListener(e -> login(isAdminLogin));
        backBtn.addActionListener(e -> {
            frame.dispose();
            createMainScreen();
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void showRegisterScreen() {
        frame.dispose();
        frame = new JFrame("Register New User");
        frame.setSize(400, 250);
        frame.setLayout(new GridLayout(4, 2, 10, 10));
        ((JPanel) frame.getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        frame.add(new JLabel("Enter Username:"));
        usernameField = new JTextField();
        frame.add(usernameField);

        frame.add(new JLabel("Enter Password:"));
        passwordField = new JPasswordField();
        frame.add(passwordField);

        frame.add(new JLabel()); 
        frame.add(new JLabel()); 

        JButton regBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");
        frame.add(regBtn);
        frame.add(backBtn);

        regBtn.addActionListener(e -> register());
        backBtn.addActionListener(e -> {
            frame.dispose();
            createMainScreen();
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void login(boolean isAdminLogin) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Username and Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "SELECT id, is_admin FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");
                boolean isAdmin = rs.getBoolean("is_admin");

                if (isAdminLogin && !isAdmin) {
                    JOptionPane.showMessageDialog(frame, "Access Denied. Not an admin account.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (!isAdminLogin && isAdmin) {
                    JOptionPane.showMessageDialog(frame, "This is an admin account. Please use the Admin Login.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Welcome " + username + "!");
                    frame.dispose();
                    if (isAdmin) {
                        showAdminDashboard();
                    } else {
                        showUserDashboard(userId);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void register() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Username and Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(frame, "User registered successfully! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
            createMainScreen();
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(frame, "Username already exists. Please choose another one.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void showAdminDashboard() {
        frame.dispose();
        frame = new JFrame("Admin Dashboard");
        frame.setSize(500, 400);
        frame.setLayout(new GridLayout(5, 1, 15, 15));
        ((JPanel) frame.getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton createQuizBtn = new JButton("Create New Quiz");
        JButton addQuestionBtn = new JButton("Add Question to Existing Quiz");
        JButton viewQuizzesBtn = new JButton("View & Manage Quizzes/Questions");
        JButton logoutBtn = new JButton("Logout");

        frame.add(createQuizBtn);
        frame.add(addQuestionBtn);
        frame.add(viewQuizzesBtn);
        frame.add(logoutBtn);

        createQuizBtn.addActionListener(e -> createQuiz());
        addQuestionBtn.addActionListener(e -> addQuestionToQuiz());
        viewQuizzesBtn.addActionListener(e -> listQuizzes());
        logoutBtn.addActionListener(e -> {
            frame.dispose();
            createMainScreen();
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void showUserDashboard(int userId) {
        frame.dispose();
        frame = new JFrame("User Dashboard");
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(2, 1, 15, 15));
        ((JPanel) frame.getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton takeQuizBtn = new JButton("Take a Quiz");
        JButton logoutBtn = new JButton("Logout");

        frame.add(takeQuizBtn);
        frame.add(logoutBtn);

        takeQuizBtn.addActionListener(e -> takeQuiz());
        logoutBtn.addActionListener(e -> {
            frame.dispose();
            createMainScreen();
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    private void createQuiz() {
        String title = JOptionPane.showInputDialog(frame, "Enter the title for the new quiz:", "Create Quiz", JOptionPane.PLAIN_MESSAGE);
        if (title == null || title.trim().isEmpty()) {
            return; 
        }

        String sql = "INSERT INTO quizzes (title) VALUES (?)";
        int quizId = -1;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title.trim());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                quizId = rs.getInt(1);
            }

            if (quizId != -1) {
                JOptionPane.showMessageDialog(frame, "Quiz '" + title + "' created successfully (ID: " + quizId + "). Now, let's add questions.", "Success", JOptionPane.INFORMATION_MESSAGE);
                while (JOptionPane.showConfirmDialog(frame, "Add a question to this new quiz?", "Add Question", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    addQuestionFlow(quizId);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addQuestionToQuiz() {
        String quizIdStr = JOptionPane.showInputDialog(frame, "Enter the ID of the quiz to add a question to:", "Add Question", JOptionPane.PLAIN_MESSAGE);
        if (quizIdStr == null || quizIdStr.trim().isEmpty()) {
            return;
        }
        try {
            int quizId = Integer.parseInt(quizIdStr.trim());
            addQuestionFlow(quizId);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid ID. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * REFACTORED: The main logic for getting question details from the admin.
     * @param quizId The ID of the quiz this question belongs to.
     */
    private void addQuestionFlow(int quizId) {
        // Using a JPanel for a better input form
        JTextField questionText = new JTextField(20);
        JTextField opt1 = new JTextField(20);
        JTextField opt2 = new JTextField(20);
        JTextField opt3 = new JTextField(20);
        JTextField opt4 = new JTextField(20);
        String[] options = {"1", "2", "3", "4"};
        JComboBox<String> correctOption = new JComboBox<>(options);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Question:"));
        panel.add(questionText);
        panel.add(new JLabel("Option 1:"));
        panel.add(opt1);
        panel.add(new JLabel("Option 2:"));
        panel.add(opt2);
        panel.add(new JLabel("Option 3:"));
        panel.add(opt3);
        panel.add(new JLabel("Option 4:"));
        panel.add(opt4);
        panel.add(new JLabel("Correct Option (1-4):"));
        panel.add(correctOption);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Enter Question Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            // CORRECTED: Inserts into the 'questions' table with all correct columns.
            String sql = "INSERT INTO questions (quiz_id, question_text, option1, option2, option3, option4, correct_option) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, quizId);
                ps.setString(2, questionText.getText());
                ps.setString(3, opt1.getText());
                ps.setString(4, opt2.getText());
                ps.setString(5, opt3.getText());
                ps.setString(6, opt4.getText());
                ps.setString(7, (String) correctOption.getSelectedItem());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Question added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==== USER QUIZ TAKING ====

    private void takeQuiz() {
        // First, show available quizzes to the user.
        ArrayList<String> quizList = new ArrayList<>();
        ArrayList<Integer> quizIds = new ArrayList<>();
        String sql = "SELECT id, title FROM quizzes";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                quizIds.add(rs.getInt("id"));
                quizList.add(rs.getInt("id") + ": " + rs.getString("title"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Could not load quizzes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (quizList.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No quizzes are available at the moment.", "No Quizzes", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Let user choose a quiz
        Object selectedQuiz = JOptionPane.showInputDialog(frame, "Choose a quiz to take:", "Take Quiz",
                JOptionPane.QUESTION_MESSAGE, null, quizList.toArray(), quizList.get(0));

        if (selectedQuiz == null) return; // User cancelled

        int quizId = Integer.parseInt(selectedQuiz.toString().split(":")[0]);

        // REFACTORED: Fetches questions from the 'questions' table and calculates score.
        String questionSql = "SELECT * FROM questions WHERE quiz_id = ?";
        int score = 0;
        int totalMarks = 0;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(questionSql)) {
            ps.setInt(1, quizId);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                String qText = rs.getString("question_text");
                String o1 = rs.getString("option1");
                String o2 = rs.getString("option2");
                String o3 = rs.getString("option3");
                String o4 = rs.getString("option4");
                String correct = rs.getString("correct_option");
                int marks = rs.getInt("marks");
                totalMarks += marks;

                String[] options = {o1, o2, o3, o4};
                int choice = JOptionPane.showOptionDialog(frame, qText, "Question",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                // choice is 0-indexed (0 for option 1, 1 for option 2, etc.)
                if (choice != -1 && String.valueOf(choice + 1).equals(correct)) {
                    score += marks;
                }
            }
            JOptionPane.showMessageDialog(frame, "Quiz Finished!\nYour Score: " + score + " out of " + totalMarks, "Result", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error while taking quiz: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // A simple utility method to list quizzes, can be used by both admin and user
    private void listQuizzes() {
        StringBuilder sb = new StringBuilder("Available Quizzes:\n--------------------\n");
        String sql = "SELECT id, title FROM quizzes";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt("id"))
                        .append(" - Title: ").append(rs.getString("title"))
                        .append("\n");
            }
            JOptionPane.showMessageDialog(frame, sb.toString(), "Quiz List", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Could not retrieve quiz list: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}