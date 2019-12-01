package edu.illinois.cs.cs125.fall2019.oh125;

import com.google.android.gms.tasks.Task;

import java.util.List;

/**
 * This interface defines a part of the behaviors of Student instances.
 */
interface SendQueue {
    /**
     * Add current Student instance's QueueItem instance as an entry to the queue database in Firestore.
     */
    public void enterQueue();
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
    public void endQueue(Student student);

}
