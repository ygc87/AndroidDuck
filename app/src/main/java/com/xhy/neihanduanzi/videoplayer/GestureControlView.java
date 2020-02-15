package com.xhy.neihanduanzi.videoplayer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * author  yangc
 * date 2018/3/23
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:  手势控制view
 */

class GestureControlView extends FrameLayout {
    /***调整进度布局,控制音频，亮度布局***/
    private View dialogProLayout, exoAudioLayout, exoBrightnessLayout;
    /***水印,封面图占位,显示音频和亮度布图*/
    private ImageView videoAudioImg, videoBrightnessImg;
    /***显示音频和亮度*/
    private ProgressBar videoAudioPro, videoBrightnessPro;
    /***视视频标题,清晰度切换,实时视频,加载速度显示,控制进度*/
    private TextView videoDialogProText;

    public GestureControlView(@NonNull Context context) {
        this(context, null);
    }

    public GestureControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(Color.TRANSPARENT);
        int videoProgressId = chuangyuan.ycj.videolibrary.R.layout.simple_exo_video_progress_dialog;
        int audioId = chuangyuan.ycj.videolibrary.R.layout.simple_video_audio_brightness_dialog;
        int brightnessId = chuangyuan.ycj.videolibrary.R.layout.simple_video_audio_brightness_dialog;
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, chuangyuan.ycj.videolibrary.R.styleable.GestureControlView, 0, 0);
            try {
                audioId = a.getResourceId(chuangyuan.ycj.videolibrary.R.styleable.GestureControlView_player_gesture_audio_layout_id, audioId);
                videoProgressId = a.getResourceId(chuangyuan.ycj.videolibrary.R.styleable.GestureControlView_player_gesture_progress_layout_id, videoProgressId);
                brightnessId = a.getResourceId(chuangyuan.ycj.videolibrary.R.styleable.GestureControlView_player_gesture_bright_layout_id, brightnessId);

            } finally {
                a.recycle();
            }
        }
        intiGestureView(audioId, brightnessId, videoProgressId);
    }

    /***
     * 初始化手势布局view
     * @param audioId 音频布局id
     * @param brightnessId 亮度布局id
     * @param videoProgressId 进度布局id
     */
    protected void intiGestureView(int audioId, int brightnessId, int videoProgressId) {
        exoAudioLayout = inflate(getContext(), audioId, null);
        exoBrightnessLayout = inflate(getContext(), brightnessId, null);
        dialogProLayout = inflate(getContext(), videoProgressId, null);
        dialogProLayout.setVisibility(GONE);
        exoAudioLayout.setVisibility(GONE);
        exoBrightnessLayout.setVisibility(GONE);
        addView(dialogProLayout, getChildCount());
        addView(exoAudioLayout, getChildCount());
        addView(exoBrightnessLayout, getChildCount());
        if (audioId == chuangyuan.ycj.videolibrary.R.layout.simple_video_audio_brightness_dialog) {
            videoAudioImg = exoAudioLayout.findViewById(chuangyuan.ycj.videolibrary.R.id.exo_video_audio_brightness_img);
            videoAudioPro = exoAudioLayout.findViewById(chuangyuan.ycj.videolibrary.R.id.exo_video_audio_brightness_pro);
        }
        if (brightnessId == chuangyuan.ycj.videolibrary.R.layout.simple_video_audio_brightness_dialog) {
            videoBrightnessImg = exoBrightnessLayout.findViewById(chuangyuan.ycj.videolibrary.R.id.exo_video_audio_brightness_img);
            videoBrightnessPro = exoBrightnessLayout.findViewById(chuangyuan.ycj.videolibrary.R.id.exo_video_audio_brightness_pro);
        }
        if (videoProgressId == chuangyuan.ycj.videolibrary.R.layout.simple_exo_video_progress_dialog) {
            videoDialogProText = dialogProLayout.findViewById(chuangyuan.ycj.videolibrary.R.id.exo_video_dialog_pro_text);
        }
    }

    /***
     * 显示隐藏手势布局
     *
     * @param visibility 状态
     */
    protected void showGesture(int visibility) {
        if (exoAudioLayout != null) {
            exoAudioLayout.setVisibility(visibility);
        }
        if (exoBrightnessLayout != null) {
            exoBrightnessLayout.setVisibility(visibility);
        }
        if (dialogProLayout != null) {
            dialogProLayout.setVisibility(visibility);
        }
    }

    /**
     *
     * **/
    public void setTimePosition(@NonNull SpannableString seekTime) {
        if (dialogProLayout != null) {
            dialogProLayout.setVisibility(View.VISIBLE);
            videoDialogProText.setText(seekTime);
        }
    }

    /**
     *
     * **/
    public void setVolumePosition(int mMaxVolume, int currIndex) {
        if (exoAudioLayout != null) {
            if (exoAudioLayout.getVisibility() != VISIBLE) {
                videoAudioPro.setMax(mMaxVolume);
            }
            exoAudioLayout.setVisibility(View.VISIBLE);
            videoAudioPro.setProgress(currIndex);
            videoAudioImg.setImageResource(currIndex == 0 ? chuangyuan.ycj.videolibrary.R.drawable.ic_volume_off_white_48px : chuangyuan.ycj.videolibrary.R.drawable.ic_volume_up_white_48px);
        }
    }
    /**
     *
     * **/
    public void setBrightnessPosition(int mMaxVolume, int currIndex) {
        if (exoBrightnessLayout != null) {
            if (exoBrightnessLayout.getVisibility() != VISIBLE) {
                videoBrightnessPro.setMax(mMaxVolume);
                videoBrightnessImg.setImageResource(chuangyuan.ycj.videolibrary.R.drawable.ic_brightness_6_white_48px);
            }
            exoBrightnessLayout.setVisibility(View.VISIBLE);
            videoBrightnessPro.setProgress(currIndex);
        }
    }

    public View getDialogProLayout() {
        return dialogProLayout;
    }

    public View getExoAudioLayout() {
        return exoAudioLayout;
    }

    public View getExoBrightnessLayout() {
        return exoBrightnessLayout;
    }
}
