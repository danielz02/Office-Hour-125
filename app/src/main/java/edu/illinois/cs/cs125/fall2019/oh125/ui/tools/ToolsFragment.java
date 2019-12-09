package edu.illinois.cs.cs125.fall2019.oh125.ui.tools;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import de.cketti.mailto.EmailIntentBuilder;
import edu.illinois.cs.cs125.fall2019.oh125.Family125;
import edu.illinois.cs.cs125.fall2019.oh125.MD5Util;
import edu.illinois.cs.cs125.fall2019.oh125.R;

public class ToolsFragment extends Fragment {

    private ToolsViewModel toolsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(ToolsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tools, container, false);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final LinearLayout caList = getView().findViewById(R.id.taList);
        caList.removeAllViews();

        db.collection("user")
                .whereEqualTo("role", "CA")
                .whereEqualTo("isAtOfficeHour", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            task.getResult().toObjects(Family125.class);
                            for (DocumentSnapshot documentSnapshot: task.getResult()) {
                                Log.i("Fetching CA", documentSnapshot.toString());
                                final String email = documentSnapshot.getString("email");
                                String identity = "CA";
                                String name = documentSnapshot.getString("name");

                                View chunkTask = getLayoutInflater().inflate(R.layout.chunk_at403,
                                        caList, false);

                                ImageView avatar = chunkTask.findViewById(R.id.avatar);
                                TextView emailText = chunkTask.findViewById(R.id.staffEmail);
                                TextView identityText = chunkTask.findViewById(R.id.identity);
                                TextView nameText = chunkTask.findViewById(R.id.staffName);

                                Glide.with(getActivity())
                                        .load("https://www.gravatar.com/avatar/"
                                                + MD5Util.md5Hex(email) + "?s=256")
                                        .into(avatar);

                                emailText.setText(email);
                                identityText.setText(identity);
                                nameText.setText(name);

                                avatar.setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View view) {
                                        EmailIntentBuilder.from(getActivity())
                                                .to(email)
                                                .subject("Regarding Office Hour")
                                                .body("From OfficeHour 125")
                                                .start();
                                        return true;
                                    }
                                });

                                caList.addView(chunkTask);
                            }
                        } else {
                            Log.w("Fetch CA List Failed", task.getException());
                        }
                    }
                });
    }

}