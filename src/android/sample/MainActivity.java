package video.effects.android.videoeffects.sample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import video.effects.android.videoeffects.FakeR;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 88888;
	private FakeR fakeR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		    fakeR = new FakeR(this);

		    String musicPath = "";

		    if(checkPermission() && null != getIntent().getExtras().getString("MUSIC_PATH")) {
          musicPath = getIntent().getExtras().getString("MUSIC_PATH");
          PortrateActivity.startActivity(MainActivity.this, musicPath);
          finish();
        }
//        }else {
//          setContentView(fakeR.getId("layout", "activity_main"));
//          findViewById(fakeR.getId("id", "portrate")).setOnClickListener(v -> {
//          });
//          findViewById(fakeR.getId("id", "landscape")).setOnClickListener(v -> {
//            LandscapeActivity.startActivity(MainActivity.this);
//          });
//          findViewById(fakeR.getId("id", "square")).setOnClickListener(v -> {
//            SquareActivity.startActivity(MainActivity.this);
//          });
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        // request camera permission if it has not been grunted.
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ) {

            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST_CODE);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "camera permission has been grunted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "[WARN] camera permission is not grunted.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


}
