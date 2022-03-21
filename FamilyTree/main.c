#include <stdio.h>

#include <unistd.h>

int main() {

  pid_t pid, grandparent; // Initialize pid and grandparent process (G)

  grandparent = getpid(); // Get pid for grandparent (G)
  pid = fork(); // Create parent process (P) from grandparent process (G)

  switch (pid) {
  case 0:
    pid = fork(); // Create child process (P) from parent process (P)
    switch (pid) {
    case 0:
      printf("I am the child process C and my pid is %d. My parent P has pid %d. My grandparent G has pid %d.\n\n", getpid(), getppid(), grandparent); // Print pid for child process (C), parent process (P), and grandparent process (G)
      break;
    default:
      wait(NULL); // Wait until parent process (P) finishes execution
      printf("I am the parent process P and my pid is %d. My parent G has pid %d.\n\n", getpid(), getppid()); // Print pid for parent process (P) and grandparent process (G)
      break;
    }
    break;
  default:
    wait(NULL); // Wait unitl child process (P) finishes execution
    printf("I am the grandparent process G and my pid is %d\n\n", grandparent); // Print pid for grandparent process (G)
    break;
  }
}