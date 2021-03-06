package edu.illinois.cs.cs125.fall2019.oh125;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;


public class Family125 implements OfficeHourStatus {
    /** The name of the person in Family125 instance. */
    private String name;
    /** The role of the person, either being student, instructor, CA, or TA. */
    private String role;
    /** The email of the person as a String. */
    private String email;
    /** The NetID of the Family125 instance, generated from @illinois.edu email. */
    private String netId;
    /** The boolean expression indicating whether the person is at office hour. */
    private boolean isAtOfficeHour;

    /** Dummy constructor. */
    Family125() {
        Log.i("Family125 Object instantiated", "dummy constructor is called!");
    }

    /**
     * @param name The name of the person in Family125 instance.
     * @param role The role of the person, either being student, instructor, CA, or TA.
     * @param email The email of the person as a String.
     * @param isAtOfficeHour The boolean expression indicating whether the person is at office hour.
     */
    Family125(String name, String role, String email, boolean isAtOfficeHour) {
        this.name = name;
        this.role = role;
        this.email = email;
        this.netId = email.split("@")[0];
        this.isAtOfficeHour = isAtOfficeHour;
    }

    /**
     * Getter for name String
     * @return the name of the person
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for role String
     * @return the role of the person
     */
    public String getRole() {
        return role;
    }

    /**
     * Getter for isAtOfficeHour boolean
     * @return a boolean value indicating whether the person is at office hour.
     */
    public boolean getIsAtOfficeHour() {
        return this.isAtOfficeHour;
    }

    /**
     * Setter for isAtOfficeHour
     * @param atOfficeHour the new boolean value indicating whether the person is at office hour.
     */
    public void setIsAtOfficeHour(final boolean atOfficeHour) {
        this.isAtOfficeHour = atOfficeHour;
    }

    /**
     * This method will update the Firestore database according to current instance's isAtOfficeHour
     * @return an Android Task of Void type
     */
    public Task<Void> updateOfficeHourStatus() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String netId = this.getNetId();
        final DocumentReference docRef = db.collection("user").document(netId);
        return db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(docRef);
                transaction.update(docRef, "isAtOfficeHour", Family125.this.isAtOfficeHour);
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

    /**
     * Getter for email String.
     * @return the email of the person as a String
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Getter for NetID String.
     * @return the NetID for the instance as a String
     */
    public String getNetId() {
        if (this.netId == null) {
            this.netId = this.email.split("@")[0];
        }
        return this.netId;
    }

    @Override @NonNull
    public String toString() {
        return "Name: " + this.getName() + "; NetID: " + this.getNetId();
    }

    /**
     * Get an instance of Family125 class from Firebase Firestore
     * @param userEmail the user's email from Firebase Auth
     * @return an instance of Task<Family125>
     * @throws IllegalArgumentException when the input is not an email address at all
     */
    public static Task<Family125> getInstance(final String userEmail) throws IllegalArgumentException {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        String netId;
        if (userEmail.contains("@illinois.edu")) {
            netId = userEmail.trim().split("@")[0];
        } else {
            throw new IllegalArgumentException("Invalid Email Address: " + userEmail);
        }
        DocumentReference docRef = db.collection("user").document(netId);
        return docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.i("getInstance Query Succeed", task.getResult().toString());
                } else {
                    Log.w("getInstance Query Failed", task.getException());
                }
            }
        }).continueWithTask(new Continuation<DocumentSnapshot, Task<Family125>>() {
            @Override
            public Task<Family125> then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                String userRole = task.getResult().getString("role");
                Log.i("User Role", userRole);
                switch (userRole) {
                    case "CA":
                        Log.i("CA instance initialized", task.getResult().toObject(CA.class).toString());
                        Family125 CA = task.getResult().toObject(CA.class);
                        return Tasks.forResult(CA);
                    case "TA":
                        Log.i("TA instance initialized", task.getResult().toObject(CA.class).toString());
                        Family125 TA = task.getResult().toObject(TA.class);
                        return Tasks.forResult(TA);
                    case "Student":
                        final Student toReturn = task.getResult().toObject(Student.class);
                        Log.i("Student instance initialized", toReturn.toString());
                        return toReturn.initializeQueueInfo();
                    default:
                        Family125 unknown = task.getResult().toObject(Student.class);
                        return Tasks.forResult(unknown);
                }
            }
        }).continueWith(new Continuation<Family125, Family125>() {
            @Override
            public Family125 then(@NonNull Task<Family125> task) throws Exception {
                return task.getResult();
            }
        });

    }
}
