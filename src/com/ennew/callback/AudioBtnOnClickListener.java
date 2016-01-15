package com.ennew.callback;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.ennew.Application;
import com.ennew.R;
import com.ennew.config.MyConstant;
import com.ennew.db.DataBaseManager;
import com.ennew.model.MessageInfo;
import com.ennew.utils.FileUtil;
import com.ennew.utils.SharedPrefUtil;

import java.io.File;

public class AudioBtnOnClickListener implements OnClickListener {

    private MediaPlayer mMediaPlayer;

    private ImageView mImageView;

    private MessageInfo mInfo;

    private AnimationDrawable voiceAnimation;

    public static boolean isPlaying = false;

    public static AudioBtnOnClickListener audioBtnOnClickListener = null;

    public static String messageId;

    private String filePath = "";

    Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                mImageView.setVisibility(View.VISIBLE);
                mInfo.setAudioFilePath(filePath);
                if (!TextUtils.isEmpty(filePath)) {
                    startPlayVoice(filePath);
                }
            }
        }
    };

    public AudioBtnOnClickListener(ImageView mImageView, MessageInfo info) {
        this.mImageView = mImageView;
        this.mInfo = info;
        this.filePath = info.getAudioFilePath();
    }

    @Override
    public void onClick(View v) {
        // 如果正在播放先停止
        if (isPlaying) {
            if (!TextUtils.equals(messageId, mInfo.getMessageId())
                    && audioBtnOnClickListener != null) {
                audioBtnOnClickListener.stopPlayVoice();
                return;
            }
            stopPlayVoice();
        }

        if (!TextUtils.isEmpty(filePath)) {
            //判断录音文件是否存在，防止人为删除
            if (!new File(filePath).exists()) {
                new Thread() {
                    public void run() {
                        try {
                            filePath = FileUtil.createAudioCacheFilePath("IM_"
                                    + System.currentTimeMillis() + ".amr");
                            filePath = FileUtil.decoderBase64File(mInfo.getContent(), Base64.DEFAULT, filePath);
                            mInfo.setAudioFilePath(filePath);
                            DataBaseManager.getInstance().updateAudioFilePathByMessageId(mInfo);
                            handler.sendEmptyMessage(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            } else {
                startPlayVoice(filePath);
            }
        } else {
            if (TextUtils.isEmpty(mInfo.getAudioFilePath())) {
                this.mImageView.setVisibility(View.INVISIBLE);
                new Thread() {
                    public void run() {
                        try {
                            filePath = FileUtil.createAudioCacheFilePath("IM_"
                                    + System.currentTimeMillis() + ".amr");
                            filePath = FileUtil.decoderBase64File(mInfo.getContent(), Base64.DEFAULT, filePath);
                            mInfo.setAudioFilePath(filePath);
                            DataBaseManager.getInstance().updateAudioFilePathByMessageId(mInfo);
                            handler.sendEmptyMessage(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        }
    }

    public void startPlayVoice(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        mMediaPlayer = new MediaPlayer();
        try {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            // 设置要播放的文件
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (mp != null) {
                        // 播放
                        mp.start();
                        showAnimation();
                        isPlaying = true;
                        audioBtnOnClickListener = AudioBtnOnClickListener.this;
                    }
                }
            });
            mMediaPlayer.prepareAsync();
            mMediaPlayer
                    .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mMediaPlayer.release();
                            mMediaPlayer = null;
                            stopPlayVoice();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 显示长在播放录音的动画
    private void showAnimation() {
        String name = (String) SharedPrefUtil.getStateString(
                Application.getInstance(), MyConstant.USER_NAME);
        voiceAnimation = new AnimationDrawable();
        Context context = Application.getInstance().getApplicationContext();
        if (!mInfo.getSenderName().equals(name)) {
            voiceAnimation.addFrame(context.getResources().getDrawable(R.drawable.chatfrom_voice_playing_f1), 200);
            voiceAnimation.addFrame(context.getResources().getDrawable(R.drawable.chatfrom_voice_playing_f2), 200);
            voiceAnimation.addFrame(context.getResources().getDrawable(R.drawable.chatfrom_voice_playing_f3), 200);
        } else {
            voiceAnimation.addFrame(context.getResources().getDrawable(R.drawable.chatto_voice_playing_f1), 200);
            voiceAnimation.addFrame(context.getResources().getDrawable(R.drawable.chatto_voice_playing_f2), 200);
            voiceAnimation.addFrame(context.getResources().getDrawable(R.drawable.chatto_voice_playing_f3), 200);
        }
        mImageView.setImageDrawable(voiceAnimation);
        voiceAnimation.setOneShot(false);
        voiceAnimation.start();

    }

    public void stopPlayVoice() {
        if (voiceAnimation != null) {
            voiceAnimation.stop();
        }
        String name = (String) SharedPrefUtil.getStateString(
                Application.getInstance(), MyConstant.USER_NAME);
        if (!mInfo.getSenderName().equals(name)) {
            mImageView.setImageResource(R.drawable.chatfrom_voice_playing);
        } else {
            mImageView.setImageResource(R.drawable.chatto_voice_playing);
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        audioBtnOnClickListener = null;
        isPlaying = false;
    }

}
