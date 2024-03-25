package eu.time.discordbot.crypto;

import java.time.LocalDateTime;

public class UntilHalving {
    private final long tilHalving;
    private final int daysCalc;
    private final int hoursCalc;
    private final int minutesCalc;
    private final int secondsCalc;
    private final LocalDateTime halvingDate;

    public UntilHalving(long tilHalving, int daysCalc, int hoursCalc, int minutesCalc, int secondsCalc, LocalDateTime halvingDate) {
        this.tilHalving = tilHalving;
        this.daysCalc = daysCalc;
        this.hoursCalc = hoursCalc;
        this.minutesCalc = minutesCalc;
        this.secondsCalc = secondsCalc;
        this.halvingDate = halvingDate;
    }

    public long getTilHalving() {
        return tilHalving;
    }

    public int getDaysCalc() {
        return daysCalc;
    }

    public int getHoursCalc() {
        return hoursCalc;
    }

    public int getMinutesCalc() {
        return minutesCalc;
    }

    public int getSecondsCalc() {
        return secondsCalc;
    }

    public LocalDateTime getHalvingDate() {
        return halvingDate;
    }
}
