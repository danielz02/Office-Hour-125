package edu.illinois.cs.cs125.fall2019.oh125.ui.queue;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import edu.illinois.cs.cs125.fall2019.oh125.R;

public class QueueActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.queue_request);

        Button submit = findViewById(R.id.sumbitTask);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText table = findViewById(R.id.table);
                EditText time = findViewById(R.id.time);
                RadioGroup getHelp = findViewById(R.id.getHelp);

                int tableNum = Integer.parseInt(table.getText().toString());
                int estimatedTime = Integer.parseInt(time.getText().toString());

                String category;
                if (getHelp.getCheckedRadioButtonId() == R.id.mp) {
                    category = "MP";
                } else {
                    category = "HW";
                }
                // add info to queue
            }
        });
    }
}
