package edu.illinois.cs.cs125.fall2019.oh125.ui.fun;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import edu.illinois.cs.cs125.fall2019.oh125.R;

public class FunFragment extends Fragment {

    private FunViewModel funViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        funViewModel = ViewModelProviders.of(this).get(FunViewModel.class);
        View root = inflater.inflate(R.layout.fragment_fun, container, false);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        WebView transformer = getView().findViewById(R.id.transformer);

        String transformerUrl = getResources().getString(R.string.transformer_url);
        transformer.setWebViewClient(new WebViewClient());
        transformer.getSettings().setJavaScriptEnabled(true);
        transformer.loadUrl(transformerUrl);


    }
}