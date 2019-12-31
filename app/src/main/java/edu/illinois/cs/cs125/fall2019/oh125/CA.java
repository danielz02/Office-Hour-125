package edu.illinois.cs.cs125.fall2019.oh125;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
     * TODO: Fix the bug in the async call
     *
     * @return an asynchronous operation which obtains Student instances in queue database
     */
    @Override @Deprecated
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
                        Log.i("Student List Completed!", studentsInQueue.toString());
                        return studentsInQueue;
                    }
                });
    }

    /**
     * Modify the assignedCA field of a QueueInfo document given student's NetID
     * @param netId the ID of the QueueInfo document (also student's NetID) to be updated
     * @return an async operation
     */
    public Task<Void> takeTask(String netId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("queue")
                .document(netId)
                .update("assignedCA", netId)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i("Update CA Assignment Succeed", task.getResult().toString());
                        } else {
                            Log.w("Update CA Assignment Failed", task.getException());
                        }
                    }
                });
    }

    /**
     * Loop through queue database to obtain queue tasks that are assigned to current user
     * @return an async operation which contains user's current task
     */
    public Task<List<QueueInfo>> getTaskList() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("queue")
                .whereEqualTo("assignedCA", this.getNetId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.i("Fetching QueueInfo List Succeed!",task.getResult().toString());
                        } else {
                            Log.w("Fetch QueueInfo List Failed!", task.getException());
                        }
                    }
                }).continueWith(new Continuation<QuerySnapshot, List<QueueInfo>>() {
                    @Override
                    public List<QueueInfo> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                        List<QueueInfo> queueInfoList = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                            queueInfoList.add(documentSnapshot.toObject(QueueInfo.class));
                        }
                        return queueInfoList;
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
        return student.exitQueue();
    }

    @Override @NonNull
    public String toString() {
        return "CA{} " + super.toString();
    }
}
