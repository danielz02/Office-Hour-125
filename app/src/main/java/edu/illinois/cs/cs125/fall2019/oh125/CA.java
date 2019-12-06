package edu.illinois.cs.cs125.fall2019.oh125;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class CA extends Family125 implements ManageQueue {
    /**
     * Dummy constructor.
     */
    public CA() { }

    /**
     * @param name The name of the person in Family125 instance.
     * @param role The role of the person, either being student, instructor, CA, or TA.
     * @param email The email of the person as a String.
     * @param isAtOfficeHour The boolean expression indicating whether the person is at office hour.
     */
    public CA(String name, String role, String email, boolean isAtOfficeHour) {
        super(name, role, email, isAtOfficeHour);
    }

    /**
     * This method will send request to Firestore to obtain all Student instances in a List.
     * Please resister a listen for callback after the web request is complete.
     *
     * @return an asynchronous operation which obtains Student instances in queue database
     */
    @Override
    public Task<List<Student>> getQueue() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("user")
                .whereEqualTo("role", "Student")
                .whereEqualTo("isInQueue", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.i("getQueue Finished Fetching Students",
                                    task.getResult().toString());
                        } else {
                            Log.w("getQueue Failed",
                                    "Fetching students failed",
                                    task.getException());
                        }
                    }
                }).continueWith(new Continuation<QuerySnapshot, List<Student>>() {
                    @Override
                    public List<Student> then(@NonNull Task<QuerySnapshot> task) {
                        List<Student> studentsInQueue = new ArrayList<>();
                        for (DocumentSnapshot document: task.getResult()) {
                            studentsInQueue.add(document.toObject(Student.class));
                        }
                        return studentsInQueue;
                    }
                }).continueWith(new Continuation<List<Student>, List<Student>>() {
                    @Override
                    public List<Student> then(@NonNull Task<List<Student>> task) {
                        for (Student studentInQueue: task.getResult()) {
                            studentInQueue.initializeQueueInfo()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) { }
                                    });
                        }
                        Log.i("getQueue Succeed", task.getResult().toString());
                        return task.getResult();
                    }
        });
    }

    /**
     * End one student's queue status.
     * Used at the end of one CA session.
     * @param student the Student instance to be kicked out from the queue
     */
    @Override @Nullable
    public Task<Void> endQueue(Student student) {
        try {
            return student.exitQueue();
        } catch (FileNotFoundException e) {
            Log.w("endQueue Failed", e);
        }
        return null;
    }
}
