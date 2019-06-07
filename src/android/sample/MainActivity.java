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
//          ImagePicker.create(this) // Activity or Fragment
//            .showCamera(false)
//            .start();
//          ImagePicker.create(this)
//            .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
//            .folderMode(true) // folder mode (false by default)
//            .toolbarFolderTitle("Folder") // folder selection title
//            .toolbarImageTitle("Tap to select") // image selection title
//            .toolbarArrowColor(Color.BLACK) // Toolbar 'up' arrow color
//            .includeVideo(true) // Show video on image picker
//            .single() // single mode
//            .multi() // multi mode (default mode)
//            .limit(10) // max images can be selected (99 by default)
//            .showCamera(true) // show camera or not (true by default)
//            .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
//            .origin(images) // original selected images, used in multi mode
//            .exclude(images) // exclude anything that in image.getPath()
//            .theme(fakeR.getId("style","AppTheme")) // must inherit ef_BaseTheme. please refer to sample
//            .enableLog(false) // disabling log
//            .start(); // start image picker activity with request code
//        }
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

  public void initBottomNav(){;

    BottomNavigationView navigation = findViewById(fakeR.getId("id","navigation"));
    loadFragment(new VideoFragment(this,musicPath));
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
                    musicPath = getIntent().getExtras().getString("MUSIC_PATH");
                    PortrateActivity.startActivity(MainActivity.this, musicPath);
                    finish();
                  }
                } else {
                    Toast.makeText(MainActivity.this, "[WARN] camera permission is not grunted.", Toast.LENGTH_SHORT).show();
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
