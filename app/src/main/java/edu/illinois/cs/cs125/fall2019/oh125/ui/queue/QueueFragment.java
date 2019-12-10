package edu.illinois.cs.cs125.fall2019.oh125.ui.queue;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Random;

import edu.illinois.cs.cs125.fall2019.oh125.R;

public class QueueFragment extends Fragment {

    private QueueViewModel queueViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        queueViewModel =
                ViewModelProviders.of(this).get(QueueViewModel.class);
        View root = inflater.inflate(R.layout.fragment_queue, container, false);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final LinearLayout queueList = getView().findViewById(R.id.queueList);

        final String[] fakeNames = getResources().getStringArray(R.array.dummy_name);

        db.collection("queue")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot qds: task.getResult()) {
                                int randInt = (int) (Math.random() * fakeNames.length);
                                String name = fakeNames[randInt];
                                String category = qds.getString("category");
                                String estimatedTime = "Estimated: "
                                        + qds.getLong("estimatedTime") + "min";
                                String tableNum = "Table #"
                                        + qds.getLong("table");
                                String enteredTime = ""
                                        + qds.getString("humanTime");

                                final View chunkList = getLayoutInflater().inflate(R.layout.chunk_queue,
                                        queueList, false);
                                TextView studentName = chunkList.findViewById(R.id.dummyName);
                                TextView categ = chunkList.findViewById(R.id.questionCat);
                                TextView timeNeeded = chunkList.findViewById(R.id.timeEst);
                                TextView table = chunkList.findViewById(R.id.tableNum);
                                TextView enterTime = chunkList.findViewById(R.id.timeEntered);

                                studentName.setText(name);
                                categ.setText(category);
                                timeNeeded.setText(estimatedTime);
                                table.setText(tableNum);
                                enterTime.setText(enteredTime);

                                queueList.addView(chunkList);

                            }
                        } else {
                            Log.w("Queue Collection Failed", task.getException());
                        }
                    }
                });
    }
}