package video.effects.android.videoeffects.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import video.effects.android.videoeffects.FakeR;

public class LandscapeActivity extends BaseCameraActivity {
	private static FakeR fakeR;
    public static void startActivity(Activity activity) {
		fakeR = new FakeR(activity);
        Intent intent = new Intent(activity, LandscapeActivity.class);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(fakeR.getId("layout", "activity_landscape"));
        videoWidth = 1280;
        videoHeight = 720;
        cameraWidth = 1280;
        cameraHeight = 720;
    }
}
