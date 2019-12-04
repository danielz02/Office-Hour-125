package edu.illinois.cs.cs125.fall2019.oh125;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
     * Add current Student instance's QueueItem instance as an entry to the queue database in Firestore.
     */
    @Override
    public Task<Void> enterQueue() {
        return null;
    }

    /**
     * @return The boolean value indicate whether the current student is in queue.
     */
    public boolean isInQueue() {
        return isInQueue;
    }

    /**
     * Getter for isInQueue.
     * @return a boolean value indicating whether the student is in queue
     */
    public boolean getIsInQueue() {
        return this.isInQueue;
    }

    /**
     * Setter for isInQueue.
     * @param inQueue The new queue status.
     */
    public void setIsInQueue(final boolean inQueue) {
        this.isInQueue = inQueue;
    }

    public Task<Void> updateQueueStatus() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String netId = this.getEmail().split("@")[0];
        final DocumentReference docRef = db.collection("user").document(netId);
        return db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(docRef);
                transaction.update(docRef, "isInQueue", Student.this.isInQueue);
                return null;
            }
        });
    }

    /**
     * Getter for QueueInfo
     * @return the QueueInfo instance of the current student
     */
    QueueInfo getQueueInfo() {
        return this.queueInfo;
    }

    @Override @NonNull
    public String toString() {
        return "Student Name: " + this.getName() + "; Student NetID: " + this.getNetId();
    }

    Task<Void> initializeQueueInfo() {
        return QueueInfo.getInstance(this.getEmail()).addOnCompleteListener(new OnCompleteListener<QueueInfo>() {
            @Override
            public void onComplete(@NonNull Task<QueueInfo> task) {
                if (task.isSuccessful()) {
                    Student.this.queueInfo = task.getResult();
                } else {
                    Log.w("Queue Info Initialization Failed", task.getException());
                }
            }
        }).continueWith(new Continuation<QueueInfo, Void>() {
            @Override
            public Void then(@NonNull Task<QueueInfo> task) throws Exception {
                return null;
            }
        });
    }

}
