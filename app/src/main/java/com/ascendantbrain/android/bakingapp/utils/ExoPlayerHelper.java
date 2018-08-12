package com.ascendantbrain.android.bakingapp.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.ascendantbrain.android.bakingapp.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class ExoPlayerHelper {

    public static SimpleExoPlayer initializePlayer(PlayerView playerView) {
        Context context = playerView.getContext();

        /* Create default TrackSelector  */

        // Measures bandwidth during playback. Null if not required
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        // Setup selector to select which trackes the renderers will consume
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        // setup exoplayer
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        player.setPlayWhenReady(true);

        // Bind the player to the view.
        playerView.setPlayer(player);

        return player;
    }

    public static MediaSessionCompat getMediaSession(Activity activity) {
        String packageName = activity.getPackageName();
        String className = activity.getLocalClassName();
        ComponentName componentName = new ComponentName(packageName,className);

        MediaSessionCompat session = new MediaSessionCompat(activity,
                "ExoPlayerSession",
                componentName,null);

        PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder();
        builder.setActions(
                PlaybackStateCompat.ACTION_PLAY |
                PlaybackStateCompat.ACTION_PAUSE |
                PlaybackStateCompat.ACTION_FAST_FORWARD
        );
        session.setPlaybackState(builder.build());
        session.isActive();
        return session;
    }

    public static void prepareMedia(@NonNull Activity activity,
                                    @NonNull ExoPlayer player,
                                    @NonNull String videoUri) {
        if(videoUri.isEmpty()) return;
        Uri mediaUri = Uri.parse(videoUri);
        String applicationName = activity.getResources().getResourceName(R.string.app_name);
        String userAgent = Util.getUserAgent(activity,applicationName);

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(activity,userAgent);
        ExtractorsFactory extractorFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .setExtractorsFactory(extractorFactory)
                .createMediaSource(mediaUri);
        player.prepare(mediaSource);
    }

    public static void releasePlayer(@NonNull ExoPlayer player,
                                     @Nullable Player.EventListener listener ) {
        if(listener != null) player.removeListener(listener);
        player.release();
    }
}
