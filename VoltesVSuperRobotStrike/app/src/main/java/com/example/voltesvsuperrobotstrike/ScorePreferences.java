package com.example.voltesvsuperrobotstrike;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Stores the single local personal-best score for the game.
 */
public final class ScorePreferences {

    public static final String PREFERENCES_NAME = "voltes_v_game_preferences";
    public static final String KEY_HIGH_SCORE = "high_score";

    private ScorePreferences() {
    }

    public static int getHighScore(Context context) {
        return getPreferences(context).getInt(KEY_HIGH_SCORE, 0);
    }

    public static void saveHighScore(Context context, int score) {
        getPreferences(context)
                .edit()
                .putInt(KEY_HIGH_SCORE, Math.max(0, score))
                .apply();
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
}
