package com.nielsmasdorp.speculum.models;

/**
 * @author Niels Masdorp (NielsMasdorp)
 */
public class Configuration {

    private boolean celsius;
    private String location;
    private int pollingDelay;
    private boolean voiceCommands;

    public static class Builder {

        private boolean celsius;
        private String location;
        private int pollingDelay;
        private boolean voiceCommands;

        public Builder celsius(boolean celsius) {
            this.celsius = celsius;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder pollingDelay(int pollingDelay) {
            this.pollingDelay = pollingDelay;
            return this;
        }

        public Builder voiceCommands(boolean voiceCommands) {
            this.voiceCommands = voiceCommands;
            return this;
        }

        public Configuration build() {

            return new Configuration(this);
        }
    }

    private Configuration(Builder builder) {

        this.celsius = builder.celsius;
        this.location = builder.location;
        this.pollingDelay = builder.pollingDelay;
        this.voiceCommands = builder.voiceCommands;
    }

    public boolean isCelsius() {
        return celsius;
    }

    public void setCelsius(boolean celsius) {
        this.celsius = celsius;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getPollingDelay() {
        return pollingDelay;
    }

    public void setPollingDelay(int pollingDelay) {
        this.pollingDelay = pollingDelay;
    }

    public boolean isVoiceCommands() {
        return voiceCommands;
    }

    public void setVoiceCommands(boolean voiceCommands) {
        this.voiceCommands = voiceCommands;
    }
}
