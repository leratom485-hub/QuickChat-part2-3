package com.quickchat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Part 2 unit tests. */
class MessageTest {

    private Message message1;
    private Message message2;

    @BeforeEach
    void setUp() {
        Message.resetSession();
        // Test data from the brief (IDs fixed so the hashes can be predicted)
        message1 = new Message("0012345678", 0, "Developer", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
        message2 = new Message("1012345678", 1, "Developer", "08575975889",
                "Hi Keegan, did you receive the payment?");
    }

    @Test
    void messageLength_success() {
        assertEquals("Message ready to send.", message1.checkMessageLength());
    }

    @Test
    void messageLength_failure() {
        Message tooLong = new Message(0, "Developer", "+27718693002", "a".repeat(260));
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                tooLong.checkMessageLength());
    }

    @Test
    void messageLength_exactly250IsAllowed() {
        Message limit = new Message(0, "Developer", "+27718693002", "a".repeat(250));
        assertEquals("Message ready to send.", limit.checkMessageLength());
    }

    @Test
    void recipient_success() {
        assertEquals("Cell phone number successfully captured.", message1.checkRecipientCell());
    }

    @Test
    void recipient_failure() {
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. "
                + "Please correct the number and try again.", message2.checkRecipientCell());
    }

    @Test
    void messageHash_message1() {
        assertEquals("00:0:HITONIGHT", message1.createMessageHash());
    }

    @Test
    void messageHash_loopOverAllMessages() {
        Message[] messages = {message1, message2};
        String[] expected = {"00:0:HITONIGHT", "10:1:HIPAYMENT"};
        for (int i = 0; i < messages.length; i++) {
            assertEquals(expected[i], messages[i].getMessageHash());
        }
    }

    @Test
    void messageID_created() {
        Message generated = new Message(0, "Developer", "+27718693002", "Hello there");
        assertTrue(generated.checkMessageID());
        assertEquals(10, generated.getMessageID().length());
        assertEquals("Message ID generated: " + generated.getMessageID(), generated.messageIDMessage());
    }

    @Test
    void messageSent_send() {
        assertEquals("Message successfully sent.", message1.sentMessage(1));
    }

    @Test
    void messageSent_disregard() {
        assertEquals("Press 0 to delete the message.", message2.sentMessage(2));
    }

    @Test
    void messageSent_store() {
        assertEquals("Message successfully stored.", message1.sentMessage(3));
    }

    @Test
    void totalMessagesSent_sendOneDiscardOne() {
        message1.sentMessage(1);
        message2.sentMessage(2);
        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    void printMessages_listsSentMessages() {
        message1.sentMessage(1);
        assertTrue(Message.printMessages().contains("00:0:HITONIGHT"));
    }

    @Test
    void storeMessage_writesAndReadsJson(@TempDir Path folder) {
        Path file = folder.resolve("messages.json");
        message1.storeMessage(file);
        message2.storeMessage(file);
        List<Message> loaded = MessageStorage.readAll(file);
        assertEquals(2, loaded.size());
        assertEquals("Hi Mike, can you join us for dinner tonight?", loaded.get(0).getText());
        assertEquals("10:1:HIPAYMENT", loaded.get(1).getMessageHash());
    }
}
