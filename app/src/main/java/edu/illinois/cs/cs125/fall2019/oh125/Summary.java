package edu.illinois.cs.cs125.fall2019.oh125;

import com.google.android.gms.tasks.Task;

public abstract class Summary {
    /**
     * This method involves i Firebase Firestore request.
     * @return the total number of students present at Office Hour
     */
    public static Task<Integer> getTotalStudent() {
        return null;
    }

    /**
     * This method involves in Firebase Firestore request.
     * @return the total number of CA present at Office Hour.
     */

    public static Task<Integer> getTotalCA() {
        return null;
    }

    /**
     * This method involves in Firebase Firestore request.
     * @return the total number of TAs present at Office Hour.
     */

    public static Task<Integer> getTotalTA() {
        return null;
    }
}
