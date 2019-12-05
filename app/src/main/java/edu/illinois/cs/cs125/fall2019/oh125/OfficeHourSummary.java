package edu.illinois.cs.cs125.fall2019.oh125;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

/**
 * This interface defines the behavior of Summary class
 */
public interface OfficeHourSummary {
    /**
     * This method involves i Firebase Firestore request.
     * @return the total number of students present at Office Hour
     */
    public Task<Integer> getTotalStudent();

    /**
     * This method involves in Firebase Firestore request.
     * @return the total number of CA present at Office Hour.
     */
    public Task<Integer> getTotalCA();

    /**
     * This method involves in Firebase Firestore request.
     * @return the total number of TAs present at Office Hour.
     */
    public Task<Integer> getTotalTA();

    /**
     * This method involves in Firestore request and return a list containing the information about
     * current queue.
     * @return an Android task of a list of all QueueInfo items
     */
    public Task<List<QueueInfo>> getQueue();

    /**
     * This method helps create change listener according to user input
     * @param type the number of student/CA/TA you want to register listener for
     * @return a listener for the input type
     */
    public ListenerRegistration getChangeListener(String type);
}
