package video.effects.android.videoeffects.sample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import video.effects.android.videoeffects.FakeR;
import video.effects.android.videoeffects.VideoEffects;

public class SlideShowFragment extends Fragment {
  private Context context;
  private Fragment fragment = this;
  private ArrayList<String> imagesPath;
  private List<Image> images;

  public SlideShowFragment(){}
  @SuppressLint("ValidFragment")
  public SlideShowFragment(Context context){
    this.context = context;
  }
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(new FakeR(context).getId("layout","activity_slideshow"), container, false);
//    LinearLayout linearLayout = v.findViewById(new FakeR(context).getId("id","linear"));
//    linearLayout.setOnClickListener(v1 -> ImagePicker.create(fragment)
//      .showCamera(false)// Activity or Fragment
//      .start());
	    ImagePicker.create(fragment)
        .showCamera(false)// Activity or Fragment
        .folderMode(true)
        .toolbarFolderTitle("Folder Mode") // folder selection title
        .toolbarImageTitle("Tap to select") // image selection title
      .start();
    return v;
  }

  @Override
  public void onActivityResult(int requestCode, final int resultCode, Intent data) {
    if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
      // Get a list of picked images
      images = ImagePicker.getImages(data);
      JSONArray jsonArray = new JSONArray();
      for (int x = 0; x < images.size(); x++){
        jsonArray.put(images.get(x).getPath());
      }
      VideoEffects.callbackContext.success(jsonArray);
      getActivity().finish();
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onPause() {
    getActivity().finish();
    super.onPause();
  }
}
