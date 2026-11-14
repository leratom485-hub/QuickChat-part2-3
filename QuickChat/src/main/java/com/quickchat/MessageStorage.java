package com.quickchat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads and writes messages to a JSON file using Gson.
 * Reference: Google (2023) Gson User Guide.
 * https://github.com/google/gson/blob/main/UserGuide.md
 * (Replace/extend this reference with the exact source(s) you consulted.)
 */
public class MessageStorage {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type LIST_TYPE = new TypeToken<ArrayList<Message>>() { }.getType();

    private MessageStorage() { }

    /** Reads all messages from the file; an empty list if the file is missing or unreadable. */
    public static ArrayList<Message> readAll(Path file) {
        if (!Files.exists(file)) {
            return new ArrayList<>();
        }
        try {
            String json = Files.readString(file);
            ArrayList<Message> messages = GSON.fromJson(json, LIST_TYPE);
            return messages == null ? new ArrayList<>() : messages;
        } catch (IOException | JsonSyntaxException e) {
            System.out.println("Could not read " + file + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /** Replaces the file contents with the given messages. */
    public static void writeAll(Path file, List<Message> messages) {
        try {
            Files.writeString(file, GSON.toJson(messages, LIST_TYPE));
        } catch (IOException e) {
            System.out.println("Could not write " + file + ": " + e.getMessage());
        }
    }

    /** Adds one message to the file. */
    public static void append(Path file, Message message) {
        ArrayList<Message> messages = readAll(file);
        messages.add(message);
        writeAll(file, messages);
    }
}
