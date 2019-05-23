package video.effects.android.videoeffects.sample;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLException;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import video.effects.android.videoeffects.VideoEffects;
import video.effects.android.videoeffects.camerarecorder.CameraRecordListener;
import video.effects.android.videoeffects.camerarecorder.CameraRecorder;
import video.effects.android.videoeffects.camerarecorder.CameraRecorderBuilder;
import video.effects.android.videoeffects.camerarecorder.LensFacing;
import video.effects.android.videoeffects.sample.widget.Filters;
import video.effects.android.videoeffects.sample.widget.GlBitmapOverlaySample;
import video.effects.android.videoeffects.sample.widget.SampleGLView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;


import video.effects.android.videoeffects.FakeR;

/**
 * Created by sudamasayuki2 on 2018/07/02.
 */

public class BaseCameraActivity extends AppCompatActivity {

  private ViewPager viewPager;
  private SampleGLView sampleGLView;
  protected CameraRecorder cameraRecorder;
  private String filepath;
  private ImageButton recordBtn;
  protected LensFacing lensFacing = LensFacing.FRONT;
  protected int cameraWidth = 1280;
  protected int cameraHeight = 720;
  protected int videoWidth = 720;
  protected int videoHeight = 720;
  private AlertDialog filterDialog;
  private boolean toggleClick = false;
  private FakeR fakeR;
  MediaPlayer mediaPlayer;
  private String musicPath = "";
  private String outFile = "";
  private long mp3Duration = 0;
  private CountDownTimer countDownTimer = null;

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  protected void onCreateActivity(String musicPath) {
    this.musicPath = musicPath;
    getSupportActionBar().hide();
    fakeR = new FakeR(this);
    initViewPager();
    initialLoadResources();
    recordBtn = findViewById(fakeR.getId("id","btn_record"));
    recordBtn.setOnClickListener(v -> {
      mediaPlayer = MediaPlayer.create(this,Uri.parse(this.musicPath));
      mp3Duration = mediaPlayer.getDuration();
      if (recordBtn.getTag().equals(getString(fakeR.getId("string","app_record")))) {
        new GlBitmapOverlaySample(BitmapFactory.decodeResource(this.getResources(), fakeR.getId("mipmap", "ic_launcher_round")));
        filepath = getVideoFilePath();
        outFile = myOneWorldMyStoryaVideosPath();
        cameraRecorder.start(filepath);
        cameraRecorder.changeAutoFocus();
        recordBtn.setTag("Stop");
        recordBtn.setImageResource(fakeR.getId("drawable","ic_recording"));
        this.musicPath = musicPath;
        mediaPlayer.start();
        countDownTimer = new CountDownTimer(mp3Duration,1000) {
          @Override
          public void onTick(long millisUntilFinished) {}
          @Override
          public void onFinish() {
            cameraRecorder.stop();
            mediaPlayer.stop();
            stopRecording();
          }
        }.start();

      } else {
        cameraRecorder.stop();
        mediaPlayer.stop();
        stopRecording();
      }

    });
    findViewById(fakeR.getId("id","btn_flash")).setOnClickListener(v -> {
      if (cameraRecorder != null && cameraRecorder.isFlashSupport()) {
        cameraRecorder.switchFlashMode();
        cameraRecorder.changeAutoFocus();
      }
    });

    findViewById(fakeR.getId("id","btn_switch_camera")).setOnClickListener(v -> {
      releaseCamera();
      if (lensFacing == LensFacing.BACK) {
        lensFacing = LensFacing.FRONT;
      } else {
        lensFacing = LensFacing.BACK;
      }
      toggleClick = true;
    });

    findViewById(fakeR.getId("id","btn_filter")).setOnClickListener(v -> {
      if (filterDialog == null) {

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("Choose a filter");
        builder.setOnDismissListener(dialog -> {
          filterDialog = null;
        });

        final Filters[] filters = Filters.values();
        CharSequence[] charList = new CharSequence[filters.length];
        for (int i = 0, n = filters.length; i < n; i++) {
          charList[i] = filters[i].name();
        }
        builder.setItems(charList, (dialog, item) -> {
          changeFilter(filters[item]);
        });
        filterDialog = builder.show();
      } else {
        filterDialog.dismiss();
      }

    });

    findViewById(fakeR.getId("id","btn_image_capture")).setOnClickListener(v -> {
      captureBitmap(bitmap -> {
        new Handler().post(() -> {
          String imagePath = getImageFilePath();
          saveAsPngImage(bitmap, imagePath);
          exportPngToGallery(getApplicationContext(), imagePath);
        });
      });
    });


    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

  }

  @Override
  protected void onResume() {
    super.onResume();
    setUpCamera();
  }

  @Override
  protected void onStop() {
    super.onStop();
    releaseCamera();
  }

  private void releaseCamera() {
    if (sampleGLView != null) {
      sampleGLView.onPause();
    }

    if (cameraRecorder != null) {
      cameraRecorder.stop();
      cameraRecorder.release();
      cameraRecorder = null;
    }

    if (sampleGLView != null) {
      ((FrameLayout) findViewById(fakeR.getId("id","wrap_view"))).removeView(sampleGLView);
      sampleGLView = null;
    }
  }

  private void setUpCameraView() {
    runOnUiThread(() -> {
      FrameLayout frameLayout = findViewById(fakeR.getId("id","wrap_view"));
      frameLayout.removeAllViews();
      sampleGLView = null;
      sampleGLView = new SampleGLView(getApplicationContext());
      sampleGLView.setTouchListener((event, width, height) -> {
        if (cameraRecorder == null) return;
        cameraRecorder.changeManualFocusPoint(event.getX(), event.getY(), width, height);
      });
      frameLayout.addView(sampleGLView);
    });
  }

  private void setUpCamera() {
    setUpCameraView();

    cameraRecorder = new CameraRecorderBuilder(this, sampleGLView)
      //.recordNoFilter(true)
      .cameraRecordListener(new CameraRecordListener() {
        @Override
        public void onGetFlashSupport(boolean flashSupport) {
          runOnUiThread(() -> {
            findViewById(fakeR.getId("id","btn_flash")).setEnabled(flashSupport);
          });
        }

        @Override
        public void onRecordComplete() {
          exportMp4ToGallery(getApplicationContext(), filepath);
        }

        @Override
        public void onRecordStart() {

        }

        @Override
        public void onError(Exception exception) {
          Log.e("CameraRecorder", exception.toString());
        }

        @Override
        public void onCameraThreadFinish() {
          if (toggleClick) {
            runOnUiThread(() -> {
              setUpCamera();
            });
          }
          toggleClick = false;
        }
      })
      .videoSize(videoWidth, videoHeight)
      .cameraSize(480, 360)
      .lensFacing(lensFacing)
      .build();


  }

  private void changeFilter(Filters filters) {
    cameraRecorder.setFilter(Filters.getFilterInstance(filters, getApplicationContext()));
  }

  private interface BitmapReadyCallbacks {
    void onBitmapReady(Bitmap bitmap);
  }

  private void captureBitmap(final BitmapReadyCallbacks bitmapReadyCallbacks) {
    sampleGLView.queueEvent(() -> {
      EGL10 egl = (EGL10) EGLContext.getEGL();
      GL10 gl = (GL10) egl.eglGetCurrentContext().getGL();
      Bitmap snapshotBitmap = createBitmapFromGLSurface(sampleGLView.getMeasuredWidth(), sampleGLView.getMeasuredHeight(), gl);

      runOnUiThread(() -> {
        bitmapReadyCallbacks.onBitmapReady(snapshotBitmap);
      });
    });
  }

  private Bitmap createBitmapFromGLSurface(int w, int h, GL10 gl) {

    int bitmapBuffer[] = new int[w * h];
    int bitmapSource[] = new int[w * h];
    IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
    intBuffer.position(0);

    try {
      gl.glReadPixels(0, 0, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
      int offset1, offset2, texturePixel, blue, red, pixel;
      for (int i = 0; i < h; i++) {
        offset1 = i * w;
        offset2 = (h - i - 1) * w;
        for (int j = 0; j < w; j++) {
          texturePixel = bitmapBuffer[offset1 + j];
          blue = (texturePixel >> 16) & 0xff;
          red = (texturePixel << 16) & 0x00ff0000;
          pixel = (texturePixel & 0xff00ff00) | red | blue;
          bitmapSource[offset2 + j] = pixel;
        }
      }
    } catch (GLException e) {
      Log.e("CreateBitmap", "createBitmapFromGLSurface: " + e.getMessage(), e);
      return null;
    }

    return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
  }

  public void saveAsPngImage(Bitmap bitmap, String filePath) {
    try {
      File file = new File(filePath);
      FileOutputStream outStream = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
      outStream.close();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void exportMp4ToGallery(Context context, String filePath) {
    final ContentValues values = new ContentValues(4);
    values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
    values.put(MediaStore.Video.Media.DATA, filePath);
    context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
      values);
    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
      Uri.parse("file://" + filePath)));

    VideoEffects.callbackContext.success(filepath);
    finish();
  }

  public static String getVideoFilePath() {
    return getAndroidMoviesFolder().getAbsolutePath() + "/Mystorya-" + new SimpleDateFormat("yyyyyMMddHHmms").format(new Date()) + "cameraRecorder.mp4";
  }

  public static String myOneWorldMyStoryaVideosPath() {
    return getAndroidMoviesFolder().getAbsolutePath() + "/Mystorya-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".mp4";
  }

  public static File getAndroidMoviesFolder() {
    String root = Environment.getExternalStorageDirectory().toString();
    File myDir = new File(root + "/MyStorya");
    if(!myDir.exists())
      myDir.mkdirs();
    return myDir;
  }

  public static void deleteUnusedVideo(){
    String path = getAndroidMoviesFolder().getAbsolutePath();
    File directory = new File(path);
    File[] files = directory.listFiles();
    for (File file : files) {
      if (file.getName().contains("cameraRecorder")) {
        file.delete();
      }
    }
  }

  private static void exportPngToGallery(Context context, String filePath) {
    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    File f = new File(filePath);
    Uri contentUri = Uri.fromFile(f);
    mediaScanIntent.setData(contentUri);
    context.sendBroadcast(mediaScanIntent);
  }

  public static String getImageFilePath() {
    return getAndroidImageFolder().getAbsolutePath() + "/" + new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + "cameraRecorder.png";
  }

  public static File getAndroidImageFolder() {
    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
  }

  public boolean addMusic(String videoInput, String audioInput, String output, Context context) {
    String[] command = new String[]  {"-i", audioInput, "-i", videoInput, "-acodec", "copy", "-shortest",
      "-vcodec", "copy", output };
    boolean exeCmdStatus = executeCMD(command,context);
    return exeCmdStatus;
  }

  private boolean executeCMD(String[] cmd,Context context) {
    FFmpeg ffmpeg = FFmpeg.getInstance(this);

    ProgressDialog dialog = ProgressDialog.show(context, "",
      "Loading. Please wait...", true);
    try {
      ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
        @Override
        public void onStart() {
        }

        @Override
        public void onFailure() {

        }

        @Override
        public void onSuccess() {
          try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

              @Override
              public void onStart() {

              }

              @Override
              public void onProgress(String message) {

              }

              @Override
              public void onFailure(String message) {
                dialog.dismiss();
                Toast.makeText(context,message,Toast.LENGTH_LONG).show();
              }

              @Override
              public void onSuccess(String message) {
                dialog.dismiss();
              }

              @Override
              public void onFinish() {
                dialog.dismiss();
                VideoEffects.callbackContext.success(outFile);
                deleteUnusedVideo();
              }
            });
          } catch (FFmpegCommandAlreadyRunningException e) {

            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            dialog.dismiss();
          }
        }

        @Override
        public void onFinish() {
        }
      });
    } catch (FFmpegNotSupportedException e) {
      Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
    }

    return true;
  }

  public void stopRecording(){
    if(null != countDownTimer){
      countDownTimer.cancel();
    }
    if(!"".equals(this.musicPath)) {
      addMusic(filepath, this.musicPath, outFile, this);
    }
    else {
      exportMp4ToGallery(this, filepath);
    }
    recordBtn.setImageResource(fakeR.getId("drawable","ic_record"));
    recordBtn.setTag(getString(fakeR.getId("string","app_record")));
  }

  public void initViewPager() {
    HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    horizontalScrollView.setLayoutParams(layoutParams);

    LinearLayout linearLayout = new LinearLayout(this);
    LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
    linearLayout.setLayoutParams(linearParams);

    horizontalScrollView.addView(linearLayout);
    LinearLayout linearLayout1 = null;

    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    params1.setMargins(15, 15, 15, 0);
    params1.gravity = Gravity.CENTER;
    final Filters[] filters = Filters.values();
    CharSequence[] charList = new CharSequence[filters.length];
    for (int i = 0, n = filters.length; i < n; i++) {
      charList[i] = filters[i].name();
    }
    for (int x = 0; x < Filters.values().length; x++) {
      ImageView imageView1 = new ImageView(this);
      imageView1.setLayoutParams(params1);
      imageView1.setImageResource(fakeR.getId("drawable", "m1w_logo"));
      imageView1.setTag(filters[x]);
      imageView1.getLayoutParams().height = 200;
      imageView1.getLayoutParams().width = 200;
      imageView1.setOnClickListener(v->{
        changeFilter((Filters)imageView1.getTag());
        Toast.makeText(this,imageView1.getTag().toString(),Toast.LENGTH_SHORT).show();
      });
      imageView1.requestLayout();
      linearLayout.addView(imageView1);
      linearLayout1 = findViewById(fakeR.getId("id", "linear_parent"));
    }

    if (linearLayout1 != null) {
      linearLayout1.addView(horizontalScrollView);
    }
  }

  public void exportInitialDrawableToLocalAndroid(int resource,String folderName,String fileNameOut){
    InputStream in = getResources().openRawResource(resource);
    File folder = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName() + "/" + folderName + "/");
    if(!folder.exists()) {
      folder.mkdirs();
    }
    if(!new File(folder,fileNameOut).exists()) {
      try {
        FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName() + "/" + folderName + "/" + fileNameOut);
        byte[] buff = new byte[1024];
        int read = 0;

        try {
          while ((read = in.read(buff)) > 0) {
            out.write(buff, 0, read);
          }
        } finally {
          in.close();
          out.close();
        }
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }

  public void initialLoadResources(){
    exportInitialDrawableToLocalAndroid(fakeR.getId(this,"raw","budots"),"sample_name","budots.mp3");
  }


}
