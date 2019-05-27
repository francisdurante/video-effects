package video.effects.android.videoeffects.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import video.effects.android.videoeffects.FakeR;

public class AudioTrimmerActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    FakeR fakeR = new FakeR(this);
    setContentView(fakeR.getId("layout","activity_audio_trimmer"));
    String path = getIntent().getExtras().getString("MUSIC_PATH");
    TextView test = findViewById(fakeR.getId("id","path"));
    test.setText(path);
  }
}
