package edu.illinois.cs.cs125.fall2019.oh125.ui.queue;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.nio.file.FileAlreadyExistsException;
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
                final int tableNum = Integer.parseInt(table.getText().toString());
                final int estimatedTime = Integer.parseInt(time.getText().toString());

                final String category;
                if (getHelp.getCheckedRadioButtonId() == R.id.mp) {
                    category = "MP";
                } else {
                    category = "HW";
                }

                // add info to queue
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                Student.getInstance(email).addOnCompleteListener(new OnCompleteListener<Family125>() {
                    @Override
                    public void onComplete(@NonNull Task<Family125> task) {
                        if (task.isSuccessful()) {
                            Student student = (Student) task.getResult();
                            try {
                                student.enterQueue(category, estimatedTime, tableNum);
                            } catch (IllegalArgumentException e) {
                                // Illegal Arg
                                dialogBox(e.getMessage());
                            } catch (FileAlreadyExistsException e) {
                                // Already in queue
                                dialogBox(e.getMessage());
                            }
                        }
                    }
                });
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

    /**
     * Pop up an alert dialog
     * @param message from calling enterQueue
     */
    public void dialogBox(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        alertDialogBuilder.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(QueueActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
