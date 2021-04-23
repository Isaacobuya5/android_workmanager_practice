package viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.work.Data;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.List;


public class BlurViewModel extends AndroidViewModel {

    private static final String TAG_OUTPUT = "TAG_OUTPUT";
    private static final String IMAGE_MANIPULATION_WORK_NAME = "IMAGE_MANIPULATION_WORK_NAME";
    private static final String KEY_IMAGE_URI = "KEY_IMAGE_URI";

    Uri imageUri = null;
    Uri outputUri = null;
    private WorkManager mWorkManager;
    LiveData<List<WorkInfo>> outputWorkInfo;

    public BlurViewModel(@NonNull Application application) {
        super(application);
        mWorkManager = WorkManager.getInstance(application.getApplicationContext());
        outputWorkInfo = mWorkManager.getWorkInfosByTagLiveData(TAG_OUTPUT);
    }

    private void cancelWork() {
        mWorkManager.cancelUniqueWork(IMAGE_MANIPULATION_WORK_NAME);
    }

    //create the input data bundle which includes the Uri to operate
    //@return Data which contains the Image Uri as a String
    private Data createInputDataForUri() {
        Data.Builder builder = new Data.Builder();
        if (imageUri != null) {
            builder.putString(KEY_IMAGE_URI, imageUri.toString());
        }
        return builder.build();
    }

    // create the work 
}
