package edu.illinois.cs.cs125.fall2019.oh125.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

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
                    Log.i("CA get queue", "unsuccessful");
                }
            }
        });
    }

    private void loadTasks(List<Student> students) {
        LinearLayout taskList = findViewById(R.id.taskList);
        taskList.removeAllViews();

        for (Student student: students) {
            QueueInfo info = student.getQueueInfo();

            // Get all info of the student's queueRequest form
            String name = student.getName();
            String category = info.getCategory();
            String estimatedTime = "" + info.getEstimatedTime();
            String tableNum = "Table #" + info.getTable();
            String enteredTime = "" + info.getTimeEntered();

            // Get all text views in chunk_task
            View chunkTask = getLayoutInflater().inflate(R.layout.chunk_task,
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

            // Add chunk to taskList in Staff Portal Page
            taskList.addView(chunkTask);
        }
    }
}
