package edu.illinois.cs.cs125.fall2019.oh125.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;

import java.util.List;

import edu.illinois.cs.cs125.fall2019.oh125.QueueInfo;
import edu.illinois.cs.cs125.fall2019.oh125.R;
import edu.illinois.cs.cs125.fall2019.oh125.Summary;

public class StaffPortal extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_portal);
        if (Summary.getInstance().getQueue().isComplete()) {
            loadTasks();
        }
    }

    private void loadTasks() {
        LinearLayout taskList = findViewById(R.id.taskList);
        taskList.removeAllViews();
        List<QueueInfo> infos = Summary.getInstance().getQueue().getResult();
        for (QueueInfo info: infos) {
            String category = info.getCategory();
            String estimatedTime = "" + info.getEstimatedTime();
            String tableNum = "" + info.getTable();
            String enteredTime = "" + info.getTimeEntered();

            View chunkTask = getLayoutInflater().inflate(R.layout.chunk_task,
                    taskList, false);
            TextView categ = chunkTask.findViewById(R.id.category);
            TextView timeNeeded = chunkTask.findViewById(R.id.timeNeeded);
            TextView table = chunkTask.findViewById(R.id.studentTable);
            TextView enterTime = chunkTask.findViewById(R.id.enteredTime);

            categ.setText(category);
            timeNeeded.setText(estimatedTime);
            table.setText(tableNum);
            enterTime.setText(enteredTime);

            taskList.addView(chunkTask);
        }
    }
}
