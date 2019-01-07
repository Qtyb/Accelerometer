
//nie zostały też uwzględnione spłaszczenia na biegunach



//ta klasa obl iczy tylko przemieszenie chwilowe
public class NewLocation {

    Coordinates coordinates = new Coordinates(0,0);  // do podmiany, potrzebna
                                                                       // jakas funkcja do
                                                                       // uzyskiwania wspolrzednych

    public Coordinates getNewLocation(float x_acceleration, float y_acceleartion, float z_acceleartion){

        //całkowanie metodą trapezów (byloby, ale w tej wersji nie tutaj :D)

        double delta_x=0;
        double delta_y=0;

        float time = 100; // milisekund trzeba uzyskac ten czas zmian

        delta_x = (x_acceleration / 2) * Math.pow(time,2) / 1000; //ruch jednostajnie zmienny
        delta_y = (y_acceleartion / 2) * Math.pow(time,2) / 1000;

        int EarthRadius = 6371000; //metrow

        double deltaFi = 0;

        //liczenie szerokosci geograficznej
        // z twierdzenia cosinusow
        deltaFi = (Math.pow(delta_y,2) - 2 * Math.pow(EarthRadius,2))/(-2 * Math.pow(EarthRadius,2));

        coordinates.latitude += deltaFi;


        //liczenie dlugosci geograficznej

        double x_1 = 0;
        double x_2 = 0;
        double y = 0;
        double part = 0;

        // z twierdzenie cosinusow

        x_1 = Math.sin(Math.toRadians(coordinates.longitude))*EarthRadius;

        y = Math.sqrt(Math.pow(EarthRadius,2) + Math.pow(x_1,2));
        x_2 = x_1 - delta_x;
        part = Math.sqrt(Math.pow(y,2) + Math.pow(x_2,2));


        // z tw cosinusow

        coordinates.longitude = Math.asin((Math.pow(x_2,2)-Math.pow(y,2)-Math.pow(part,2))/(2*y*part));

        return coordinates;
        }
}



