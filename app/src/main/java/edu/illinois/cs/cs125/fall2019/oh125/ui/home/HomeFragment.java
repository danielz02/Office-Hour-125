package edu.illinois.cs.cs125.fall2019.oh125.ui.home;

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

import edu.illinois.cs.cs125.fall2019.oh125.Family125;
import edu.illinois.cs.cs125.fall2019.oh125.R;

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
                            setUpUi();
                            Toast.makeText(HomeFragment.this.getContext(), user.toString(),
                                    Toast.LENGTH_SHORT).show();
                            Log.i("Query Succeed", user.toString());
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
        return root;
    }

    /**
     * Perform all the UI setup after user login
     */
    private void setUpUi() {
        if (!this.user.getRole().equals("Student")) {
            Button staffPortalButton = getView().findViewById(R.id.staffPortal);
            staffPortalButton.setVisibility(View.VISIBLE);
        }
    }
}