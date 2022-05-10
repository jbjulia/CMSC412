import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static final String ANSI_FLUSH = "\033[H\033[2J";
    public static final String ANSI_BOLD = "\033[1m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    static final int MIN_VIRTUAL_FRAME = 0;
    static final int MAX_VIRTUAL_FRAME = 9;
    static final int MIN_PHYSICAL_FRAME = 1;
    static final int MAX_PHYSICAL_FRAME = 8;

    static ArrayList < Integer > referenceString = new ArrayList < > ();

    /**
     * Displays menu options for user to select from.
     */
    private static void mainMenu() {
        String mainMenu = "Main Menu";

        System.out.println(mainMenu + "\n" + "-".repeat(mainMenu.length()) + "\n");
        System.out.println("0 - Exit");
        System.out.println("1 - Read reference string");
        System.out.println("2 - Generate reference string");
        System.out.println("3 - Display current reference string");
        System.out.println("4 - Simulate FIFO");
        System.out.println("5 - Simulate OPT");
        System.out.println("6 - Simulate LRU");
        System.out.println("7 - Simulate LFU");
        System.out.print(ANSI_BOLD + "\nPlease select from the above menu options (default 0): " + ANSI_RESET);
    }

    private static void processInput(int option, Scanner input) throws IOException {
        switch (option) {
            case 0:
                exitProgram(); // Prints exit message and ends program
                break;
            case 1:
                referenceString = getReferenceString(input); // Read reference string
                break;
            case 2:
                referenceString = generateReferenceString(input); // Generate reference string
                break;
            case 3:
                if (referenceString.isEmpty()) {
                    System.out.println("\nUh-oh. No reference string set."); // Warn user null reference string
                    break;
                }
                System.out.println("\nCurrently stored reference string:\n");
                System.out.println(referenceString); // Display current reference string
                break;
            case 4:
                simulateFifo(input, referenceString); // Simulate FIFO
                break;
            case 5:
                simulateOpt(input, referenceString); // Simulate OPT
                break;
            case 6:
                simulateLru(input, referenceString); // Simulate LRU
                break;
            case 7:
                simulateLfu(input, referenceString); // Simulate LFU
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
     * A reference string will be read from the keyboard and stored in a buffer. Each value of
     * the reference string will be verified and validated (or rejected).
     *
     * Using option 1 again will result in overwriting the old reference string.
     */
    private static ArrayList < Integer > getReferenceString(Scanner input) {
        System.out.println(
            "\nPlease enter a reference string [" +
            MIN_VIRTUAL_FRAME +
            " - " +
            MAX_VIRTUAL_FRAME +
            "]:\n"
        );

        String[] string = input.nextLine().split("\\s+");
        ArrayList < Integer > refString = new ArrayList < > ();
        int temp;

        for (int i = 0; i < string.length; i++) {
            try {
                temp = Integer.parseInt(string[i]);
            } catch (NumberFormatException e) {
                System.out.println("\nUh-oh. Reference string must only contain numbers.");
                System.out.println(string[i] + " is not a number.");
                return null;
            }
            if (temp > MAX_VIRTUAL_FRAME || temp < MIN_VIRTUAL_FRAME) {
                System.out.println(string[i] + " is not within the required range.");
                return getReferenceString(input);
            }
            refString.add(i, temp);
        }

        return refString;
    }

    /**
     * A reference string will be randomly generated; the length of the reference string will be 
     * given by the user interactively. The string will be stored in a buffer.
     *
     * Using option 2 more than once will result in overwriting the old reference string.
     */
    private static ArrayList < Integer > generateReferenceString(Scanner input) {
        System.out.println("\nPlease enter the desired string length: ");

        String lengthString = input.nextLine();
        int length = Integer.parseInt(lengthString);
        ArrayList < Integer > referenceString = new ArrayList < > ();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            referenceString
                .add(i, random.nextInt((MAX_VIRTUAL_FRAME - MIN_VIRTUAL_FRAME) + 1) + MIN_VIRTUAL_FRAME);
        }

        System.out.println("\nReference string successfully generated!");
        System.out.println(referenceString);

        return referenceString;
    }

    /**
     * Will display the stored reference string; if there is no reference string stored yet, an
     * error message will be displayed.
     */
    private static String[][] generateTable(ArrayList < Integer > referenceString, int frames) {
        String[][] table = new String[frames + 3][referenceString.size() + 1];
        table[0][0] = "Reference String";

        for (int i = 1; i < frames; i++) {
            table[1][0] = "Physical Frame " + (i - 1);
        }

        table[frames + 1][0] = "Page Faults";
        table[frames + 2][0] = "Victim Frames";

        for (int i = 0; i < referenceString.size(); i++) {
            table[0][i + 1] = String.valueOf(referenceString.get(i));
        }

        return table;
    }

    private static void printTable(String[][] table) {
        for (String[] row: table) {
            for (int col = 0; col < row.length; col++) {
                if (col == 0) {
                    System.out.printf("%-18s", row[0]);
                } else {
                    if (row[col] == null) {
                        System.out.printf("%4s", " ");
                    } else {
                        System.out.printf("%4s", row[col]);
                    }
                }
            }
            System.out.println();
        }
    }

    /**
     * Will simulate the step by step execution of the FIFO algorithm using the stored reference 
     * string; if there is no reference string stored yet, an error message must be displayed.
     *
     * The user will press a key after each step of the simulation to continue the simulation.
     * The total number of faults will be displayed at the end of the simulation.
     */
    private static void simulateFifo(Scanner input, ArrayList < Integer > referenceString) {
        System.out.println(
            "\nPlease enter the number of physical frames [" +
            MIN_PHYSICAL_FRAME +
            " - " +
            MAX_PHYSICAL_FRAME +
            "]:\n"
        );

        int frames = Integer.parseInt(input.nextLine());
        ArrayList < Integer > memory = new ArrayList < > (frames);
        String[][] table = generateTable(referenceString, frames);

        int victim = -1;
        boolean fault;
        int currentFrame = 0;
        int faultCount = 0;

        System.out.println(ANSI_BOLD + "\nStarting FIFO" + ANSI_RESET);
        printTable(table);
        System.out.println("\nPress ENTER to continue...");

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < referenceString.size(); i++) {
            if (!memory.contains(referenceString.get(i))) {
                if (memory.size() < frames) {
                    memory.add(currentFrame, referenceString.get(i));
                    ++currentFrame;
                    fault = true;
                    faultCount++;
                } else {
                    if (currentFrame >= frames) {
                        currentFrame = 0;
                    }
                    fault = true;
                    faultCount++;
                    victim = memory.get(currentFrame);
                    memory.set(currentFrame, referenceString.get(i));
                    ++currentFrame;
                }
            } else {
                fault = false;
            }
            for (int j = 0; j < memory.size(); ++j) {
                table[j + 1][i + 1] = String.valueOf(memory.get(j));
            }
            if (fault) {
                table[frames + 1][i + 1] = "F";
                if (victim != -1) {
                    table[frames + 2][i + 1] = String.valueOf(victim);
                }
            }
            System.out.println("\nCurrent Table\n");
            printTable(table);
            System.out.println("\nPlease press ENTER to continue...");
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\nA total of " + faultCount + " faults occurred.");
    }

    /**
     * Will simulate the step by step execution of the OPT algorithm using the stored reference 
     * string; if there is no reference string stored yet, an error message must be displayed
     *
     * The user will press a key after each step of the simulation to continue the simulation.
     * The total number of faults will be displayed at the end of the simulation.
     */
    private static void simulateOpt(Scanner input, ArrayList < Integer > referenceString) {
        System.out.println(
            "\nPlease enter the number of physical frames [" +
            MIN_PHYSICAL_FRAME +
            " - " +
            MAX_PHYSICAL_FRAME +
            "]:\n"
        );

        int frames = Integer.parseInt(input.nextLine());
        ArrayList < Integer > memory = new ArrayList < > (frames);
        ArrayList < Integer > refList = new ArrayList < > ();

        for (int i: referenceString)
            refList.add(i); // Create a reference list to search future

        String[][] table = generateTable(referenceString, frames);

        int victim = -1;
        boolean fault;
        int currentFrame = 0;
        int faultCount = 0;
        int max = -1;
        int index;

        System.out.println(ANSI_BOLD + "\nStarting OPT\n" + ANSI_RESET);
        printTable(table);
        System.out.print("\nPlease press ENTER to continue...");

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < referenceString.size(); i++) {
            if (!memory.contains(referenceString.get(i))) {
                if (memory.size() < frames) {
                    memory.add(currentFrame, referenceString.get(i));
                    refList.remove((Integer) referenceString.get(i));
                    ++currentFrame;
                    fault = true;
                    faultCount++;
                } else {
                    fault = true;
                    faultCount++;
                    int temp = refList.get(0);
                    refList.remove(0);
                    for (int m: memory) {
                        index = refList.indexOf(m);
                        if (index == -1) {
                            victim = m;
                            break;
                        }
                        if (index > max) {
                            victim = m;
                            max = index;
                        }
                    }
                    memory.set(memory.indexOf(victim), temp);
                    max = -1;
                }
            } else {
                fault = false;
                refList.remove(0);
            }
            for (int j = 0; j < memory.size(); ++j) {
                table[j + 1][i + 1] = String.valueOf(memory.get(j));
            }
            if (fault) {
                table[frames + 1][i + 1] = "F";
                if (victim != -1)
                    table[frames + 2][i + 1] = String.valueOf(victim);
            }
            printTable(table);
            System.out.print("\nPlease press ENTER to continue...");
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\nA total of " + faultCount + " faults occurred.");
    }

    /**
     * Will simulate the step by step execution of the LRU algorithm using the stored reference 
     * string; if there is no reference string stored yet, an error message must be displayed.
     *
     * The user will press a key after each step of the simulation to continue the simulation.
     * The total number of faults will be displayed at the end of the simulation.
     */
    private static void simulateLru(Scanner input, ArrayList < Integer > referenceString) {
        System.out.println(
            "\nPlease enter the number of physical frames [" +
            MIN_PHYSICAL_FRAME +
            " - " +
            MAX_PHYSICAL_FRAME +
            "]:\n"
        );

        int frames = Integer.parseInt(input.nextLine());
        ArrayList < Integer > memory = new ArrayList < > (frames);
        int[] lruCount = new int[frames];
        String[][] table = generateTable(referenceString, frames);

        int victim = -1;
        boolean fault;
        int currentFrame = 0;
        int faultCount = 0;
        int max;
        int index;

        System.out.println(ANSI_BOLD + "\nStarting LRU\n" + ANSI_RESET);
        printTable(table);
        System.out.print("\nPlease press ENTER to continue...");

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < referenceString.size(); ++i) {
            if (!memory.contains(referenceString.get(i))) {
                if (memory.size() < frames) {
                    memory.add(currentFrame, referenceString.get(i));
                    for (int j = 0; j < lruCount.length; ++j) {
                        lruCount[j]++;
                    }
                    lruCount[currentFrame] = 1;
                    ++currentFrame;
                    fault = true;
                    faultCount++;
                } else {
                    max = -1;
                    index = 0;
                    fault = true;
                    faultCount++;
                    for (int j = 0; j < lruCount.length; ++j) {
                        if (lruCount[j] > max) {
                            max = lruCount[j];
                            index = j;
                        }
                    }
                    victim = memory.get(index);
                    memory.set(index, referenceString.get(i));
                    for (int j = 0; j < lruCount.length; ++j) {
                        lruCount[j]++;
                    }
                    lruCount[memory.indexOf(referenceString.get(i))] = 1;
                }
            } else {
                fault = false;
                for (int j = 0; j < lruCount.length; ++j) {
                    lruCount[j]++;
                }
                lruCount[memory
                    .indexOf(referenceString.get(i))] = 1;
            }
            for (int j = 0; j < memory.size(); ++j) {
                table[j + 1][i + 1] = String.valueOf(memory.get(j));
            }
            if (fault) {
                table[frames + 1][i + 1] = "F";
                if (victim != -1) {
                    table[frames + 2][i + 1] = String.valueOf(victim);
                }
            }
            printTable(table);
            System.out.print("\nPlease press ENTER to continue...");
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\nA total of " + faultCount + " occurred.");
    }

    /**
     * Will simulate the step by step execution of the LFU algorithm using the stored reference 
     * string; if there is no reference string stored yet, an error message must be displayed.
     *
     * The user will press a key after each step of the simulation to continue the simulation.
     * The total number of faults will be displayed at the end of the simulation.
     */
    private static void simulateLfu(Scanner input, ArrayList < Integer > referenceString) {
        System.out.println(
            "\nPlease enter the number of physical frames [" +
            MIN_PHYSICAL_FRAME +
            " - " +
            MAX_PHYSICAL_FRAME +
            "]:\n"
        );

        int frames = Integer.parseInt(input.nextLine());
        ArrayList < Integer > memory = new ArrayList < > (frames);
        HashMap < Integer, Integer > lfuCount = new HashMap < > ();
        String[][] table = generateTable(referenceString, frames);

        int victim = -1;
        boolean fault;
        int currentFrame = 0;
        int faultCount = 0;
        int min;
        int index;
        int count;

        System.out.println(ANSI_BOLD + "\nStarting LFU\n" + ANSI_RESET);
        printTable(table);
        System.out.println("\nPlease press ENTER to continue...");

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < referenceString.size(); ++i) {
            if (!memory.contains(referenceString.get(i))) {
                if (memory.size() < frames) {
                    memory.add(currentFrame, referenceString.get(i));
                    lfuCount.put(referenceString.get(i), 1);
                    ++currentFrame;
                    fault = true;
                    faultCount++;
                } else {
                    min = lfuCount.get(memory.get(0));
                    index = 0;
                    fault = true;
                    faultCount++;
                    for (int j = 0; j < memory.size(); ++j) {
                        if (lfuCount.get(memory.get(j)) < min) {
                            min = lfuCount.get(memory.get(j));
                            index = j;
                        }
                    }
                    victim = memory.get(index);
                    memory.set(index, referenceString.get(i));
                    if (lfuCount.containsKey(referenceString.get(i))) {
                        count = lfuCount.get(referenceString.get(i));
                        count++;
                        lfuCount.put(referenceString.get(i), count);
                    } else {
                        lfuCount.put(referenceString.get(i), 1);
                    }
                }
            } else {
                fault = false;
                count = lfuCount.get(referenceString.get(i));
                count++;
                lfuCount.put(referenceString.get(i), count);
            }
            for (int j = 0; j < memory.size(); ++j) {
                table[j + 1][i + 1] = String.valueOf(memory.get(j));
            }
            if (fault) {
                table[frames + 1][i + 1] = "F";
                if (victim != -1) {
                    table[frames + 2][i + 1] = String.valueOf(victim);
                }
            }
            System.out.println("\nCurrent Table\n");
            printTable(table);
            System.out.print("\nPlease press ENTER to continue...");
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\nA total of " + faultCount + " faults occurred.");
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