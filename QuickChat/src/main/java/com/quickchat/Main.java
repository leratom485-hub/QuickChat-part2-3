package com.quickchat;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * QuickChat console application: registration, login, sending messages and stored-message tools.
 */
public class Main {

    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        Login login = register();
        if (!login(login)) {
            return;
        }

        System.out.println("\nWelcome to QuickChat.");
        String sender = login.getFirstName();
        MessageManager manager = new MessageManager(Path.of(Message.DEFAULT_FILE));
        manager.loadStoredMessages(); // read the JSON file into the stored messages array

        int maxMessages = readInt("How many messages do you wish to enter? ", 1, 1000);
        boolean messagesEntered = false;
        boolean running = true;

        while (running) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            System.out.println("5) Load developer test data");
            int choice = readInt("Choose an option: ", 1, 5);

            switch (choice) {
                case 1 -> {
                    if (messagesEntered) {
                        System.out.println("You have already entered your " + maxMessages + " message(s).");
                    } else {
                        sendMessages(maxMessages, sender, manager);
                        messagesEntered = true;
                    }
                }
                case 2 -> System.out.println("Coming Soon.");
                case 3 -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                case 4 -> storedMessagesMenu(manager);
                case 5 -> loadTestData(manager, sender);
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // ---------- Part 1 ----------

    private static Login register() {
        System.out.println("=== Register ===");
        System.out.print("First name: ");
        String firstName = SCANNER.nextLine().trim();
        System.out.print("Last name: ");
        String lastName = SCANNER.nextLine().trim();
        Login login = new Login(firstName, lastName);

        String username;
        do {
            System.out.print("Username: ");
            username = SCANNER.nextLine().trim();
            System.out.println(login.usernameMessage(username));
        } while (!login.checkUserName(username));

        String password;
        do {
            System.out.print("Password: ");
            password = SCANNER.nextLine();
            System.out.println(login.passwordMessage(password));
        } while (!login.checkPasswordComplexity(password));

        String cell;
        do {
            System.out.print("Cell phone number (e.g. +27838968976): ");
            cell = SCANNER.nextLine().trim();
            System.out.println(login.cellPhoneMessage(cell));
        } while (!login.checkCellPhoneNumber(cell));

        System.out.println(login.registerUser(username, password, cell));
        return login;
    }

    private static boolean login(Login login) {
        System.out.println("\n=== Login ===");
        for (int attempt = 1; attempt <= 3; attempt++) {
            System.out.print("Username: ");
            String username = SCANNER.nextLine().trim();
            System.out.print("Password: ");
            String password = SCANNER.nextLine();
            boolean loggedIn = login.loginUser(username, password);
            System.out.println(login.returnLoginStatus(loggedIn));
            if (loggedIn) {
                return true;
            }
        }
        System.out.println("Too many failed attempts. Exiting.");
        return false;
    }

    // ---------- Part 2 ----------

    private static void sendMessages(int maxMessages, String sender, MessageManager manager) {
        for (int i = 0; i < maxMessages; i++) {
            System.out.println("\n--- Message " + (i + 1) + " of " + maxMessages + " ---");

            String recipient;
            Message message;
            do {
                System.out.print("Recipient cell number: ");
                recipient = SCANNER.nextLine().trim();
                message = new Message(i, sender, recipient, "x");
                System.out.println(message.checkRecipientCell());
            } while (!Login.isValidCellNumber(recipient));

            String text;
            do {
                System.out.print("Message: ");
                text = SCANNER.nextLine().trim();
                message = new Message(i, sender, recipient, text.isEmpty() ? "x" : text);
                if (text.isEmpty()) {
                    System.out.println("A message cannot be empty.");
                } else if (!message.isLengthValid()) {
                    System.out.println("Please enter a message of less than 250 characters.");
                    System.out.println(message.checkMessageLength());
                }
            } while (text.isEmpty() || !message.isLengthValid());
            System.out.println(message.checkMessageLength());
            System.out.println(message.messageIDMessage());

            boolean decided = false;
            while (!decided) {
                System.out.println("1) Send Message  2) Disregard Message  3) Store Message to send later");
                int option = readInt("Choose an option: ", 1, 3);
                System.out.println(message.sentMessage(option));
                if (option == 2) {
                    System.out.print("> ");
                    if (SCANNER.nextLine().trim().equals("0")) {
                        System.out.println("Message deleted.");
                        decided = true;
                    } else {
                        System.out.println("Message not deleted; please choose again.");
                    }
                } else {
                    if (option == 1) {
                        System.out.println("\n" + message.details());
                    } else {
                        message.storeMessage();
                    }
                    decided = true;
                }
            }
            manager.addMessage(message);
        }
        System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
    }

    // ---------- Part 3 ----------

    private static void storedMessagesMenu(MessageManager manager) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Stored Messages ---");
            System.out.println("1) Display sender and recipient of all stored messages");
            System.out.println("2) Display the longest stored message");
            System.out.println("3) Search for a message ID");
            System.out.println("4) Search messages for a recipient");
            System.out.println("5) Delete a message using its hash");
            System.out.println("6) Report of all stored messages");
            System.out.println("7) Report of all sent messages");
            System.out.println("8) Back to main menu");
            int choice = readInt("Choose an option: ", 1, 8);

            switch (choice) {
                case 1 -> System.out.println(manager.displaySenderAndRecipient());
                case 2 -> System.out.println(manager.getLongestStoredMessage());
                case 3 -> {
                    System.out.print("Message ID: ");
                    System.out.println(manager.searchByMessageID(SCANNER.nextLine().trim()));
                }
                case 4 -> {
                    System.out.print("Recipient: ");
                    ArrayList<String> found = manager.searchByRecipient(SCANNER.nextLine().trim());
                    if (found.isEmpty()) {
                        System.out.println("No messages found for that recipient.");
                    }
                    for (String text : found) {
                        System.out.println("- " + text);
                    }
                }
                case 5 -> {
                    System.out.print("Message hash: ");
                    System.out.println(manager.deleteByHash(SCANNER.nextLine().trim()));
                }
                case 6 -> System.out.println(manager.storedReport());
                case 7 -> System.out.println(manager.sentReport());
                default -> back = true;
            }
        }
    }

    /** Fills the arrays with the five test messages from the brief (developer use / demo). */
    private static void loadTestData(MessageManager manager, String sender) {
        String[][] data = {
            {"+27834557896", "Did you get the cake?", "SENT"},
            {"+27838884567", "Where are you? You are late! I have asked you to be on time.", "STORED"},
            {"+27834484567", "Yohoooo, I am at your gate.", "DISREGARDED"},
            {"0838884567", "It is dinner time!", "SENT"},
            {"+27838884567", "Ok, I am leaving without you.", "STORED"}
        };
        for (int i = 0; i < data.length; i++) {
            Message message = new Message(i, sender, data[i][0], data[i][1]);
            message.setStatus(Message.Status.valueOf(data[i][2]));
            manager.addMessage(message);
        }
        System.out.println("Test data loaded into the arrays.");
    }

    // ---------- Input helper ----------

    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // fall through to the error message
            }
            System.out.println("Please enter a number between " + min + " and " + max + ".");
        }
    }
}
