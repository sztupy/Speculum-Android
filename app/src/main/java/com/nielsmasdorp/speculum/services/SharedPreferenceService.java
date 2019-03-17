package com.nielsmasdorp.speculum.services;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nielsmasdorp.speculum.models.Configuration;
import com.nielsmasdorp.speculum.util.Constants;

/**
 * @author Niels Masdorp (NielsMasdorp)
 */
public class SharedPreferenceService {

    private SharedPreferences preferences;

    public SharedPreferenceService(Application application) {

        this.preferences = PreferenceManager.getDefaultSharedPreferences(application);
    }

    public void storeConfiguration(Configuration configuration) {

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(Constants.SP_LOCATION_IDENTIFIER, configuration.getLocation());
        editor.putInt(Constants.SP_POLLING_IDENTIFIER, configuration.getPollingDelay());
        editor.putBoolean(Constants.SP_CELSIUS_IDENTIFIER, configuration.isCelsius());
        editor.putBoolean(Constants.SP_VOICE_IDENTIFIER, configuration.isVoiceCommands());

        editor.apply();
    }

    public Configuration getRememberedConfiguration() {

        Configuration configuration = new Configuration.Builder()
                .location(getLocation())
                .pollingDelay(getPollingDelay())
                .celsius(getCelsius())
                .voiceCommands(getVoiceCommands())
                .build();

        return configuration;
    }

    public String getLocation() {
        return preferences.getString(Constants.SP_LOCATION_IDENTIFIER, null);
    }

    public int getPollingDelay() {
        return preferences.getInt(Constants.SP_POLLING_IDENTIFIER, 0);
    }

    public boolean getCelsius() {
        return preferences.getBoolean(Constants.SP_CELSIUS_IDENTIFIER, false);
    }

    public boolean getVoiceCommands() {
        return preferences.getBoolean(Constants.SP_VOICE_IDENTIFIER, false);
    }

    public void removeConfiguration() {

        SharedPreferences.Editor editor = preferences.edit();

        editor.remove(Constants.SP_LOCATION_IDENTIFIER);
        editor.remove(Constants.SP_POLLING_IDENTIFIER);
        editor.remove(Constants.SP_CELSIUS_IDENTIFIER);
        editor.remove(Constants.SP_VOICE_IDENTIFIER);

        editor.apply();
    }
}