package com.ascendantbrain.android.bakingapp.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ascendantbrain.android.bakingapp.R;
import com.ascendantbrain.android.bakingapp.model.Step;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.Locale;

import static com.ascendantbrain.android.bakingapp.utils.ExoPlayerHelper.getMediaSession;
import static com.ascendantbrain.android.bakingapp.utils.ExoPlayerHelper.initializePlayer;
import static com.ascendantbrain.android.bakingapp.utils.ExoPlayerHelper.prepareMedia;
import static com.ascendantbrain.android.bakingapp.utils.ExoPlayerHelper.releasePlayer;

public class StepDetailFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "StepDetailFragment";

    public static final String ARGS_STEP = "arg_step";

    private Step mStep;
    private MediaSessionCompat mMediaSession;
    private SimpleExoPlayer mPlayer;
    private NavigationListener mNavigationListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StepDetailFragment() {}

    public static StepDetailFragment getInstance(Step step) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_STEP, step);
        StepDetailFragment fragment = new StepDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (null != args && args.containsKey(ARGS_STEP)) {
            mStep = args.getParcelable(ARGS_STEP);
        }

        if(getActivity() instanceof NavigationListener) {
            mNavigationListener = (NavigationListener) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.step_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mStep != null) {
            String navLabel = (mStep.getId() == 0)
                    ? "Introduction"
                    : String.format(Locale.US,"Step %d",mStep.getId());
            ((TextView) view.findViewById(R.id.nav_label)).setText(navLabel);
            ((TextView) view.findViewById(R.id.step_description)).setText(mStep.getDescription());
            view.findViewById(R.id.nav_previous).setOnClickListener(this);
            view.findViewById(R.id.nav_next).setOnClickListener(this);
            PlayerView playerView = view.findViewById(R.id.step_exoplayer);
            setupPlayer(playerView);
        }
    }

    private void setupPlayer(PlayerView playerView) {
        FragmentActivity activity = getActivity();
        if(activity==null) return;

        mPlayer = initializePlayer(playerView);
        mMediaSession = getMediaSession(activity);
        if(mStep.getVideoURL().isEmpty()) {
            playerView.setVisibility(View.GONE);
        } else {
            prepareMedia(activity,mPlayer,mStep.getVideoURL());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer(mPlayer,null);
    }

    @Override
    public void onClick(View view) {
        if(mNavigationListener == null) return;  // no one listening so ignore
        switch(view.getId()) {
            case R.id.nav_previous:
                mNavigationListener.onPreviousClicked();
                break;
            case R.id.nav_next:
                mNavigationListener.onNextClicked();
                break;
            default:
                Log.w(TAG,"Requested navigation action has not yet been implemented.");
        }
    }
}
