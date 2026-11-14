package com.quickchat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Part 3 unit tests (arrays, search, delete, report). */
class MessageManagerTest {

    private static final String LONGEST = "Where are you? You are late! I have asked you to be on time.";

    private MessageManager manager;
    private Message message2;

    @BeforeEach
    void setUp(@TempDir Path folder) {
        manager = new MessageManager(folder.resolve("messages.json"));
        // Test data from the brief. Message 4 uses the developer ID 0838884567.
        add("1100000001", 0, "+27834557896", "Did you get the cake?", Message.Status.SENT);
        message2 = add("2200000002", 1, "+27838884567", LONGEST, Message.Status.STORED);
        add("3300000003", 2, "+27834484567", "Yohoooo, I am at your gate.", Message.Status.DISREGARDED);
        add("0838884567", 3, "0838884567", "It is dinner time!", Message.Status.SENT);
        add("5500000005", 4, "+27838884567", "Ok, I am leaving without you.", Message.Status.STORED);
    }

    private Message add(String id, int number, String recipient, String text, Message.Status status) {
        Message message = new Message(id, number, "Developer", recipient, text);
        message.setStatus(status);
        manager.addMessage(message);
        return message;
    }

    @Test
    void sentMessagesArray_correctlyPopulated() {
        assertEquals(List.of("Did you get the cake?", "It is dinner time!"), manager.getSentMessageTexts());
    }

    @Test
    void otherArraysPopulated() {
        assertEquals(1, manager.getDisregardedMessages().size());
        assertEquals(2, manager.getStoredMessages().size());
        assertEquals(5, manager.getMessageHashes().size());
        assertEquals(5, manager.getMessageIDs().size());
    }

    @Test
    void longestMessage() {
        assertEquals(LONGEST, manager.getLongestStoredMessage());
        assertEquals(LONGEST, MessageManager.longestMessage(manager.getStoredMessages()));
    }

    @Test
    void searchForMessageID() {
        assertEquals("It is dinner time!", manager.findByMessageID("0838884567").getText());
        assertEquals("Recipient: 0838884567\nMessage: It is dinner time!",
                manager.searchByMessageID("0838884567"));
    }

    @Test
    void searchForMessageID_notFound() {
        assertEquals("No message found with ID 9999999999.", manager.searchByMessageID("9999999999"));
    }

    @Test
    void searchByRecipient() {
        assertEquals(List.of(LONGEST, "Ok, I am leaving without you."),
                manager.searchByRecipient("+27838884567"));
    }

    @Test
    void deleteByHash() {
        String hash = message2.getMessageHash();
        assertEquals("Message: \"" + LONGEST + "\" successfully deleted.", manager.deleteByHash(hash));
        assertEquals(1, manager.getStoredMessages().size());
        assertFalse(manager.getMessageHashes().contains(hash));
        assertEquals(4, manager.getMessageIDs().size());
    }

    @Test
    void deleteByHash_notFound() {
        assertEquals("No message found with hash 00:0:NOPE.", manager.deleteByHash("00:0:NOPE"));
    }

    @Test
    void sentReport_showsHashRecipientAndMessage() {
        String report = manager.sentReport();
        assertTrue(report.contains(manager.getSentMessages().get(0).getMessageHash()));
        assertTrue(report.contains("+27834557896"));
        assertTrue(report.contains("Did you get the cake?"));
        assertTrue(report.contains("It is dinner time!"));
    }

    @Test
    void storedReport_showsFullDetails() {
        String report = manager.storedReport();
        assertTrue(report.contains(message2.getMessageID()));
        assertTrue(report.contains(LONGEST));
        assertFalse(report.contains("Did you get the cake?"));
    }

    @Test
    void jsonFile_readIntoStoredArray(@TempDir Path folder) {
        Path file = folder.resolve("saved.json");
        MessageManager writer = new MessageManager(file);
        message2.storeMessage(file);
        MessageManager reader = new MessageManager(file);
        assertEquals(1, reader.loadStoredMessages());
        assertNotNull(reader.findByMessageID("2200000002"));
        assertEquals(0, reader.loadStoredMessages()); // no duplicates on a second load
        assertEquals(0, writer.getStoredMessages().size());
    }
}
