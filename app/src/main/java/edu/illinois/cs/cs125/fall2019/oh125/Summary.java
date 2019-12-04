package edu.illinois.cs.cs125.fall2019.oh125;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public abstract class Summary {
    /**
     * This method involves i Firebase Firestore request.
     * @return the total number of students present at Office Hour
     */
    public static Task<Integer> getTotalStudent() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("user")
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

    public static Task<Integer> getTotalCA() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("user")
                .whereEqualTo("role", "CA")
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
     * @return the total number of TAs present at Office Hour.
     */

    public static Task<Integer> getTotalTA() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("user")
                .whereEqualTo("role", "TA")
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
}
