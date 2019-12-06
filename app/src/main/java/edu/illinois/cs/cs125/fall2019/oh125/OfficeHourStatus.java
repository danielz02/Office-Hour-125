package edu.illinois.cs.cs125.fall2019.oh125;

import com.google.android.gms.tasks.Task;

import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;

/**
 * This interface defines a part of the behaviors of Student instances.
 */
interface SendQueue {
    /**
     * Add current Student instance's QueueItem instance as an entry to the queue database in Firestore.
     * @param category The category of students' question, either being MP or Homework
     * @param estimatedTime The estimated time of the current session, in minutes
     * @param table The number of student's current table
     * @return An Android task of Void type
     * @throws IllegalArgumentException when category is not either MP or Homework
     * @throws FileAlreadyExistsException when current student already has an entry of QueueInfo
     */
    public Task<Void> enterQueue(String category,
                                 int estimatedTime,
                                 int table) throws IllegalArgumentException, FileAlreadyExistsException;

    /**
     * Delete current student's record in the queue
     * @return An Android task of type Void
     * @throws FileNotFoundException when current user doesn't even have a QueueInfo instance
     */
    public Task<Void> exitQueue() throws FileNotFoundException;
}

/**
 * This interface defines course staff's behaviors regarding queue management.
 */
interface ManageQueue {
    /**
     * This method will send request to Firestore to obtain all Student instances in a List.
     * Please resister a listen for callback after the web request is complete.
     * @return an asynchronous operation which obtains Student instances in queue database
     */
    public Task<List<Student>> getQueue();

    /**
     * End one student's queue status.
     * Used at the end of one CA session.
     * @param student the Student instance to be kicked out from the queue
     */
    public Task<Void> endQueue(Student student);
}

/**
 * This interfaces define the behavior regarding the presence in office hour
 * of any instance of Family 125.
 */
interface OfficeHourStatus {
    /**
     * This method will update the Firestore database according to current instance's isAtOfficeHour
     * @return an Android Task of Void type
     */
    public Task<Void> updateOfficeHourStatus();
}