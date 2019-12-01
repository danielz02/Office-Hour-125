package edu.illinois.cs.cs125.fall2019.oh125;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class Family125 {
    /** The name of the person in Family125 instance. */
    private String name;
    /** The role of the person, either being student, instructor, CA, or TA. */
    private String role;
    /** The email of the person as a String. */
    private String email;
    /** The boolean expression indicating whether the person is at office hour. */
    private boolean isAtOfficeHour;

    /** Dummy constructor. */
    public Family125() { }

    /**
     *
     * @param name The name of the person in Family125 instance.
     * @param role The role of the person, either being student, instructor, CA, or TA.
     * @param email The email of the person as a String.
     * @param isAtOfficeHour The boolean expression indicating whether the person is at office hour.
     */
    public Family125(String name, String role, String email, boolean isAtOfficeHour) {
        this.name = name;
        this.role = role;
        this.email = email;
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
    public boolean isAtOfficeHour() {
        return this.isAtOfficeHour;
    }

    /**
     * Setter for isAtOfficeHour
     * @param atOfficeHour the new boolean value indicating whether the person is at office hour.
     */
    public void setIsAtOfficeHour(boolean atOfficeHour) {
        this.isAtOfficeHour = atOfficeHour;
    }

    /**
     * Getter for email String.
     * @return The email of the person as a String
     */
    public String getEmail() {
        return email;
    }

    @Override @NonNull
    public String toString() {
        return "Name: " + this.name + "; Email: " + this.email;
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
                    Log.i("Query Succeed", task.getResult().toString());
                } else {
                    Log.e("Query Failed", task.getException().getMessage());
                }
            }
        }).continueWith(new Continuation<DocumentSnapshot, Family125>() {
            @Override
            public Family125 then(Task<DocumentSnapshot> task) {
                String userRole = task.getResult().getString("role");
                switch (userRole) {
                    case "Student":
                        return task.getResult().toObject(Student.class);
                    default:
                        return task.getResult().toObject(Family125.class);
                }
            }
        });
    }
}
