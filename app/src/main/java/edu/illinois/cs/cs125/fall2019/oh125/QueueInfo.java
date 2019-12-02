package edu.illinois.cs.cs125.fall2019.oh125;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class QueueInfo {
    /** The category of queue request, either being MP, Homework, or Exam. */
    private String category;
    /** The estimated time, in minutes, of one appointment. */
    private int estimatedTime;
    /** The number of the table the student is current sitting. */
    private int table;
    /** The unix timestamp of the time when the student entered the queue. */
    private int timeEntered;

    /** The dummy constructor for Firebase Firestore. */
    public QueueInfo() { }

    /**
     * The constructor for the inner class.
     * @param category The category of queue request, either being MP, Homework, or Exam.
     * @param estimatedTime The estimated time, in minutes, of one appointment.
     * @param tableNumber The number of the table the student is current sitting.
     * @param timeEntered The unix timestamp of the time when the student entered the queue.
     */
    public QueueInfo(String category, int estimatedTime, int tableNumber, int timeEntered) {
        this.category = category;
        this.estimatedTime = estimatedTime;
        this.table = tableNumber;
        this.timeEntered = timeEntered;
    }

    public String getCategory() {
        return category;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public int getTable() {
        return table;
    }

    public int getTimeEntered() {
        return timeEntered;
    }

    @Override @NonNull
    public String toString() {
        return "QueueInfo{" +
                "category='" + category + '\'' +
                ", estimatedTime=" + estimatedTime +
                ", tableNumber=" + table +
                ", timeEntered=" + timeEntered +
                '}';
    }

    public static Task<QueueInfo> getInstance(String email) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        //TODO: Fix the initialization of NetID and remove this temporary fix.
        String netId = email.split("@")[0];
        DocumentReference docRef = db.collection("queue").document(netId);
        return docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.i("Queue Info Query Succeed", task.getResult().toString());
                } else {
                    Log.w("Query Failed", task.getException());
                }
            }
        }).continueWith(new Continuation<DocumentSnapshot, QueueInfo>() {
            @Override
            public QueueInfo then(@NonNull Task<DocumentSnapshot> task) {
                return task.getResult().toObject(QueueInfo.class);
            }
        });
    }
}
