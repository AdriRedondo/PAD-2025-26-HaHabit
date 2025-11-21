package es.ucm.fdi.pad.hahabit.ui.tracker;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TrackerViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TrackerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is tracker fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
