import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    private static Path directoryPath = null;
    public static final String ANSI_FLUSH = "\033[H\033[2J";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    /**
     * Displays menu options for user to select from.
     */
    private static void mainMenu() {
        String mainMenu = "Main Menu";

        System.out.println(mainMenu + "\n" + "-".repeat(mainMenu.length()) + "\n");
        System.out.println("0 - Exit");
        System.out.println("1 - Select directory");
        System.out.println("2 - List directory content (first level)");
        System.out.println("3 - List directory content (all levels)");
        System.out.println("4 - Delete file");
        System.out.println("5 - Display file (hexadecimal view)");
        System.out.println("6 - Encrypt file (XOR with password)");
        System.out.println("7 - Decrypt file (XOR with password)");
        System.out.print("\n\033[1mPlease select from the above menu options (default 0): \033[0m\n");
    }

    /**
     * Takes user's selection and calls the corresponding method. Default is 0 (system exit).
     */
    private static void processInput(int option, Scanner input) throws IOException {
        switch (option) {
            case 0:
                exitProgram(); // Prints exit message and ends program
                break;
            case 1:
                selectDirectory(input); // Prompts user to select directory
                break;
            case 2:
                try {
                    listDirectory(); // Lists content of selected directory (first level)
                } catch (IOException e) {
                    e.printStackTrace(); // Catches IOException and prints error message
                }
                break;
            case 3:
                listDirectoryRecursive(); // // Lists content of selected directory (all levels)
                break;
            case 4:
                deleteFile(input); // Deletes selected file
                break;
            case 5:
                displayFileHex(input); // Displays selected file content in hexadecimal
                break;
            case 6:
                fileBytes(input, "encrypt"); // Encrypts selected file (XOR with password)
                break;
            case 7:
                fileBytes(input, "decrypt"); // Decrypts selected file (XOR with password)
                break;
            default:
                exitProgram(); // Prints exit message and ends program
        }
    }

    /**
     * Prints exit message and ends program. Exit message is preceded by "-" multiplied by string length.
     */
    private static void exitProgram() {
        String exitMessage = "Thank you for using, goodbye!";

        System.out.println("\n" + "-".repeat(exitMessage.length()) + "\n" + exitMessage + "\n");
        System.exit(0);
    }

    /**
     * The user is prompted for a directory [absolute] name. This is the first options that must be 
     * selected by the user. All the options below are working on the directory selected here. After 
     * performing several operations on the selected directory, the user can select another directory
     * and work with it.
     */
    private static void selectDirectory(Scanner input) {
        System.out.print("\nPlease enter a directory path (e.g. documents): ");
        String inputPath = input.nextLine();

        while (!Files.exists(Paths.get(inputPath))) {
            System.out.println(ANSI_RED + "\nUh-oh. " + inputPath + " does not exist." + ANSI_RESET);
            System.out.print(ANSI_YELLOW + "\nPlease enter a correct directory path: " + ANSI_RESET);
            inputPath = input.nextLine();
        }

        if (!(inputPath == "")) {
            directoryPath = Paths.get(inputPath);
            System.out.println(ANSI_GREEN + "\nSuccess! Directory path set to: " + directoryPath.toString() + ANSI_RESET);
        } else {
            System.out.println(ANSI_RED + "\nUh-oh. You did not enter anything." + ANSI_RESET);
        }
    }

    /**
     * This option displays the content of the selected directory on the screen. All the files and sub
     * directories from the first level must be displayed (files and directories should be listed
     * separately). If no directory was selected an error message must be displayed.
     */
    private static void listDirectory() throws IOException {
        if (!(directoryPath == null)) {
            System.out.println("\nContents of current directory (first level):\n");
            Files.list(new File(directoryPath.toString()).toPath())
                .forEach(System.out::println);
        } else {
            System.out.println(ANSI_YELLOW + "\nYou must first select a directory." + ANSI_RESET);
        }
    }

    /**
     * This option displays the content of the selected directory on the screen. All the files and sub
     * directories from the first and subsequent levels must be displayed (files and directories should
     * be listed separately). If no directory was selected an error message must be displayed.
     */
    private static void listDirectoryRecursive() throws IOException {
        if (!(directoryPath == null)) {
            System.out.println("\nContents of current directory (all levels):\n");
            Files.walk(Paths.get(String.valueOf(directoryPath)))
                .filter(Files::isRegularFile)
                .forEach(System.out::println);
        } else {
            System.out.println(ANSI_YELLOW + "\nYou must first select a directory." + ANSI_RESET);
        }
    }

    /**
     * This option prompts the user for a filename and deletes that file from the selected directory. If 
     * no directory was selected an error message must be displayed. If the directory does not contain 
     * the file specified by the user, an error message must be displayed. The filename does not 
     * include any path, it’s just the name of the file.
     */
    private static void deleteFile(Scanner input) {
        if (directoryPath == null) {
            System.out.println(ANSI_YELLOW + "\nYou must first select a directory." + ANSI_RESET);
            return;
        }

        System.out.print("\nPlease enter filename: " + directoryPath.toString() + "/");
        String filePath = directoryPath + "/" + input.nextLine();

        try {
            Files.delete(Paths.get(filePath));
            System.out.println(ANSI_GREEN + "\nSuccess! " + filePath + " has been deleted." + ANSI_RESET);
        } catch (NoSuchFileException x) {
            System.out.println(ANSI_RED + "\nUh-oh. File not found." + ANSI_RESET);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This option prompts the user for a filename (from the selected directory) and displays the 
     * content of that file on the screen, in hexadecimal view. If no directory was selected an error 
     * message must be displayed. If the directory does not contain the file specified by the user, an 
     * error message must be displayed. The filename does not include any path, it’s just the name of
     * the file.
     */
    private static void displayFileHex(Scanner input) {
        if (directoryPath == null) {
            System.out.println(ANSI_YELLOW + "\nYou must first select a directory." + ANSI_RESET);
            return;
        }

        System.out.print("\nPlease enter filename: " + directoryPath.toString() + "/");
        String filePath = directoryPath + "/" + input.nextLine();
        System.out.println("\nContents of " + directoryPath.toString() + " in hexadecimal view:\n");

        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            int i = 0;
            int count = 0;
            while ((i = fileInputStream.read()) != -1) {
                System.out.printf("%02x ", i);
                count++;
                if (count == 16) {
                    System.out.println("");
                    count = 0;
                }
            }
            System.out.println("");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        };
    }

    /**
     * Prepares for file encryption/decryption by obtaining file size (bytes) and password.
     */
    private static void fileBytes(Scanner input, String action) {
        if (directoryPath == null) {
            System.out.println(ANSI_YELLOW + "\nYou must first select a directory." + ANSI_RESET);
            return;
        }

        System.out.print("\nPlease enter filename: " + directoryPath.toString() + "/");
        String fileName = input.nextLine();
        String filePath = directoryPath + "/" + fileName;
        System.out.print("\nPlease enter password: ");
        String password = input.nextLine();

        if (password.getBytes().length > 256) {
            System.out.println(ANSI_YELLOW + "\nUh-oh. Password is too long." + ANSI_RESET);
        }

        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            byte[] passwordBytes = password.getBytes();
            if (action == "encrypt") {
                encryptFile(fileBytes, passwordBytes, fileName);
            } else {
                decryptFile(fileBytes, passwordBytes, fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This option prompts the user for a password (max 256 bytes long, may contain letters, digits, 
     * other characters) and then prompts the user for a filename and encrypts the content of the 
     * selected file using that password. The encryption method is very simple: just XOR the 
     * password with the file content byte after byte; the password being shorter than the file content, 
     * you must repeat the password as needed.
     */
    private static void encryptFile(byte[] fileBytes, byte[] passwordBytes, String fileName) {
        try {
            int j = 0;
            for (int i = 0; i < fileBytes.length; i++) {
                if (j > passwordBytes.length - 1) {
                    j = 0;
                }
                fileBytes[i] = (byte)(fileBytes[i] ^ passwordBytes[j]);
                j++;
            }
            File outputFile = new File(directoryPath + "/" + fileName);
            FileOutputStream stream = new FileOutputStream(outputFile); // Creates new file
            try {
                stream.write(fileBytes);
                System.out.println(ANSI_GREEN + "\nFile successfully encrypted." + ANSI_RESET);
            } finally {
                stream.close();
            }
        } catch (IOException e) {
            System.out.println(ANSI_RED + "\nUh-oh. Your file was not found." + ANSI_RESET);
            return;
        }
    }

    /**
     * This option prompts the user for a password (max 256 bytes long, may contain letters, digits, 
     * other characters) and then prompts the user for a filename and decrypts the content of the 
     * selected file using that password. The decryption method is very simple: just XOR the 
     * password with the file content byte after byte; the password being shorter than the file content, 
     * you must repeat the password as needed.
     */
    private static void decryptFile(byte[] fileBytes, byte[] passwordBytes, String fileName) {
        try {
            int j = 0;
            for (int i = 0; i < fileBytes.length; i++) {
                if (j > passwordBytes.length - 1) {
                    j = 0;
                }
                fileBytes[i] = (byte)(passwordBytes[j] ^ fileBytes[i]);
                j++;
            }
            File outputFile = new File(directoryPath + "/" + fileName);
            FileOutputStream stream = new FileOutputStream(outputFile);
            try {
                stream.write(fileBytes);
                System.out.println(ANSI_GREEN + "\nFile successfully decrypted." + ANSI_RESET);
            } finally {
                stream.close();
            }
        } catch (IOException e) {
            System.out.println(ANSI_RED + "\nUh-oh. Your file was not found." + ANSI_RESET);
        }
    }

    /**
     * The menu is displayed and the user must select an option (a number between 0 and 7). The 
     * action corresponding to the selection is performed, then the menu is displayed again and the 
     * user can choose another option. This cycle is repeated until the user selects 0, which exits the 
     * loop and ends the program.
     */
    public static void main(String[] args) throws IOException {
        Scanner optionSelection = new Scanner(System.in);

        System.out.print(ANSI_FLUSH);
        System.out.flush();
        mainMenu();

        while (true) {
            System.out.print("\n\nOption (e.g. 1): ");
            processInput(Integer.parseInt(optionSelection.nextLine().trim()), optionSelection);
        }
    }
}