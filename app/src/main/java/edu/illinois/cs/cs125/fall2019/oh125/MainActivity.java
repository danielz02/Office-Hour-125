package edu.illinois.cs.cs125.fall2019.oh125;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import edu.illinois.cs.cs125.fall2019.oh125.ui.StaffPortal;

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
        // Floating button with Ben's image
        // Link to CS125 Forum Page when clicked
        FloatingActionButton fabBen = findViewById(R.id.fabBen);
        fabBen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://cs125-forum.cs.illinois.edu/"));
                startActivity(intent);
            }
        });

        // Floating button with Geoff's image
        // Link to CS125 'Learn' Page when clicked
        FloatingActionButton fabGeoff = findViewById(R.id.fabGeoff);
        fabGeoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://cs125.cs.illinois.edu/learn/"));
                startActivity(intent);
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
                            Toast.makeText(MainActivity.this,
                                    MainActivity.this.user.toString(),
                                    Toast.LENGTH_LONG).show();
                            setUpUi();
                            newQueueNotification();
                            queueStatusUpdateNotification();
                            Log.i("Current User Info Query Succeed", user.toString());
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
        ImageView avatar = findViewById(R.id.gravatar);
        Glide.with(this)
                .load("https://www.gravatar.com/avatar/" + MD5Util.md5Hex(userEmailString) + "?s=256")
                .into(avatar);
        if (this.user instanceof Student) {
            final Student userAsStudent = (Student) this.user;
            try {
                Log.i("QueueInfo Initialization Succeed",
                        userAsStudent.getQueueInfo().toString());
                Toast.makeText(getApplicationContext(),
                        userAsStudent.getQueueInfo().toString(),
                        Toast.LENGTH_LONG).show();
                Log.i("User NetID", userAsStudent.getNetId());
            } catch (NullPointerException e) {
                Log.w("Student not in queue", e);
            }
        }
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

    private void newQueueNotification() {
        Intent intent = new Intent(this, StaffPortal.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, intent, 0);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this, getString(R.string.notification_channel_id))
                .setSmallIcon(R.drawable.cs125)
                .setContentTitle("New student entered Queue!")
                .setContentText("Help Needed!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Please go to staff portal and pick up your task!"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Log.i("Notification in Process", "processing");
        if (this.user instanceof CA && this.user.getIsAtOfficeHour()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("queue")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                            @Nullable FirebaseFirestoreException e) {
                            createNotificationChannel();
                            NotificationManagerCompat notificationManager =
                                    NotificationManagerCompat.from(MainActivity.this);

                            // notificationId is a unique int for each notification that you must define
                            notificationManager.notify(5, builder.build());
                            Log.i("Notification succeed", "sent!");
                        }
                    });
        }

    }

    private void queueStatusUpdateNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, intent, 0);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this, getString(R.string.notification_channel_id))
                .setSmallIcon(R.drawable.cs125)
                .setContentTitle("You have been assigned a CA")
                .setContentText("Go to check out!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Please go talk to him/her!"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Log.i("Notification in Process", "processing");
        if (this.user instanceof Student && this.user.getIsAtOfficeHour()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("queue").document(this.user.getNetId())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w("QueueInfo listener failed", e);
                                return;
                            }
                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                Log.i("Current QueueInfo Changed: ", documentSnapshot.toString());
                                createNotificationChannel();
                                NotificationManagerCompat notificationManager =
                                        NotificationManagerCompat.from(MainActivity.this);
                                // notificationId is a unique int for each notification that you must define
                                notificationManager.notify(5, builder.build());
                                Log.i("Notification succeed", "sent!");
                            } else {
                                Log.i("Current QueueInfo Not Found: ", "User not in queue");
                            }
                        }
                    });
        }
    }



    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_name);
            String description = getString(R.string.notification_description);
            final String CHANNEL_ID = getString(R.string.notification_channel_id);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
