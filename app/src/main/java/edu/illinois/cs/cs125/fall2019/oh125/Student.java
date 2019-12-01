package edu.illinois.cs.cs125.fall2019.oh125;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

public class Student extends Family125 implements SendQueue {
    /** The boolean value indicating whether a student is in queue. */
    private boolean isInQueue;

    /** An instance of the inner class storing student's information about queue. */
    private QueueInfo queueInfo;

    private class QueueInfo {
        private String category;
        int estimatedTime;
        int tableNumber;
        int timeEntered;
    }

    /**
     * Add current Student instance's QueueItem instance as an entry to the queue database in Firestore.
     */
    @Override
    public void enterQueue() {

    }

    /**
     * An empty constructor for Firebase Firestore.
     */
    public Student() { }

    /**
     * The
     * @param name The name of the student.
     * @param role The role of the student. In this case it must be a student.
     * @param email The email of the student as a String.
     * @param isAtOfficeHour he boolean expression indicating whether the student is at office hour.
     * @param isInQueue he boolean expression indicating whether the student is in queue.
     */
    public Student(String name, String role, String email, boolean isAtOfficeHour, boolean isInQueue) {
        super(name, role, email, isAtOfficeHour);
        this.isInQueue = isInQueue;
    }

    /**
     * @return The boolean value indicate whether the current student is in queue.
     */
    public boolean isInQueue() {
        return isInQueue;
    }

    /**
     *
     * @param inQueue The new queue status.
     */
    public void setIsInQueue(final boolean inQueue) {
        isInQueue = inQueue;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String netId = this.getEmail().split("@")[0];
        final DocumentReference docRef = db.collection("user").document(netId);
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(docRef);
                transaction.update(docRef, "isInQueue", inQueue);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Firebase Update", "Transaction succeed!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Firebase Update", e);
            }
        });
    }

    @Override @NonNull
    public String toString() {
        return "Student Name: " + this.getName() + "; Email: " + this.getEmail();
    }

}
