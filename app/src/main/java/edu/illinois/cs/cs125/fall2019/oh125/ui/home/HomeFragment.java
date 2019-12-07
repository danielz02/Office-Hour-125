package edu.illinois.cs.cs125.fall2019.oh125.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import edu.illinois.cs.cs125.fall2019.oh125.Family125;
import edu.illinois.cs.cs125.fall2019.oh125.R;
import edu.illinois.cs.cs125.fall2019.oh125.Summary;
import edu.illinois.cs.cs125.fall2019.oh125.ui.StaffPortal;
import edu.illinois.cs.cs125.fall2019.oh125.ui.queue.QueueActivity;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FirebaseAuth mAuth;
    private Family125 user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        // Initialize instance variable
        mAuth = FirebaseAuth.getInstance();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull final View view, final Bundle savedInstanceState) {
        if (mAuth.getCurrentUser() != null) {
            Toast.makeText(HomeFragment.this.getContext(), "Already Logged in",
                    Toast.LENGTH_SHORT).show();
            try {
                Task<Family125> task = Family125.getInstance(mAuth.getCurrentUser().getEmail());
                task.addOnCompleteListener(HomeFragment.this.getActivity(), new OnCompleteListener<Family125>() {
                    @Override
                    public void onComplete(@NonNull Task<Family125> task) {
                        if (task.isSuccessful()) {
                            HomeFragment.this.user = task.getResult();
                            Toast.makeText(HomeFragment.this.getContext(), user.toString(),
                                    Toast.LENGTH_SHORT).show();
                            Log.i("Query Succeed", user.toString());
                            setUpUi(view);
                        } else {
                            Log.e("Query Failed", task.getException().getMessage());
                        }
                    }
                });
                // Just for testing, remove later
            } catch (NullPointerException e) {
                Toast.makeText(HomeFragment.this.getContext(), "No record found",
                        Toast.LENGTH_SHORT).show();
                Log.e("Query Failed", "No record found");
            } catch (IllegalArgumentException e) {
                Toast.makeText(HomeFragment.this.getContext(), e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.e("Query Failed", "Illegal Email Address");
            }
        }
    }


    /**
     * Perform all the UI setup after user login
     */
    private void setUpUi(final View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (!this.user.getRole().equals("Student")) {
            // If the user is not student, then set staffPortal page visible.
            Button staffPortalButton = view.findViewById(R.id.staffPortal);
            staffPortalButton.setVisibility(View.VISIBLE);
            // Start StaffPortal activity when button is clicked
            staffPortalButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), StaffPortal.class);
                    startActivity(intent);
                }
            });
            // Set queue request button invisible
            Button queue = getView().findViewById(R.id.queueRequestButton);
            queue.setVisibility(View.GONE);
        } else {
            // When student press queue request button, start queue request activity
            Button queue = getView().findViewById(R.id.queueRequestButton);
            queue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), QueueActivity.class);
                    startActivity(intent);
                }
            });
        }

        // Display number of students at Office Hour
        Summary.getInstance().getTotalStudent().addOnCompleteListener(new OnCompleteListener<Integer>() {
            @Override
            public void onComplete(@NonNull Task<Integer> task) {
                if (task.isSuccessful()) {
                    TextView studentCount = view.findViewById(R.id.studentCount);
                    studentCount.setText(String.valueOf(task.getResult()));
                } else {
                    Log.w("Total Student Display Failed", task.getException());
                }
            }
        });

        // Listener for any change in the total number of student at Office Hour
        db.collection("user")
                .whereEqualTo("role", "Student")
                .whereEqualTo("isAtOfficeHour", true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Listen Error for Total Student Number", e);
                        } else {
                            int newCount = queryDocumentSnapshots.getDocuments().size();
                            TextView studentCount = view.findViewById(R.id.studentCount);
                            studentCount.setText(String.valueOf(newCount));
                        }
                    }
                });

        // Display number of CA at Office Hour
        Summary.getInstance().getTotalCA().addOnCompleteListener(new OnCompleteListener<Integer>() {
            @Override
            public void onComplete(@NonNull Task<Integer> task) {
                if (task.isSuccessful()) {
                    TextView caCount = view.findViewById(R.id.caCount);
                    caCount.setText(String.format(getResources().getString(R.string.ca_count),
                            task.getResult()));
                } else {
                    Log.w("Total CA Display Failed", task.getException());
                }
            }
        });

        // Listener for any change in the total number of CAs at Office Hour
        db.collection("user")
                .whereEqualTo("role", "CA")
                .whereEqualTo("isAtOfficeHour", true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Listen Error for Total CA Number", e);
                        } else {
                            int newCount = queryDocumentSnapshots.getDocuments().size();
                            TextView caCount = view.findViewById(R.id.caCount);
                            caCount.setText(String.format(getResources().getString(R.string.ca_count),
                                    newCount));
                        }
                    }
                });

        // Display number of TA at Office Hour
        Summary.getInstance().getTotalTA().addOnCompleteListener(new OnCompleteListener<Integer>() {
            @Override
            public void onComplete(@NonNull Task<Integer> task) {
                if (task.isSuccessful()) {
                    TextView taCount = view.findViewById(R.id.taCount);
                    taCount.setText(String.format(getResources().getString(R.string.ta_count),
                            task.getResult()));
                } else {
                    Log.w("Total TA Display Failed", task.getException());
                }
            }
        });

        // Listener for any change in the total number of TAs at Office Hour
        db.collection("user")
                .whereEqualTo("role", "TA")
                .whereEqualTo("isAtOfficeHour", true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Listen Error for Total TA Number", e);
                        } else {
                            int newCount = queryDocumentSnapshots.getDocuments().size();
                            TextView caCount = view.findViewById(R.id.taCount);
                            caCount.setText(String.format(getResources().getString(R.string.ta_count),
                                    newCount));
                        }
                    }
                });
    }
}