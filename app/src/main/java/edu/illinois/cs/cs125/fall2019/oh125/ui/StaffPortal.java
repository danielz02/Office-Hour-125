package edu.illinois.cs.cs125.fall2019.oh125.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import edu.illinois.cs.cs125.fall2019.oh125.CA;
import edu.illinois.cs.cs125.fall2019.oh125.Family125;
import edu.illinois.cs.cs125.fall2019.oh125.QueueInfo;
import edu.illinois.cs.cs125.fall2019.oh125.R;
import edu.illinois.cs.cs125.fall2019.oh125.Student;
import edu.illinois.cs.cs125.fall2019.oh125.Summary;

public class StaffPortal extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_portal);

        // Get Current CA Email
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        CA.getInstance(email).addOnCompleteListener(new OnCompleteListener<Family125>() {
            @Override
            public void onComplete(@NonNull Task<Family125> task) {
                if (task.isSuccessful()) {
                    CA ca = (CA) task.getResult();
                    // Get list of students in Queue
                    ca.getQueue().addOnSuccessListener(new OnSuccessListener<List<Student>>() {
                        @Override
                        public void onSuccess(List<Student> students) {
                            Log.i("CA get queue", students.toString());
                            loadTasks(students);
                        }
                    });
                } else {
                    Log.w("CA getQueue Failed", task.getException());
                }
            }
        });
    }

    private void loadTasks(List<Student> students) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final LinearLayout taskList = findViewById(R.id.taskList);
        taskList.removeAllViews();

        for (final Student currentStudent: students) {
            db.collection("queue")
                    .document(currentStudent.getNetId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                // Get all info of the student's queueRequest form
                                Log.i("QueueInfo Fetch Succeed", task.getResult().toString());
                                String name = "  " + currentStudent.getName();
                                String category = task.getResult().getString("category");
                                String estimatedTime = "Estimated: " + task.getResult().getLong("estimatedTime") + "min";
                                String tableNum = "Table #" + task.getResult().getLong("table");
                                String enteredTime = "" + task.getResult().getString("humanTime");

                                // Get all text views in chunk_task
                                final View chunkTask = getLayoutInflater().inflate(R.layout.chunk_task,
                                        taskList, false);
                                TextView studentName = chunkTask.findViewById(R.id.studentName);
                                TextView categ = chunkTask.findViewById(R.id.category);
                                TextView timeNeeded = chunkTask.findViewById(R.id.timeNeeded);
                                TextView table = chunkTask.findViewById(R.id.studentTable);
                                TextView enterTime = chunkTask.findViewById(R.id.enteredTime);

                                // Display info in chunk task
                                studentName.setText(name);
                                categ.setText(category);
                                timeNeeded.setText(estimatedTime);
                                table.setText(tableNum);
                                enterTime.setText(enteredTime);

                                // CheckBox getTask
                                final CheckBox getTask = chunkTask.findViewById(R.id.getTask);
                                getTask.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        new AlertDialog.Builder(StaffPortal.this)
                                                .setTitle("Getting new task...")
                                                .setMessage("Are you sure you want to take this task?")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        //set what would happen when positive button is clicked
                                                        /**
                                                         * feature to be added later
                                                         * Only set the chunk gone for now
                                                         */
                                                        chunkTask.setVisibility(View.GONE);
                                                    }
                                                })
                                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        //Uncheck the checkbox when negative button is clicked
                                                        getTask.setChecked(false);
                                                    }
                                                }).show();
                                    }
                                });

                                // Add chunk to taskList in Staff Portal Page
                                taskList.addView(chunkTask);
                            } else {
                                Log.w("QueueInfo Initialization Failed", task.getException());
                            }
                        }
                    });
        }
    }
}
