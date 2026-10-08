package com.example.voltesvsuperrobotstrike;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.example.voltesvsuperrobotstrike.audio.GameMusicManager;

/** Application owner for process-wide music and foreground/background state. */
public final class VoltesVApplication extends Application {

    private GameMusicManager musicManager;
    private int startedActivityCount;

    @Override
    public void onCreate() {
        super.onCreate();
        musicManager = GameMusicManager.getInstance(this);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityStarted(Activity activity) {
                if (startedActivityCount == 0) {
                    musicManager.onAppForeground();
                }
                startedActivityCount++;
            }

            @Override
            public void onActivityStopped(Activity activity) {
                startedActivityCount = Math.max(0, startedActivityCount - 1);
                if (startedActivityCount == 0) {
                    musicManager.onAppBackground();
                }
            }

            @Override
            public void onActivityCreated(
                    Activity activity,
                    Bundle savedInstanceState
            ) {
                // No Activity reference is retained.
            }

            @Override
            public void onActivityResumed(Activity activity) {
                // Foreground detection is based on started Activities.
            }

            @Override
            public void onActivityPaused(Activity activity) {
                // Individual Activity pauses must not restart menu music.
            }

            @Override
            public void onActivitySaveInstanceState(
                    Activity activity,
                    Bundle outState
            ) {
                // No audio state is persisted across process death.
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                // The manager owns only application context and MediaPlayers.
            }
        });
    }

    public GameMusicManager getMusicManager() {
        return musicManager;
    }
}
