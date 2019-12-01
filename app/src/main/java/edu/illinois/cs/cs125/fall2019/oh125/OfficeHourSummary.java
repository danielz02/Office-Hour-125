package edu.illinois.cs.cs125.fall2019.oh125;

import com.google.android.gms.tasks.Task;

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
}
