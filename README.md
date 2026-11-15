# QuickChat – Parts 1, 2 and 3 (Final PoE)

## Module
PROG5121 Programming 1A

## Project Overview
QuickChat is a console-based Java chat application. A user registers with their personal details and login credentials, logs in, and then sends, stores or disregards messages. Messages are saved to a JSON file and held in arrays, and the user can search, delete and report on them from a "Stored Messages" menu.

The project was built in three parts:

- **Part 1 – Registration and login:** validation of the username, password and South African cell phone number, followed by login.
- **Part 2 – Sending messages:** a numeric menu, a message loop, message IDs and hashes, send/store/disregard options, and JSON storage.
- **Part 3 – Store data and display report:** arrays for sent, disregarded and stored messages, hashes and IDs, plus the stored-message search, delete and report features.

## Main Features
- Username validation (underscore and no more than five characters).
- Password complexity validation (eight characters, capital letter, number and special character).
- Cell phone validation with a regular expression (international code followed by the number).
- User registration and login, with success and failure messages.
- Welcome message and numeric menu that runs until the user quits.
- The user chooses how many messages to enter, and only that many are accepted.
- Message ID (random ten-digit number) and message hash (for example `00:0:HITONIGHT`).
- Message length check (maximum 250 characters).
- Send, disregard or store each message; total messages sent is displayed.
- Stored messages written to and read from a JSON file.
- Stored Messages menu:
  - Display the sender and recipient of all stored messages.
  - Display the longest stored message.
  - Search for a message ID.
  - Search for all messages for a particular recipient.
  - Delete a message using its hash.
  - Display a report of all stored (or sent) messages.
- JUnit unit tests, run automatically with GitHub Actions.

## Technologies Used
- Java (JDK 21)
- Apache NetBeans
- Maven
- JUnit 5
- Gson (reading and writing JSON)
- Git, GitHub and GitHub Actions

## Project Structure
- `Main.java` – Runs the console application, shows the menus and receives user input.
- `Login.java` – Registration, validation and login methods (Part 1).
- `Message.java` – Message ID, hash, length and recipient checks, send/store/disregard, and the messages sent while the program runs (Part 2).
- `MessageStorage.java` – Writes messages to and reads messages from the JSON file.
- `MessageManager.java` – Holds the arrays and provides the search, delete and report features (Part 3).
- `LoginTest.java` – Unit tests for registration and login.
- `MessageTest.java` – Unit tests for message checks, hashes, IDs, sending and JSON storage.
- `MessageManagerTest.java` – Unit tests for the arrays, searches, delete and reports.
- `pom.xml` – Maven configuration with the JUnit and Gson dependencies.
- `.github/workflows/TestJava.yml` – GitHub Actions workflow that runs the tests on every push.

## How to Run the Application
1. Open the `QuickChat` Maven project in Apache NetBeans.
2. Locate `Main.java` under Source Packages.
3. Run the `Main.java` file.
4. Register, log in, then follow the instructions in the NetBeans Output window.

### Main menu
1. Send Messages
2. Show recently sent messages (displays "Coming Soon.")
3. Quit
4. Stored Messages
5. Load developer test data (demonstration helper that fills the arrays with the five test messages)

Stored messages are saved in `messages.json` in the project folder.

## Testing
JUnit 5 is used to test all three parts. There are 43 unit tests:

- `LoginTest` (18 tests) – valid and invalid usernames, passwords and cell phone numbers, registration messages, and successful and unsuccessful login.
- `MessageTest` (14 tests) – message length (success and failure), recipient number, message hashes (including a loop over all messages), message ID, send/disregard/store messages, total messages sent, and JSON storage.
- `MessageManagerTest` (11 tests) – arrays populated with the test data, longest message, search by message ID and by recipient, delete by hash, reports, and reading the JSON file into an array.

All 43 tests pass with no failures or errors. The tests are also run automatically in GitHub through `TestJava.yml` whenever code is pushed.

To run the tests locally: `mvn test` (or right-click the project in NetBeans and choose **Test**).

## GitHub
- Repository link: *(add your GitHub link here)*
- Video presentation: *(add your unlisted video link here)*

## Notes on the Brief
- The brief uses slightly different wording for the cell phone messages in different sections; `Login` uses the Part 1 test wording and `Message` uses the Part 2 wording.
- Message 4 in the Part 3 test data is treated as a developer entry, with `0838884567` used as its message ID.

## References

- Independent Communications Authority of South Africa (ICASA). (2016). *Numbering Plan Regulations, 2016*. Government Gazette No. 39861. https://www.icasa.org.za/uploads/files/NumberingPlanReg.pdf
- Oracle. (2023). *Java SE 21: Pattern Class*. https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/regex/Pattern.html
- Google. (2023). *Gson User Guide*. https://github.com/google/gson/blob/main/UserGuide.md
- PROG5121 Programming 1A Part 1 Student Guide.
