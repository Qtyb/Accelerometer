package com.example.piotrkutyba.accelerometr;//nie zostały uwzględnione spłaszczenia na biegunach


import android.util.Log;

import com.example.piotrkutyba.accelerometr.MainActivity;
import com.google.android.gms.maps.model.LatLng;

import java.util.concurrent.TimeUnit;

//ta klasa obl iczy tylko przemieszenie chwilowe
public class NewLocation {

   // Coordinates Last_coordinates = new Coordinates(MainActivity.coordinates.latitude, MainActivity.coordinates.longitude);  // do podmiany, potrzebna
    // jakas funkcja do
    // uzyskiwania wspolrzednych

    double initialVelocityX = 0;
    double initialVelocityY = 0;

    public Coordinates getNewLocation(double x_acceleration, double y_acceleartion, double z_acceleartion) throws InterruptedException {
    //    Log.d("NewLocation: ","getNewLocation Start");
        //całkowanie metodą trapezów (byloby, ale w tej wersji nie tutaj :D)
        double delta_x=0;  //szukane przemieszczenie w kierunku osi x
        double delta_y=0;  //szukane przemieszczenie w kierunku osi y

        float time = 1080; // milisekund trzeba uzyskac ten czas zmian

        //delta_x = (x_acceleration / 2) * Math.pow(time,2) / 1000; //ruch jednostajnie zmienny
        //
        // delta_y = (y_acceleartion / 2) * Math.pow(time,2) / 1000;


        // przemieszenie w kierunku x to całka z prędkości w kierunku x  dt
        // prędkość w kierunku x to całka z przyspieszenia w kierunku x dt

        // całkowanie numeryczne metodą trapezow

        int numberOfPeriod = (int)time / 8;
        float TheHeightOfTheTrapezoid = time / numberOfPeriod; //odległość między sąsiedimi punktami
        //pod wykresem
        long timeSleep = (long) TheHeightOfTheTrapezoid;

        double acceleration_X[] = new double [9];   // tablice przechowywujace wszystkie poprzednie
        double acceleration_y[] = new double [9];   // wartosci przyspieszenia

        double velocity_X[] = new double [9];    //tablice przechowywujace tymczasowe wertosci
        double velocity_Y[] = new double [9];    //predkosci w kirunku x i y

        double temp_vel_X = 0;            //zmienne potrzebne do obliczenia
        double temp_vel_y = 0;            //wartosci predkosci


        // uzyskiwanie predkosci w kilku przedzialach metoda calkowania numerycznego
        for(int i = 0; i < 9; i++ ){
            if(i == 0){
                acceleration_X [i] = x_acceleration;
                acceleration_y [i] = y_acceleartion;
            }
            else {
                acceleration_X[i] = MainActivity.sensorXAcc; // Oś Y jest pionowa, na razie zbędna
                acceleration_y[i] = MainActivity.sensorZAcc;
            }
            temp_vel_X = initialVelocityX;
            temp_vel_y = initialVelocityY;

            for (int j = 0; j <= i; j++){
                if(j == 0 || j == i) {
                    temp_vel_X += acceleration_X[j] / 2;
                    temp_vel_y += acceleration_y[j] / 2;
                }
                else{
                    temp_vel_X += acceleration_X [j];
                    temp_vel_y += acceleration_y [j];
                }
            }
            velocity_X[i] = temp_vel_X * TheHeightOfTheTrapezoid;
            velocity_Y[i] = temp_vel_y * TheHeightOfTheTrapezoid;

            //TimeUnit.SECONDS.sleep(timeSleep);
            Thread.sleep(timeSleep);
        }

        initialVelocityX = velocity_X[8];
        initialVelocityY = velocity_Y[8];

        //uzyskiewanie przebytej drogi metoda calkoawnia numerycznego

        for(int i = 0; i < 9; i++){
            if(i == 0 || i == 8){
                delta_x += velocity_X [i] / 2;
                delta_y += velocity_Y [i] / 2;
            }
            else {
                delta_x += velocity_X [i];
                delta_y += velocity_Y [i];
            }
        }

        delta_x = delta_x * TheHeightOfTheTrapezoid;
        delta_y = delta_y * TheHeightOfTheTrapezoid;

      //  delta_x = (x_acceleration / 2) * Math.pow(time,2) / 1000;
    //    delta_y = (y_acceleartion / 2) * Math.pow(time,2) / 1000;
        int EarthRadius = 6371000; //metrow

        double deltaFi = 0;

        //liczenie szerokosci geograficznej
        // z twierdzenia cosinusow
        deltaFi = ((Math.pow(delta_y,2) - 2 * Math.pow(EarthRadius,2))/(-2 * Math.pow(EarthRadius,2)))/(57.29578*1000);
        if(x_acceleration>0.8){
            MainActivity.coordinates.latitude += deltaFi;
        }else{

        }

        //liczenie dlugosci geograficznej

        double x_1 = 0;
        double x_2 = 0;
        double y = 0;
        double part = 0;

        // z twierdzenie cosinusow

        x_1 = Math.sin(Math.toRadians(MainActivity.coordinates.longitude))*EarthRadius;

        y = Math.sqrt(Math.pow(EarthRadius,2) - Math.pow(x_1,2));
        x_2 = x_1 - delta_x;
        part = Math.sqrt(Math.pow(y,2) + Math.pow(x_2,2));


        // z tw cosinusow
        if(y_acceleartion>0.8){
            MainActivity.coordinates.longitude += Math.asin((Math.pow(x_2,2)-Math.pow(y,2)-Math.pow(part,2))/(2*y*part))/(57.29578*1000);
        }else{

        }
        return MainActivity.coordinates;
    }
}