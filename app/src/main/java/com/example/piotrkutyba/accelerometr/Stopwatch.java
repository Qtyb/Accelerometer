package com.example.piotrkutyba.accelerometr;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Stopwatch {

    private long startTime;
    private long finishTime;

    public void turnOnStopwatch(){                           //właczenie stopera
        startTime = System.currentTimeMillis();
    }

    public long getMeasurement(){                           // uzyskanie pomiaru w milisekundach
        finishTime = System.currentTimeMillis();

        return finishTime - startTime;
    }

    public String printMeasurement(){                       //ładnie opisane wyniki pomiarów
        finishTime = getMeasurement();

        int miliseconds = (int) (finishTime % 1000);
        int seconds = (int) ((finishTime / 1000) % 60);
        int minutes = (int) ((finishTime / 60000) % 60);
        int hours = (int) ((finishTime / 3600000) % 24);

        String measuredTime = "Your time: " + Integer.toString(hours) + " Hours "
                                            + Integer.toString(minutes) + " Minutes "
                                            + Integer.toString(seconds) + " Seconds "
                                            + Integer.toString(miliseconds) + " Miliseconds";

        return  measuredTime;
    }
}
