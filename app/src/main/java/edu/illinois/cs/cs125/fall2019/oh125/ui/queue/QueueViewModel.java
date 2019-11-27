package edu.illinois.cs.cs125.fall2019.oh125.ui.queue;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QueueViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public QueueViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is queue fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}