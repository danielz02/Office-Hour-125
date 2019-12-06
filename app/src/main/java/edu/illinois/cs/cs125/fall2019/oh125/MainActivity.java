package edu.illinois.cs.cs125.fall2019.oh125;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    @Nullable
    private Family125 user;

    /** A constant that can be passed by to onActivityResult to validate the result. */
    private static final int SIGN_IN_REQUEST = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View view) {
                Snackbar.make(view, "Feature Under Development", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_queue, R.id.nav_forecast,
                R.id.nav_status, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Initialize instance variable
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            Log.i("Authentication Succeed", mAuth.getCurrentUser().getEmail());
            try {
                Task<Family125> task = Family125.getInstance(mAuth.getCurrentUser().getEmail());
                task.addOnCompleteListener(this, new OnCompleteListener<Family125>() {
                    @Override
                    public void onComplete(@NonNull Task<Family125> task) {
                        if (task.isSuccessful()) {
                            MainActivity.this.user = task.getResult();
                            Toast.makeText(MainActivity.this, MainActivity.this.user.toString(),
                                    Toast.LENGTH_LONG).show();
                            setUpUi();
                            Log.i("User Info Query Succeed", user.toString());
                            // TODO: Remove the test here
                            if (MainActivity.this.user instanceof Student) {
                                Toast.makeText(MainActivity.this, ((Student) MainActivity.this.user).getQueueInfo().toString(),
                                        Toast.LENGTH_LONG).show();
                                Log.i("Student Queue Info Query Succeed",
                                        ((Student) user).getQueueInfo().toString());
                                Log.i("User NetID", MainActivity.this.user.getNetId());
                            }
                        } else {
                            Log.w("User Info Query Failed", task.getException());
                        }
                    }
                });
            } catch (Exception e) {
                Log.w("User Info Query Failed", e);
            }
        } else {
            loginPrompt();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.i("Selected", "ActionBar " + item.getItemId());
        switch (item.getItemId()) {
            case R.id.action_settings:
                settingsDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     *
     * @param requestCode the request code passed by to startActivityForResult
     * @param resultCode a value indicating how the request finished (e.g. completed or canceled)
     * @param data an Intent containing results (e.g. as a URI or in extras)
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If the user has logged in with @illinois.edu address, start MainActivity.
        boolean isEmailValid = mAuth.getCurrentUser().getEmail().contains("@illinois.edu");
        if (requestCode == SIGN_IN_REQUEST && resultCode == RESULT_OK && !isEmailValid) {
            Toast.makeText(this, "Non @illinois.edu Email Address!",
                    Toast.LENGTH_SHORT).show();
            mAuth.getCurrentUser().delete();
            mAuth.signOut();
            loginPrompt();
        } else if (requestCode == SIGN_IN_REQUEST && resultCode == RESULT_OK) {
            Toast.makeText(this, "Authentication Succeed.",
                    Toast.LENGTH_SHORT).show();
            recreate();
        } else {
            loginPrompt();
        }
    }

    /**
     * Perform all the UI setup after user login
     */
    private void setUpUi() {
        // Get current FirebaseUser instance
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userEmailString = currentUser.getEmail();
        // Retrieve navigation bar and TextView
        NavigationView navigationView = findViewById(R.id.nav_view);
        TextView userEmailView = navigationView.getHeaderView(0).findViewById(R.id.userEmail);
        userEmailView.setText(userEmailString);
        TextView userNameView = navigationView.getHeaderView(0).findViewById(R.id.userName);
        userNameView.setText(currentUser.getDisplayName());
        if (!this.user.getRole().equals("Student")) {
            Button staffPortalButton = findViewById(R.id.staffPortal);
            staffPortalButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Prompt Firebase login UI
     */
    private void loginPrompt() {
        // Choose Email as authentication provider
        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build());
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers).build(), SIGN_IN_REQUEST);
    }

    /**
     * Prompt settings AlertDialog
     */
    private void settingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.logout,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AuthUI.getInstance()
                                .signOut(getApplicationContext())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mAuth.signOut();
                                            MainActivity.this.recreate();
                                        } else {
                                            Log.w("log out failed", task.getException());
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.i("Logout", "Logout canceled");
                            }
                        });
        builder.show();
    }

}
