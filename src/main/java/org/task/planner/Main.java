package org.task.planner;

import org.task.planner.database.SQLite;
import org.task.planner.model.Task;
import org.task.planner.model.User;

import java.sql.SQLException;
import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws SQLException {
        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Sign in ");
            System.out.println("2. Register ");
            System.out.println("3. Exit ");

            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    User user = signIn();
                    if (user != null) {
                        showTaskMenu(user.getId());
                    }
                    break;
                case 2:
                    register();
                    break;
                case 3:
                    System.out.println("Exiting the program. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private static void showTaskMenu(Integer userId) {
        try {
            while (true) {
                System.out.println("\nTask Planner Menu:");
                System.out.println("1. Add Task: ");
                System.out.println("2. View Tasks: ");
                System.out.println("3. Exit: ");

                System.out.print("Choose an option  (1-3): ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1 -> addTask(userId);
                    case 2 -> displayTasks(userId);
                    case 3 -> {
                        System.out.println("Goodbye! ");
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid choice. Please choose an option (1 to 3).");
                }
            }
        } catch (Exception e) {
            System.out.println("Error. Please check your input");
        }
    }

    private static void addTask(Integer userId) {
        try {
            System.out.print("Enter task category : ");
            String category = Main.scanner.next();

            System.out.print("Enter task name : ");
            String name = Main.scanner.next();

            System.out.print("Enter task description: ");
            Main.scanner.nextLine();
            String description = Main.scanner.nextLine();

            System.out.print("Enter task date  (example,  2023-12-31): ");
            String targetDate = Main.scanner.next();

            Task task = new Task(category, name, description, true, targetDate);
            SQLite.insertTask(userId, task);

            System.out.println("Task has been added!");
        } catch (Exception e) {
            System.out.println("An error occurred while adding a task.");
        }
    }

    private static void displayTasks(Integer userId) {
        try {
            List<Task> tasks = SQLite.getActiveUserTasks(userId);
            if (tasks.isEmpty()) {
                System.out.println("Task list empty");
                return;
            }

            System.out.println("\nList of active tasks:");
            for (Task task : tasks) {
                System.out.printf("%s. %s (%s - %s)\n%s\n\n", task.getId(), task.getName(), task.getCategory(), task.getTargetDate(), task.getDescription());
                System.out.print("Do you want to complete and delete the task? (y/n): ");
                String input = scanner.next();

                if ("y".equalsIgnoreCase(input)) {
                    SQLite.changeTaskStatus(task.getId(), false);
                    System.out.println("Task completed and deleted.\n");
                } else {
                    System.out.println("The task remains active.\n");
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred while viewing tasks.");
        }
    }

    private static User signIn() throws SQLException {
        System.out.print("Enter login: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = validateUser(username, password);
        if (user != null) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid username or password. Please try again.");
        }
        return user;
    }

    private static User register() throws SQLException {
        System.out.print("Create username: ");
        String login = scanner.nextLine();

        if (getUserByUsername(login) != null) {
            System.out.println("Username already exists. Please choose a different username.");
            return null;
        }

        System.out.print("Create password: ");
        String password = scanner.nextLine();

        User user = createUser(login, password);
        System.out.println("Account created successfully. Please log in.");

        return user;
    }

    private static int getUserChoice() {
        int choice = 0;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException ignored) {
        }
        return choice;
    }

    private static User getUserByUsername(String login) throws SQLException {
        return SQLite.getUserByUsername(login);
    }

    private static User validateUser(String login, String password) throws SQLException {
        return SQLite.validateUser(login, password);
    }

    private static User createUser(String login, String password) throws SQLException {
        return SQLite.insertUser(login, password);
    }
}
