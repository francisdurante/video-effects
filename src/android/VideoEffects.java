package video.effects.android.videoeffects;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PermissionHelper;
import android.content.Context;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import video.effects.android.videoeffects.sample.MainActivity;

/**
 * This class echoes a string called from JavaScript.
 */
public class VideoEffects extends CordovaPlugin {
    public static CallbackContext callbackContext;
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
      this.callbackContext = callbackContext;
        if (action.equals("showCamera")) {
          String path = "";
          path = args.getString(0) == null ? "" : args.getString(0);
          this.coolMethod(path);
          return true;
        }
        return false;
    }

    private void coolMethod(String musicPath) {
		    Context context = this.cordova.getActivity().getApplicationContext();
        openNewActivity(context,musicPath);
    }

	private void openNewActivity(Context context, String musicPath) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("MUSIC_PATH",musicPath);
        this.cordova.getActivity().startActivity(intent);
    }
}
