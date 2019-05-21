package video.effects.android.videoeffects;

import android.app.Activity;
import android.content.Context;

/**
 * This class echoes a string called from JavaScript.
 */
public class FakeR {
	private Context context;
	private String packageName;

	public FakeR(Activity activity) {
		context = activity.getApplicationContext();
		packageName = context.getPackageName();
	}

	public FakeR(Context context) {
		this.context = context;
		packageName = context.getPackageName();
	}

	public int getId(String group, String key) {
		return context.getResources().getIdentifier(key, group, packageName);
	}

	public static int getId(Context context, String group, String key) {
		return context.getResources().getIdentifier(key, group, context.getPackageName());
	}
}