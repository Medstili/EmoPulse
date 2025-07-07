package com.medstili.emopulse.fragment;

import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.medstili.emopulse.DataBase.DataBase;
import com.medstili.emopulse.R;
import com.medstili.emopulse.databinding.FragmentBodyScanExerciseBinding;

import java.io.IOException;
import java.util.Locale;

public class BodyScanExerciseFragment extends Fragment {
    private static final String TAG = "BodyScanExerciseFrag";

    private FragmentBodyScanExerciseBinding binding;
    private MediaPlayer mediaPlayer;
    private final Handler handler = new Handler();
    DataBase db ;

    // Runnable to update SeekBar & current time every second
    private final Runnable seekBarUpdater = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                int currentPos = mediaPlayer.getCurrentPosition();
                binding.seekBar.setProgress(currentPos);
                binding.playerPosition.setText(formatTime(currentPos));
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBodyScanExerciseBinding.inflate(inflater, container, false);
        db = DataBase.getInstance();
        View root = binding.getRoot();



        // Disable play/pause until the MediaPlayer is ready:
        binding.playPauseBtn.setEnabled(false);

        // 1) Initialize MediaPlayer according to the official docs:
        setupMediaPlayer_Basic();

        // 2) Wire up Play/Pause button:
        binding.playPauseBtn.setOnClickListener(v -> {
            if (mediaPlayer == null) return;

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                handler.removeCallbacks(seekBarUpdater);
                binding.playPauseBtn.setIcon(
                        AppCompatResources.getDrawable( requireContext(),R.drawable.play_button));
                binding.dotLottieAnimationView.pause();
            } else {
                mediaPlayer.start();
                handler.post(seekBarUpdater);
                binding.playPauseBtn.setIcon(
                        AppCompatResources.getDrawable( requireContext(),R.drawable.pause_button));
                binding.dotLottieAnimationView.play();

            }
        });

        return root;
    }


    private void setupMediaPlayer_Basic() {
        // 1) Create the MediaPlayer instance
        mediaPlayer = new MediaPlayer();

        // 2) Configure audio attributes (so Android knows what type of audio this is)
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
        );

        try {
            // 3) Open your MP3 from assets/ (body_scan_audio.mp3)
            AssetFileDescriptor afd = requireContext()
                    .getAssets()
                    .openFd("body_scan_audio.mp3");

            // 4) Supply the data source to the MediaPlayer (via file descriptor)
            mediaPlayer.setDataSource(
                    afd.getFileDescriptor(),
                    afd.getStartOffset(),
                    afd.getLength()
            );
            afd.close();

            // 5) We want to prepare asynchronously (so the UI thread is not blocked):
            mediaPlayer.setOnPreparedListener(mp -> {
                // Called when MediaPlayer is ready to play
                int duration = mp.getDuration();
                binding.seekBar.setMax(duration);
                binding.playerDuration.setText(formatTime(duration));

                // Enable the play/pause button now that it's prepared
                binding.playPauseBtn.setEnabled(true);
            });

            // 6) Handle playback completion: reset SeekBar + UI
            mediaPlayer.setOnCompletionListener(mp -> {
                handler.removeCallbacks(seekBarUpdater);
                mp.seekTo(0);
                binding.seekBar.setProgress(0);
                binding.playerPosition.setText(formatTime(0));
                binding.playPauseBtn.setIcon(
                        AppCompatResources.getDrawable(requireContext(), R.drawable.play_button)
                );
                binding.dotLottieAnimationView.stop();
                db.recordExerciseCompletion("Body_Scan", new
                        DataBase.CompletionCallback() {

                            @Override
                            public void onSuccess(Integer newCount) {

                                Log.d(TAG, "Exercise completed successfully. New count: " + newCount);
                                Snackbar.make(binding.getRoot(), " Body Scan completed! Count: " + newCount, Snackbar.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(DatabaseError error) {
                                Log.e(TAG, "Failed to record exercise completion: " + error.getMessage());
                            }


                });

            });

            // 7) Handle any errors during playback
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "MediaPlayer Error: what=" + what + " extra=" + extra);
                return true; // true = we handled the error
            });

            // 8) Finally, kick off the background prepare:
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            Log.e(TAG, "Failed to set data source for MediaPlayer", e);
        }

        // 9) Let the SeekBar be seek able once prepared:
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            boolean userDragging = false;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // As user drags, update the current-time label in real time
                    binding.playerPosition.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                userDragging = true;
                handler.removeCallbacks(seekBarUpdater);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                userDragging = false;
                if (mediaPlayer != null) {
                    int newPos = seekBar.getProgress();
                    mediaPlayer.seekTo(newPos);
                    binding.playerPosition.setText(formatTime(newPos));
                }
                handler.postDelayed(seekBarUpdater, 1000);
            }
        });
    }

    private String formatTime(int milliseconds) {
        int totalSeconds = milliseconds / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(seekBarUpdater);
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
