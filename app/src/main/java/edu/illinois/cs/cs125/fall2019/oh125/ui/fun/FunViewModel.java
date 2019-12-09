package edu.illinois.cs.cs125.fall2019.oh125.ui.fun;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FunViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FunViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is fun fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}