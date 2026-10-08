package com.example.voltesvsuperrobotstrike.audio;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.example.voltesvsuperrobotstrike.R;

/** Centralized lifecycle-safe manager for the two looping background tracks. */
public final class GameMusicManager {

    private static final float MUSIC_VOLUME = 0.55f;

    private static GameMusicManager instance;

    private final Context applicationContext;
    private final AudioManager audioManager;
    private final AudioFocusRequest audioFocusRequest;

    private MediaPlayer menuMediaPlayer;
    private MediaPlayer gameplayMediaPlayer;
    private MusicTrack desiredTrack = MusicTrack.NONE;
    private boolean appInForeground;
    private boolean gameplayPausedByMenu;
    private boolean audioFocusGranted;

    public enum MusicTrack {
        NONE,
        MENU,
        GAMEPLAY
    }

    private GameMusicManager(Context context) {
        applicationContext = context.getApplicationContext();
        audioManager = (AudioManager) applicationContext.getSystemService(
                Context.AUDIO_SERVICE
        );
        audioFocusRequest = new AudioFocusRequest.Builder(
                AudioManager.AUDIOFOCUS_GAIN
        )
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build())
                .setWillPauseWhenDucked(true)
                .setOnAudioFocusChangeListener(this::handleAudioFocusChange)
                .build();
    }

    public static GameMusicManager getInstance(Context context) {
        synchronized (GameMusicManager.class) {
            if (instance == null) {
                instance = new GameMusicManager(context);
            }
            return instance;
        }
    }

    public synchronized void playMenuMusic() {
        desiredTrack = MusicTrack.MENU;
        gameplayPausedByMenu = false;
        ensureDesiredTrackPlaying();
    }

    public synchronized void startGameplayMusicForNewMission() {
        desiredTrack = MusicTrack.GAMEPLAY;
        gameplayPausedByMenu = false;
        pausePlayer(menuMediaPlayer);

        MediaPlayer player = ensureGameplayPlayer();
        if (player != null) {
            seekToStart(player);
        }
        ensureDesiredTrackPlaying();
    }

    public synchronized void pauseGameplayForMenu() {
        desiredTrack = MusicTrack.GAMEPLAY;
        gameplayPausedByMenu = true;
        pausePlayer(gameplayMediaPlayer);
    }

    public synchronized void resumeGameplayFromMenu() {
        if (desiredTrack != MusicTrack.GAMEPLAY) {
            return;
        }

        gameplayPausedByMenu = false;
        ensureDesiredTrackPlaying();
    }

    public synchronized void switchToMenuMusic() {
        playMenuMusic();
    }

    public synchronized void onAppForeground() {
        appInForeground = true;
        ensureDesiredTrackPlaying();
    }

    public synchronized void onAppBackground() {
        appInForeground = false;
        pausePlayer(menuMediaPlayer);
        pausePlayer(gameplayMediaPlayer);
        abandonAudioFocus();
    }

    public synchronized void stopAll() {
        desiredTrack = MusicTrack.NONE;
        gameplayPausedByMenu = false;
        releasePlayer(menuMediaPlayer);
        releasePlayer(gameplayMediaPlayer);
        menuMediaPlayer = null;
        gameplayMediaPlayer = null;
        abandonAudioFocus();
    }

    public synchronized void release() {
        stopAll();
    }

    private void ensureDesiredTrackPlaying() {
        if (!appInForeground || !requestAudioFocus()) {
            return;
        }

        if (desiredTrack == MusicTrack.MENU) {
            pausePlayer(gameplayMediaPlayer);
            MediaPlayer player = ensureMenuPlayer();
            startPlayer(player);
            return;
        }

        if (desiredTrack == MusicTrack.GAMEPLAY) {
            pausePlayer(menuMediaPlayer);
            if (gameplayPausedByMenu) {
                pausePlayer(gameplayMediaPlayer);
                return;
            }

            MediaPlayer player = ensureGameplayPlayer();
            startPlayer(player);
        }
    }

    private MediaPlayer ensureMenuPlayer() {
        if (menuMediaPlayer == null) {
            menuMediaPlayer = createPlayer(
                    R.raw.music_mainmenu,
                    MusicTrack.MENU
            );
        }
        return menuMediaPlayer;
    }

    private MediaPlayer ensureGameplayPlayer() {
        if (gameplayMediaPlayer == null) {
            gameplayMediaPlayer = createPlayer(
                    R.raw.music_gameplay,
                    MusicTrack.GAMEPLAY
            );
        }
        return gameplayMediaPlayer;
    }

    private MediaPlayer createPlayer(int resourceId, MusicTrack track) {
        MediaPlayer player;
        try {
            player = MediaPlayer.create(applicationContext, resourceId);
        } catch (RuntimeException exception) {
            return null;
        }

        if (player == null) {
            return null;
        }

        try {
            player.setLooping(true);
            player.setVolume(MUSIC_VOLUME, MUSIC_VOLUME);
            player.setOnErrorListener((mediaPlayer, what, extra) -> {
                handlePlayerError(track, mediaPlayer);
                return true;
            });
        } catch (RuntimeException exception) {
            releasePlayer(player);
            return null;
        }

        return player;
    }

    private synchronized void handlePlayerError(
            MusicTrack track,
            MediaPlayer player
    ) {
        if (track == MusicTrack.MENU && menuMediaPlayer == player) {
            releasePlayer(menuMediaPlayer);
            menuMediaPlayer = null;
        } else if (track == MusicTrack.GAMEPLAY
                && gameplayMediaPlayer == player) {
            releasePlayer(gameplayMediaPlayer);
            gameplayMediaPlayer = null;
        }
    }

    private boolean requestAudioFocus() {
        if (audioFocusGranted) {
            return true;
        }
        if (audioManager == null) {
            return false;
        }

        int result;
        try {
            result = audioManager.requestAudioFocus(audioFocusRequest);
        } catch (RuntimeException exception) {
            return false;
        }

        audioFocusGranted = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        return audioFocusGranted;
    }

    private void abandonAudioFocus() {
        if (audioManager == null || !audioFocusGranted) {
            audioFocusGranted = false;
            return;
        }

        try {
            audioManager.abandonAudioFocusRequest(audioFocusRequest);
        } catch (RuntimeException exception) {
            // Audio focus cleanup must never interrupt Activity navigation.
        }
        audioFocusGranted = false;
    }

    private void handleAudioFocusChange(int focusChange) {
        synchronized (this) {
            if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                audioFocusGranted = true;
                ensureDesiredTrackPlaying();
                return;
            }

            if (focusChange == AudioManager.AUDIOFOCUS_LOSS
                    || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
                    || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                audioFocusGranted = false;
                pausePlayer(menuMediaPlayer);
                pausePlayer(gameplayMediaPlayer);
            }
        }
    }

    private void startPlayer(MediaPlayer player) {
        if (player == null) {
            return;
        }

        try {
            if (!player.isPlaying()) {
                player.start();
            }
        } catch (RuntimeException exception) {
            handlePlayerError(
                    player == menuMediaPlayer
                            ? MusicTrack.MENU
                            : MusicTrack.GAMEPLAY,
                    player
            );
        }
    }

    private void pausePlayer(MediaPlayer player) {
        if (player == null) {
            return;
        }

        try {
            if (player.isPlaying()) {
                player.pause();
            }
        } catch (RuntimeException exception) {
            handlePlayerError(
                    player == menuMediaPlayer
                            ? MusicTrack.MENU
                            : MusicTrack.GAMEPLAY,
                    player
            );
        }
    }

    private void seekToStart(MediaPlayer player) {
        if (player == null) {
            return;
        }

        try {
            player.seekTo(0);
        } catch (RuntimeException exception) {
            handlePlayerError(MusicTrack.GAMEPLAY, player);
        }
    }

    private void releasePlayer(MediaPlayer player) {
        if (player == null) {
            return;
        }

        try {
            player.release();
        } catch (RuntimeException exception) {
            // Release is best effort; never crash during lifecycle cleanup.
        }
    }
}
