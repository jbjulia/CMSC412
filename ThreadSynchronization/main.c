/*
The purpose of this code is to demonstrate thread synchronization by
creating three (3) new threads and printing their thread-id, in turn,
for five (5) iterations. Sleep() is used to demonstrate thread creation.
*/

#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

int thread;
pthread_t threads[3]; // Initialize thread array
pthread_mutex_t mutex;

void * printThread() {
  pthread_mutex_lock( & mutex); // Lock code

  pthread_t tid = pthread_self(); // Get thread

  printf("\nThread %d (thread-id: %ld) created successfully.", thread + 1,
    tid); // Print thread creation, account for zero index

  pthread_mutex_unlock( & mutex); // Unlock code

  return NULL;
}

int main(void) {
  int iteration;
  int error;

  if (pthread_mutex_init( & mutex, NULL) != 0) {
    printf("\n Error: pthread_mutex_init failure. [%s]\n", strerror(error));
    return 1;
  } // Catch and print pthread_mutex_init error

  printf("Begin multithreading...\n");

  /* Create and print three (3) threads by thread-id */
  for (thread = 0; thread < 3; thread++) {
    error = pthread_create( & (threads[thread]), NULL, & printThread, NULL); // Create new thread
    sleep(1); // Necessary to ensure proper order
    if (error != 0)
      printf("\nError: pthread_create failure. [%s]\n", strerror(error));
  } // Catch and print pthread_create error

  /* Synchronize threads */
  pthread_join(threads[0], NULL);
  pthread_join(threads[1], NULL);
  pthread_join(threads[2], NULL);

  /* Print three (3) threads, in turn, for five (5) iterations */
  for (iteration = 0; iteration < 5; iteration++) {
    printf("\n\n\tIteration: %d of 5 - \n", iteration + 1); // Account for zero index
    for (thread = 0; thread < 3; thread++) {
      printf("\n\t\tThread %d (thread-id: %ld)", thread + 1,
        threads[thread]); // Print threads in turn, account for zero index
      sleep(1); // Sleep to simulate thread step
    }
  }

  printf("\n\nMultithreading complete.\n");

  /* Cleanup mutex and threads */
  pthread_mutex_destroy( & mutex);
  pthread_exit( & threads);

  return 0;
}