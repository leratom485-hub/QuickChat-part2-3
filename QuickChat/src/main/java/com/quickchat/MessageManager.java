package com.quickchat;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the arrays of messages, hashes and IDs and provides the search, delete
 * and report features for the "Stored Messages" menu (Part 3).
 */
public class MessageManager {

    private final ArrayList<Message> sentMessages = new ArrayList<>();
    private final ArrayList<Message> disregardedMessages = new ArrayList<>();
    private final ArrayList<Message> storedMessages = new ArrayList<>();
    private final ArrayList<String> messageHashes = new ArrayList<>();
    private final ArrayList<String> messageIDs = new ArrayList<>();
    private final Path jsonFile; // null = do not touch any file

    public MessageManager(Path jsonFile) {
        this.jsonFile = jsonFile;
    }

    /** Adds a message to the correct array (by its status) and records its hash and ID. */
    public void addMessage(Message message) {
        switch (message.getStatus()) {
            case SENT -> sentMessages.add(message);
            case STORED -> storedMessages.add(message);
            case DISREGARDED -> disregardedMessages.add(message);
            default -> { return; }
        }
        messageHashes.add(message.getMessageHash());
        messageIDs.add(message.getMessageID());
    }

    /** Reads the JSON file into the stored messages array (skips IDs already loaded). */
    public int loadStoredMessages() {
        if (jsonFile == null) {
            return 0;
        }
        int loaded = 0;
        for (Message message : MessageStorage.readAll(jsonFile)) {
            if (!messageIDs.contains(message.getMessageID())) {
                message.setStatus(Message.Status.STORED);
                addMessage(message);
                loaded++;
            }
        }
        return loaded;
    }

    /** The text of every sent message. */
    public ArrayList<String> getSentMessageTexts() {
        return texts(sentMessages);
    }

    public ArrayList<String> getMessageHashes() { return messageHashes; }
    public ArrayList<String> getMessageIDs() { return messageIDs; }
    public ArrayList<Message> getStoredMessages() { return storedMessages; }
    public ArrayList<Message> getDisregardedMessages() { return disregardedMessages; }
    public ArrayList<Message> getSentMessages() { return sentMessages; }

    /** a) Sender and recipient of all stored messages. */
    public String displaySenderAndRecipient() {
        if (storedMessages.isEmpty()) {
            return "There are no stored messages.";
        }
        StringBuilder builder = new StringBuilder();
        for (Message message : storedMessages) {
            builder.append("Sender: ").append(message.getSender())
                    .append(" | Recipient: ").append(message.getRecipient()).append("\n");
        }
        return builder.toString().trim();
    }

    /** b) The longest stored message. */
    public String getLongestStoredMessage() {
        return longestMessage(storedMessages);
    }

    /** The longest message text in any list (first one wins a tie). */
    public static String longestMessage(List<Message> messages) {
        String longest = "";
        for (Message message : messages) {
            if (message.getText().length() > longest.length()) {
                longest = message.getText();
            }
        }
        return longest.isEmpty() ? "There are no messages." : longest;
    }

    /** Finds a message by ID in any array (null if not found). */
    public Message findByMessageID(String id) {
        for (Message message : allMessages()) {
            if (message.getMessageID().equals(id)) {
                return message;
            }
        }
        return null;
    }

    /** c) Search for a message ID and display the recipient and message. */
    public String searchByMessageID(String id) {
        Message message = findByMessageID(id);
        if (message == null) {
            return "No message found with ID " + id + ".";
        }
        return "Recipient: " + message.getRecipient() + "\nMessage: " + message.getText();
    }

    /** d) Every sent or stored message for one recipient. */
    public ArrayList<String> searchByRecipient(String recipient) {
        ArrayList<String> found = new ArrayList<>();
        for (Message message : sentMessages) {
            if (message.getRecipient().equals(recipient)) {
                found.add(message.getText());
            }
        }
        for (Message message : storedMessages) {
            if (message.getRecipient().equals(recipient)) {
                found.add(message.getText());
            }
        }
        return found;
    }

    /** e) Delete a message using its hash (removed from every array and the JSON file). */
    public String deleteByHash(String hash) {
        for (Message message : allMessages()) {
            if (message.getMessageHash().equalsIgnoreCase(hash)) {
                boolean wasStored = storedMessages.remove(message);
                sentMessages.remove(message);
                disregardedMessages.remove(message);
                messageHashes.remove(message.getMessageHash());
                messageIDs.remove(message.getMessageID());
                if (wasStored && jsonFile != null) {
                    MessageStorage.writeAll(jsonFile, storedMessages);
                }
                return "Message: \"" + message.getText() + "\" successfully deleted.";
            }
        }
        return "No message found with hash " + hash + ".";
    }

    /** f) Report listing the full details of the stored messages. */
    public String storedReport() {
        return buildReport("STORED MESSAGES REPORT", storedMessages);
    }

    /** Report listing the sent messages (hash, recipient, message). */
    public String sentReport() {
        return buildReport("SENT MESSAGES REPORT", sentMessages);
    }

    private String buildReport(String title, List<Message> messages) {
        StringBuilder builder = new StringBuilder("===== " + title + " =====\n");
        if (messages.isEmpty()) {
            return builder.append("No messages to display.").toString();
        }
        for (Message message : messages) {
            builder.append("Message ID: ").append(message.getMessageID()).append("\n")
                    .append("Message Hash: ").append(message.getMessageHash()).append("\n")
                    .append("Sender: ").append(message.getSender()).append("\n")
                    .append("Recipient: ").append(message.getRecipient()).append("\n")
                    .append("Message: ").append(message.getText()).append("\n")
                    .append("-----------------------------\n");
        }
        return builder.toString().trim();
    }

    private ArrayList<Message> allMessages() {
        ArrayList<Message> all = new ArrayList<>(sentMessages);
        all.addAll(storedMessages);
        all.addAll(disregardedMessages);
        return all;
    }

    private static ArrayList<String> texts(List<Message> messages) {
        ArrayList<String> result = new ArrayList<>();
        for (Message message : messages) {
            result.add(message.getText());
        }
        return result;
    }
}
