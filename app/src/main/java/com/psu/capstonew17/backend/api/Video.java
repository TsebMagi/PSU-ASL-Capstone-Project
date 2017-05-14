//MIT License Copyright 2017 PSU ASL Capstone Team

package com.psu.capstonew17.backend.api;
import android.media.MediaPlayer;
import android.os.Parcelable;

public interface Video extends Parcelable {
    /**
     * Configure the supplied MediaPlayer to play this video. Note that this operation may cause
     * errors inside the media player, so you should set its OnErrorListener before calling this
     * method.
     */
    void configurePlayer(MediaPlayer player);
}
