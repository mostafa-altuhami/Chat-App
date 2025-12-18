package com.example.chatapplication;

import static com.example.chatapplication.other.Constants.CLOUD_NAME;
import android.app.Application;
import com.cloudinary.android.MediaManager;
import com.example.chatapplication.Utils.FCMTokenUtil;
import java.util.HashMap;
import java.util.Map;

public class MyApp extends Application {



    @Override
    public void onCreate() {
        super.onCreate();
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", CLOUD_NAME);
        MediaManager.init(this, config);

        FCMTokenUtil.initializeFCM(this);


    }


}
