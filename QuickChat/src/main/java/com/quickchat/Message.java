package com.quickchat;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A single QuickChat message (Parts 2 and 3).
 * Static members track the messages sent while the program is running.
 */
public class Message {

    /** What happened to the message. */
    public enum Status { SENT, STORED, DISREGARDED }

    public static final int MAX_LENGTH = 250;
    public static final String DEFAULT_FILE = "messages.json";

    // Messages sent while the program is running (not saved to JSON, not serialised by Gson)
    private static final ArrayList<Message> SENT_THIS_SESSION = new ArrayList<>();

    private String messageID;
    private int messageNumber;
    private String sender;
    private String recipient;
    private String text;
    private String messageHash;
    private Status status;

    /** Creates a message with a randomly generated ID. */
    public Message(int messageNumber, String sender, String recipient, String text) {
        this(generateMessageID(), messageNumber, sender, recipient, text);
    }

    /** Creates a message with a known ID (used when loading from JSON and in unit tests). */
    public Message(String messageID, int messageNumber, String sender, String recipient, String text) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.sender = sender;
        this.recipient = recipient;
        this.text = text;
        this.messageHash = createMessageHash();
    }

    /** Randomly generates a ten-digit message ID (leading zeros allowed). */
    public static String generateMessageID() {
        long number = ThreadLocalRandom.current().nextLong(10_000_000_000L);
        return String.format("%010d", number);
    }

    /** True if the message ID is not more than ten characters long. */
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    /** Message shown when an ID has been generated. */
    public String messageIDMessage() {
        return "Message ID generated: " + messageID;
    }

    /** Checks the message length (maximum 250 characters). */
    public String checkMessageLength() {
        if (text.length() <= MAX_LENGTH) {
            return "Message ready to send.";
        }
        int extra = text.length() - MAX_LENGTH;
        return "Message exceeds 250 characters by " + extra + "; please reduce the size.";
    }

    /** True if the message is within the length limit. */
    public boolean isLengthValid() {
        return text.length() <= MAX_LENGTH;
    }

    /** Checks the recipient number: international code followed by no more than ten digits. */
    public String checkRecipientCell() {
        if (Login.isValidCellNumber(recipient)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. "
                + "Please correct the number and try again.";
    }

    /** Hash = first two ID digits : message number : FIRSTWORDLASTWORD (all caps), e.g. 00:0:HITHANKS. */
    public String createMessageHash() {
        String[] words = text.trim().split("\\s+");
        String firstWord = cleanWord(words[0]);
        String lastWord = cleanWord(words[words.length - 1]);
        String idStart = messageID.substring(0, Math.min(2, messageID.length()));
        return (idStart + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
    }

    // Removes punctuation such as "tonight?" -> "tonight"
    private static String cleanWord(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

    /**
     * Applies the user's choice: 1 = send, 2 = disregard, 3 = store.
     * Returns the matching confirmation message.
     */
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                status = Status.SENT;
                SENT_THIS_SESSION.add(this);
                return "Message successfully sent.";
            case 2:
                status = Status.DISREGARDED;
                return "Press 0 to delete the message.";
            case 3:
                status = Status.STORED;
                return "Message successfully stored.";
            default:
                return "Invalid option.";
        }
    }

    /** Returns every message sent while the program is running. */
    public static String printMessages() {
        if (SENT_THIS_SESSION.isEmpty()) {
            return "No messages have been sent yet.";
        }
        StringBuilder builder = new StringBuilder();
        for (Message message : SENT_THIS_SESSION) {
            builder.append(message.details()).append("\n\n");
        }
        return builder.toString().trim();
    }

    /** Returns the total number of messages sent. */
    public static int returnTotalMessages() {
        return SENT_THIS_SESSION.size();
    }

    /** Clears the session (used by unit tests). */
    public static void resetSession() {
        SENT_THIS_SESSION.clear();
    }

    /** Stores this message in the JSON file (see MessageStorage for attribution). */
    public void storeMessage(Path file) {
        MessageStorage.append(file, this);
    }

    public void storeMessage() {
        storeMessage(Path.of(DEFAULT_FILE));
    }

    /** Full details in the required order: ID, Hash, Recipient, Message. */
    public String details() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + text;
    }

    public String getMessageID() { return messageID; }
    public int getMessageNumber() { return messageNumber; }
    public String getSender() { return sender; }
    public String getRecipient() { return recipient; }
    public String getText() { return text; }
    public String getMessageHash() { return messageHash; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
