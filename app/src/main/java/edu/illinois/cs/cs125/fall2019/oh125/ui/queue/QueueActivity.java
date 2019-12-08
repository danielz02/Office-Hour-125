package edu.illinois.cs.cs125.fall2019.oh125.ui.queue;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import edu.illinois.cs.cs125.fall2019.oh125.Family125;
import edu.illinois.cs.cs125.fall2019.oh125.MainActivity;
import edu.illinois.cs.cs125.fall2019.oh125.R;
import edu.illinois.cs.cs125.fall2019.oh125.Student;

public class QueueActivity extends AppCompatActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.queue_request);

        Button submit = findViewById(R.id.sumbitTask);
        Button cancel = findViewById(R.id.cancel);

        // If student clicked submit, put student in queue
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText table = findViewById(R.id.table);
                EditText time = findViewById(R.id.time);
                RadioGroup getHelp = findViewById(R.id.getHelp);

                // Get info that student put in to queue request form
                if (table.getText().toString().isEmpty() && time.getText().toString().isEmpty()) {
                    // Displaying toast message if no time or table is put in
                    Toast toast = Toast.makeText(QueueActivity.this,
                            "Please put in your estimated time and table number",
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL|
                            Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                } else if (table.getText().toString().isEmpty()) {
                    // Displaying toast message if no table number is put in
                    Toast toast= Toast.makeText(QueueActivity.this,
                            "Please put in your table number",
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL|
                                    Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                } else if (time.getText().toString().isEmpty()) {
                    // Displaying toast message if no time is put in
                    Toast toast = Toast.makeText(QueueActivity.this,
                            "Please put in the estimated time",
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL|
                            Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                } else {
                    // Get info that student put in to queue request form
                    final int tableNum = Integer.parseInt(table.getText().toString());
                    final int estimatedTime = Integer.parseInt(time.getText().toString());

                    final String category;
                    if (getHelp.getCheckedRadioButtonId() == R.id.mp) {
                        category = "MP";
                    } else if (getHelp.getCheckedRadioButtonId() == R.id.hw) {
                        category = "HW";
                    } else if (getHelp.getCheckedRadioButtonId() == R.id.other) {
                        category = "Other";
                    } else {
                        category = "";
                    }

                    // add info to queue
                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    Student.getInstance(email).addOnCompleteListener(new OnCompleteListener<Family125>() {
                        @Override
                        public void onComplete(@NonNull Task<Family125> task) {
                            if (task.isSuccessful()) {
                                final Student student = (Student) task.getResult();
                                try {
                                    student.enterQueue(category, estimatedTime, tableNum)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.i("Entered Queue",
                                                                student.getQueueInfo().toString());
                                                        // Successfully entered queue
                                                        // Set the button exit Queue in Home Fragment Visible
                                                        final Button queue = findViewById(R.id.queueRequestButton);
                                                        final Button exitQueue = findViewById(R.id.exitQueue);
                                                        exitQueue.setVisibility(View.VISIBLE);
                                                        queue.setVisibility(View.GONE);
                                                        // Create a toast
                                                        Toast toast = Toast.makeText(QueueActivity.this,
                                                                "Congrats! You've entered the queue!",
                                                                Toast.LENGTH_LONG);
                                                        toast.setGravity(Gravity.CENTER_VERTICAL|
                                                                Gravity.CENTER_HORIZONTAL, 0, 0);
                                                        toast.show();
//                                                        Intent intent = new Intent(QueueActivity.this,
//                                                                MainActivity.class);
//                                                        startActivity(intent);
//                                                        finish();
                                                    } else {
                                                        Log.w("Enter queue failed",
                                                                task.getException());
                                                        Toast toast = Toast.makeText(QueueActivity.this,
                                                                "You failed to enter queue!",
                                                                Toast.LENGTH_LONG);
                                                        toast.setGravity(Gravity.CENTER_VERTICAL|
                                                                Gravity.CENTER_HORIZONTAL, 0, 0);
                                                        toast.show();
                                                    }
                                                }
                                            });
                                } catch (Exception e) {
                                    // Illegal Arg or File Already Exist
                                    // Create a toast
                                    Log.w("Enter Queue Failed", e);
                                    Toast toast = Toast.makeText(QueueActivity.this,
                                            e.getMessage(),
                                            Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER_VERTICAL|
                                            Gravity.CENTER_HORIZONTAL, 0, 0);
                                    toast.show();
                                }
                            }
                        }
                    });
                }
            }
        });

        // if student clicked cancel, return to the home page
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QueueActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
