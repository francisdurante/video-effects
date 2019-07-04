package video.effects.android.videoeffects.sample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import video.effects.android.videoeffects.FakeR;
import video.effects.android.videoeffects.VideoEffects;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 88888;
	  private FakeR fakeR;
    private List<Image> images;
    private ArrayList<String> imagesPath;
    public static String musicPath = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		    fakeR = new FakeR(this);
		    this.images = new ArrayList<>();
		    imagesPath = new ArrayList<String>();
			  setContentView(fakeR.getId("layout","activity_main"));
        if(checkPermission() && null != getIntent().getExtras().getString("MUSIC_PATH")) {
          musicPath = getIntent().getExtras().getString("MUSIC_PATH");
          initBottomNav();
        }
    }

  public void initBottomNav(){;

    BottomNavigationView navigation = findViewById(fakeR.getId("id","navigation"));
    loadFragment(new SlideShowFragment(this));
      navigation.setOnNavigationItemSelectedListener(menuItem -> {
        switch(menuItem.getTitle().toString()){
          case "VIDEO":
            loadFragment(new VideoFragment(this,musicPath));
            break;
          case "SLIDE SHOW":
            loadFragment(new SlideShowFragment(this));
            break;
        }
        return true;
      });
  }

  @Override
  protected void onResume() {
      super.onResume();
      checkPermission();
  }

  @Override
  public void onBackPressed() {
      VideoEffects.callbackContext.error("userBack");
      super.onBackPressed();
  }

    @Override
  protected void onPause() {
    super.onPause();
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
                  String musicPath = "";
                  if(checkPermission() && null != getIntent().getExtras().getString("MUSIC_PATH")) {
                    finish();
                  }
                } else {
//                    Toast.makeText(MainActivity.this, "[WARN] camera permission is not grunted.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
  @Override
  protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
    if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
      // Get a list of picked images
      images = ImagePicker.getImages(data);
      JSONArray jsonArray = new JSONArray();
      for (int x = 0; x < images.size(); x++){
        jsonArray.put(images.get(x).getPath());
      }
      VideoEffects.callbackContext.success(jsonArray);
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private boolean loadFragment(Fragment fragment) {
    //switching fragment
    if (fragment != null) {
      getSupportFragmentManager()
        .beginTransaction()
        .replace(fakeR.getId("id","fragment_container"),fragment)
        .commit();
      return true;
    }
    return false;
  }
}
