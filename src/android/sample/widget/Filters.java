package video.effects.android.videoeffects.sample.widget;

import android.content.Context;
import android.graphics.BitmapFactory;

import video.effects.android.videoeffects.camerarecorder.egl.filter.GlBilateralFilter;
import video.effects.android.videoeffects.camerarecorder.egl.filter.GlBoxBlurFilter;
import video.effects.android.videoeffects.camerarecorder.egl.filter.GlBulgeDistortionFilter;
import video.effects.android.videoeffects.camerarecorder.egl.filter.GlCGAColorspaceFilter;
import video.effects.android.videoeffects.camerarecorder.egl.filter.GlFilter;
import video.effects.android.videoeffects.camerarecorder.egl.filter.GlFilterGroup;
import video.effects.android.videoeffects.camerarecorder.egl.filter.GlGaussianBlurFilter;
import video.effects.android.videoeffects.camerarecorder.egl.filter.GlGrayScaleFilter;
import video.effects.android.videoeffects.camerarecorder.egl.filter.GlInvertFilter;
import video.effects.android.videoeffects.camerarecorder.egl.filter.GlLookUpTableFilter;
import video.effects.android.videoeffects.camerarecorder.egl.filter.GlMonochromeFilter;
import video.effects.android.videoeffects.camerarecorder.egl.filter.GlSepiaFilter;
import video.effects.android.videoeffects.camerarecorder.egl.filter.GlSharpenFilter;
import video.effects.android.videoeffects.camerarecorder.egl.filter.GlSphereRefractionFilter;
import video.effects.android.videoeffects.camerarecorder.egl.filter.GlToneCurveFilter;
import video.effects.android.videoeffects.camerarecorder.egl.filter.GlToneFilter;
import video.effects.android.videoeffects.camerarecorder.egl.filter.GlVignetteFilter;
import video.effects.android.videoeffects.camerarecorder.egl.filter.GlWeakPixelInclusionFilter;
import video.effects.android.videoeffects.FakeR;

import java.io.InputStream;

public enum Filters {
    NORMAL,
    BILATERAL,
    BOX_BLUR,
    BULGE_DISTORTION,
    CGA_COLOR_SPACE,
    GAUSSIAN_BLUR,
    GLAY_SCALE,
    INVERT,
    LOOKUP_TABLE,
    MONOCHROME,
    OVERLAY,
    SEPIA,
    SHARPEN,
    SPHERE_REFRACTION,
    TONE_CURVE,
    TONE,
    VIGNETTE,
    WEAKPIXELINCLUSION,
    FILTER_GROUP;
	private static FakeR fakeR;

    public static GlFilter getFilterInstance(Filters filter, Context context) {
		fakeR = new FakeR(context);
        switch (filter) {
            case BILATERAL:
                return new GlBilateralFilter();
            case BOX_BLUR:
                return new GlBoxBlurFilter();
            case BULGE_DISTORTION:
                return new GlBulgeDistortionFilter();
            case CGA_COLOR_SPACE:
                return new GlCGAColorspaceFilter();
            case GAUSSIAN_BLUR:
                return new GlGaussianBlurFilter();
            case GLAY_SCALE:
                return new GlGrayScaleFilter();
            case INVERT:
                return new GlInvertFilter();
            case LOOKUP_TABLE:
                return new GlLookUpTableFilter(BitmapFactory.decodeResource(context.getResources(), fakeR.getId("drawable", "lookup_sample")));
            case MONOCHROME:
                return new GlMonochromeFilter();
            case OVERLAY:
                return new GlBitmapOverlaySample(BitmapFactory.decodeResource(context.getResources(), fakeR.getId("mipmap", "ic_launcher_round")));
            case SEPIA:
                return new GlSepiaFilter();
            case SHARPEN:
                return new GlSharpenFilter();
            case SPHERE_REFRACTION:
                return new GlSphereRefractionFilter();
            case TONE_CURVE:
                try {
                    InputStream inputStream = context.getAssets().open("acv/tone_cuver_sample.acv");
                    return new GlToneCurveFilter(inputStream);
                } catch (Exception e) {
                    return new GlFilter();
                }
            case TONE:
                return new GlToneFilter();
            case VIGNETTE:
                return new GlVignetteFilter();
            case WEAKPIXELINCLUSION:
                return new GlWeakPixelInclusionFilter();
            case FILTER_GROUP:
                return new GlFilterGroup(new GlMonochromeFilter(), new GlVignetteFilter());

            default:
                return new GlFilter();
        }

    }

}
