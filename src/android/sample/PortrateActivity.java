package video.effects.android.videoeffects.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import video.effects.android.videoeffects.FakeR;

public class PortrateActivity extends BaseCameraActivity {
	private static FakeR fakeR;
	private static String musicPath;
    public static void startActivity(Activity activity, String musicPath) {
		fakeR = new FakeR(activity);
        Intent intent = new Intent(activity, PortrateActivity.class);
        PortrateActivity.musicPath = musicPath;
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(fakeR.getId("layout","activity_portrate"));
        onCreateActivity(musicPath);
        videoWidth = 720;
        videoHeight = 1280;
        cameraWidth = 480;
        cameraHeight = 360;
    }
}
