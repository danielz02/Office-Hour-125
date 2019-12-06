package edu.illinois.cs.cs125.fall2019.oh125;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Summary implements OfficeHourSummary {
    /** The Firestore database instance to retrieve data. */
    private FirebaseFirestore db;

    /**
     * Default constructor.
     */
    private Summary() {
        this.db = FirebaseFirestore.getInstance();
    }
    /**
     * This method involves i Firebase Firestore request.
     * @return the total number of students present at Office Hour
     */
    @Override
    public Task<Integer> getTotalStudent() {
        return this.db.collection("user")
                .whereEqualTo("role", "Student")
                .whereEqualTo("isAtOfficeHour", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document: task.getResult()) {
                                Log.i("Student Query Succeed", document.getData().toString());
                            }
                        } else {
                            Log.w("Student Count Query Failed", task.getException());
                        }
                    }
                }).continueWith(new Continuation<QuerySnapshot, Integer>() {
                    @Override
                    public Integer then(@NonNull Task<QuerySnapshot> task) {
                        return task.getResult().size();
            }
        });
    }

    /**
     * This method involves in Firebase Firestore request.
     * @return the total number of CA present at Office Hour.
     */
    @Override
    public Task<Integer> getTotalCA() {
        return this.db.collection("user")
                .whereEqualTo("role", "CA")
                .whereEqualTo("isAtOfficeHour", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document: task.getResult()) {
                                Log.i("CA Query Succeed", document.getData().toString());
                            }
                        } else {
                            Log.w("CA Count Query Failed", task.getException());
                        }
                    }
                }).continueWith(new Continuation<QuerySnapshot, Integer>() {
                    @Override
                    public Integer then(@NonNull Task<QuerySnapshot> task) {
                        return task.getResult().size();
                    }
                });
    }

    /**
     * This method involves in Firebase Firestore request.
     * @return the total number of TAs present at Office Hour.
     */
    @Override
    public Task<Integer> getTotalTA() {
        return this.db.collection("user")
                .whereEqualTo("role", "TA")
                .whereEqualTo("isAtOfficeHour", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document: task.getResult()) {
                                Log.i("TA Query Succeed", document.getData().toString());
                            }
                        } else {
                            Log.w("TA Count Query Failed", task.getException());
                        }
                    }
                }).continueWith(new Continuation<QuerySnapshot, Integer>() {
                    @Override
                    public Integer then(@NonNull Task<QuerySnapshot> task) {
                        return task.getResult().size();
                    }
                });
    }

    /**
     * This method helps create change listener according to user input
     *
     * @param type the number of student/CA/TA you want to register listener for
     * @return a listener for the given input type
     */
    @Override
    public ListenerRegistration getChangeListener(String type) {
        if (!type.equals("Student") && !type.equals("CA") && !type.equals("TA")) {
            throw new IllegalArgumentException("Wrong Type of data:" + type);
        }
        return this.db.collection("user")
                .whereEqualTo("role", type)
                .whereEqualTo("isAtOfficeHour", true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Error in register listener", e);
                        }
                    }
                });
    }

    /**
     * This method involves in Firestore request and return a list containing the information about
     * current queue.
     * @return an Android task of a list of all QueueInfo items
     */
    @Override
    public Task<List<QueueInfo>> getQueue() {
        return this.db.collection("queue")
                .orderBy("timeEntered")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.i("Queue Collection Query Succeed", task.getResult().toString());
                        } else {
                            Log.w("Queue Collection Query Failed", task.getException());
                        }
                    }
                }).continueWith(new Continuation<QuerySnapshot, List<QueueInfo>>() {
                    @Override
                    public List<QueueInfo> then(@NonNull Task<QuerySnapshot> task) {
                        List<QueueInfo> queueInfoList = new ArrayList<>();
                        for (QueryDocumentSnapshot document: task.getResult()) {
                            QueueInfo currentQueueInfo = document.toObject(QueueInfo.class);
                            queueInfoList.add(currentQueueInfo);
                            Log.i("Instantiating QueueInfo: ", currentQueueInfo.toString());
                        }
                        return queueInfoList;
                    }
                });
    }

    /**
     * Wrapper for the constructor
     * @return a new instance of Summary class
     */
    public static Summary getInstance() {
        return new Summary();
    }
}
