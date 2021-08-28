package com.tea.ilearn.ui.link;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LinkViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public LinkViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Link fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}