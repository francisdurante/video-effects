package video.effects.android.videoeffects.sample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;

import video.effects.android.videoeffects.FakeR;

public class VideoFragment extends Fragment {
  private Context context;
  private Fragment fragment = this;
  private String musicPath = "";

  public VideoFragment() {
  }

  @SuppressLint("ValidFragment")
  public VideoFragment(Context context, String musicPath) {
    this.context = context;
    this.musicPath = musicPath;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(new FakeR(context).getId("layout", "activity_video"), container, false);
    LinearLayout linearLayout = v.findViewById(new FakeR(context).getId("id", "video_fragment"));
    linearLayout.setOnClickListener(v1 -> Toast.makeText(getContext(),"Under Maintenance",Toast.LENGTH_LONG).show());
	//
    return v;
  }
}
