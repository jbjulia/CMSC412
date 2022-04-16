import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;

/*
Reads local file defining resources and processes, then calculates and
prints solution using the Banker's Algorithm, backtracking to ensure
all possible solutions were found. A single test case is provided for
this exercise, however, new test cases may be added locally.
*/
class Main {
  public static final int MAX_PROCESSES = 9; // Defined by assignment (N < 10)
  public static final int MAX_RESOURCES = 9; // Defined by assignment (M < 10)
  static int allSolutions = 0;

  public static void main(String[] args) {
    String fileName, line;

    Scanner console = new Scanner(System.in); // Initialize Scanner to read user input
    System.out.printf("Please enter the name of the file you wish to use (e.g. test.txt): ");
    fileName = console.nextLine(); // Get user input from console
    console.close(); // Close scanner

    FileReader f = null; // Initialize FileReader

    try {
      f = new FileReader(fileName); // Attempt to ingest file
    } catch (FileNotFoundException e) {
      System.out.println(e); // Catch and print exception upon failure
    }

    Scanner file = new Scanner(f); // Initialize scanner to read file
    int[] resourceArray = new int[MAX_RESOURCES]; // Initialize resourceArray to MAX_RESOURCES
    int currentResource = 0; // Set current resource to 0
    Process[] processArray = new Process[MAX_PROCESSES]; // Initialize processArray to MAX_PROCESSES
    int processNo = 0; // Initialize process number to 0

    while (file.hasNextLine()) {
      line = file.nextLine(); // Read each line in file
      if ((line.length() > 0) && (line.charAt(0) == 'R')) {
        line = line.substring(4); // Recognize file format (R = resource)
        if (line.charAt(0) == ' ') {
          line = line.substring(1);
        }
        resourceArray[currentResource] = Integer.parseInt(line); // Populate resourceArray from file
        currentResource += 1;
      }
      if ((line.length() > 0) && (line.charAt(0) == 'P')) {
        line = line.replaceAll("[^\\d]", " "); // Remove unnecessary characters
        int[] pHeld = new int[MAX_RESOURCES];
        int[] pMax = new int[MAX_RESOURCES];
        Scanner p = new Scanner(line); // Initialize Scanner for next section of file
        p.nextInt(); // Remove unnecessary characters
        for (int i = 0; i < currentResource; i++) {
          pHeld[i] = p.nextInt();
        }
        for (int i = 0; i < currentResource; i++) {
          pMax[i] = p.nextInt();
        }
        processArray[processNo] = new Process(pHeld, pMax, processNo, currentResource);
        processNo += 1;

      }
    }

    file.close(); // Close file

    // Calculate starting resources
    int[] currentResourceArray = resourceArray.clone();
    for (int i = 0; i < processNo; i++) {
      int[] pa = processArray[i].getHeldResources();
      for (int j = 0; j < currentResource; j++) {
        currentResourceArray[j] -= pa[j];
      }
    }

    // Create ArrayLists for the backtracking
    ArrayList < Process > processList = new ArrayList < Process > ();
    ArrayList < Process > hist = new ArrayList < Process > ();
    for (int i = 0; i < processNo; i++) {
      processList.add(processArray[i]);
    }

    // Calculate all solutions
    System.out.println("\nSolutions: \n");
    backtrack(currentResourceArray, processList, hist); // Backtrack through algorithm to calculate all possible solutions

    if (allSolutions == 0) {
      System.out.println("\nUh-oh. No solutions were found.");
    } else {
      System.out.println("\n" + Integer.toString(allSolutions) + " solution" + (allSolutions > 1 ? "s" : "") + " found."); // Print total number of solutions, append 's' if more than one
    }
  }

  static void backtrack(int[] currentResources, ArrayList < Process > processes, ArrayList < Process > hist) {
    for (Process p: processes) {
      if (p.canRun(currentResources) && (processes.size() > 1)) { // If the process can run and there are other processes on the list that need to be run
        int[] newResources = currentResources.clone();
        int[] proccessResources = p.getHeldResources();
        ArrayList < Process > newHist = new ArrayList < Process > (hist);
        newHist.add(p);
        ArrayList < Process > newProcesses = new ArrayList < Process > (processes);
        newProcesses.remove(p);
        for (int i = 0; i < currentResources.length; i++) {
          newResources[i] = currentResources[i] + proccessResources[i];
        }
        backtrack(newResources, newProcesses, newHist);
      } else if (p.canRun(currentResources)) { // Else this process can run and is last on the list
        hist.add(p);
        allSolutions += 1;
        for (Process proc: hist) {
          System.out.printf("P" + Integer.toString(proc.getNameAsInt() + 1) + " -> ");
        }
        System.out.printf("Done.");
        System.out.println();
      }
    }
  }
}
