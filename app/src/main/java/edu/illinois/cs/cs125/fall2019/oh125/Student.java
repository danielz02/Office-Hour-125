package edu.illinois.cs.cs125.fall2019.oh125;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Date;

public class Student extends Family125 implements SendQueue {

    /** The boolean value indicating whether a student is in queue. */
    private boolean isInQueue;

    /** An instance of the inner class storing student's information about queue. */
    @Nullable
    private QueueInfo queueInfo;

    /**
     * An empty constructor for Firebase Firestore.
     */
    public Student() { }

    /**
     * The parametrized constructor for Student class.
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
     * @param category      The category of students' question, either being MP or Homework
     * @param estimatedTime The estimated time of the current session, in minutes
     * @param table         The number of student's current table
     * @return An Android task of Void type
     * @throws IllegalArgumentException when category is not either MP or Homework
     */
    @Override
    public Task<Void> enterQueue(String category,
                                 int estimatedTime,
                                 int table) throws IllegalArgumentException, FileAlreadyExistsException {
        if (estimatedTime < 0) {
            throw new IllegalArgumentException("Invalid Time: " + estimatedTime);
        } else if (estimatedTime > 10) {
            throw new IllegalArgumentException("Your Requested Time"
                    + "(" + estimatedTime + " minutes) " + "is too Long!");
        } else if (table < 0 || table > 5) {
            throw new IllegalArgumentException("Table number is invalid: " + table);
        } else if (this.queueInfo != null) {
            throw new FileAlreadyExistsException("Your are already in queue!");
            // I'm too lazy to create a customized exception.
            // Please display a alert dialogue to let the user to decide whether to exit queue and
            // make a new request.
        }
        Timestamp timeEntered = new Timestamp(new Date(Long.parseLong(FieldValue.serverTimestamp().toString())));
        this.queueInfo = new QueueInfo(category, estimatedTime, table, timeEntered);
        Log.i("Queue Info Created", queueInfo.toString());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.setIsInQueue(true);
        return db.collection("queue")
                .add(queueInfo)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.i("Enter Queue Succeed", task.getResult().toString());
                        } else {
                            Log.w("Enter Queue Failed", task.getException());
                        }
                    }
                }).continueWith(new Continuation<DocumentReference, Void>() {
                    @Override
                    public Void then(@NonNull Task<DocumentReference> task) {
                        updateQueueStatus().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i("Queue Status Updated", task.getResult().toString());
                                } else {
                                    Log.w("Queue Status Update Failed", task.getException());
                                }
                            }
                        });
                        return null;
                    }
                });
    }

    /**
     * Delete current student's record in the queue
     * @return An Android task of type Void
     */
    @Override
    public Task<Void> exitQueue() throws FileNotFoundException {
        if (this.queueInfo == null) {
            throw new FileNotFoundException("User is not in queue! No need to exit!");
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.setIsInQueue(false);
        return db.collection("queue")
                .document(this.getNetId())
                .delete().continueWith(new Continuation<Void, Void>() {
                    @Override
                    public Void then(@NonNull Task<Void> task) throws Exception {
                        updateQueueStatus().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i("Exited Queue", task.getResult().toString());
                                } else {
                                    Log.w("Exit Failed", task.getException());
                                }
                            }
                        });
                        return null;
                    }
                });
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
    void setIsInQueue(final boolean inQueue) {
        this.isInQueue = inQueue;
    }

    Task<Void> updateQueueStatus() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String netId = this.getNetId();
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
    @Nullable
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
