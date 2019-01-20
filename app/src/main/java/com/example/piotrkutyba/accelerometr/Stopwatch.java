package com.example.piotrkutyba.accelerometr;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Stopwatch{

    private long startTime;
    private long finishTime;

    protected void turnOnStopwatch(){                           //właczenie stopera
        startTime = System.currentTimeMillis();
        finishTime = -1;
    }
    protected String printForStopWatch(){

        long tmpTime = System.currentTimeMillis()-startTime;

        int miliseconds = (int) (tmpTime % 1000);
        int seconds = (int) ((tmpTime / 1000) % 60);
        int minutes = (int) ((tmpTime / 60000) % 60);
        int hours = (int) ((tmpTime / 3600000) % 24);

        String tmpMilSec = Integer.toString(miliseconds);
        String tmpSec = Integer.toString(seconds);
        String tmpMin = Integer.toString(minutes);
        String tmpHrs = Integer.toString(hours);

        if(miliseconds<100){
            tmpMilSec = "0";
            if(miliseconds<10){
                tmpMilSec = "00";
            }
            tmpMilSec += Integer.toString(miliseconds);
        }
        if(seconds<10)
            tmpSec = "0" + Integer.toString(seconds);
        if(minutes<10)
            tmpMin = "0" + Integer.toString(minutes);
        if(hours<10)
            tmpHrs = "0" + Integer.toString(hours);


        String measuredTime =tmpHrs + ":" + tmpMin + ":"
                + tmpSec + ":" + tmpMilSec;

        return  measuredTime;
    }
    protected long getMeasurement(int Aparam){
        long raceTime;
        finishTime = System.currentTimeMillis();
        switch (Aparam) {
            default:                                     //milisekndy
                raceTime=finishTime - startTime;
            case 1:                                     //sekundy
                raceTime=finishTime - startTime/1000;
            case 2:                                     //minuty
                raceTime=finishTime - startTime/(1000*60);
        }
        return raceTime;
    }
    protected void resetStopwatch(){
        startTime = 0;
        finishTime = 0;
    }
    public String printMeasurement(){                       //ładnie opisane wyniki pomiarów
        finishTime = getMeasurement(0);

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
