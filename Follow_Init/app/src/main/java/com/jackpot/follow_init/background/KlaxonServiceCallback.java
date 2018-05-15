package com.jackpot.follow_init.background;

import android.media.MediaPlayer;
import android.net.Uri;

public interface KlaxonServiceCallback {
    void stopSelf();

    Uri getDefaultUri(int type);

    MediaPlayer createMediaPlayer();
}