public class NBody {
    public static double readRadius(String fileName){
        In in = new In(fileName);
        in.readLine();
        return in.readDouble();
    }
    
    public static Planet[] readPlanets(String fileName){
        double xP, yP, xV, yV, m;
        String img;
        In in = new In(fileName);
        int size = in.readInt();
        Planet[] result = new Planet[size];
        in.readLine();
        in.readLine();
        for(int i = 0; i< size; i++){
            xP = in.readDouble();
            yP = in.readDouble();
            xV = in.readDouble();
            yV = in.readDouble();
            m = in.readDouble();
            img = in.readString();
            in.readLine();
            result[i] = new Planet(xP, yP, xV, yV, m, img);
        }
        return result;
    }

    public static void main(String[] args){
        if (args.length < 3){
            return;
        }
        double T, dt, radius, time = 0;
        String filename;
        Planet[] planets;
        T = Double.parseDouble(args[0]);
        dt = Double.parseDouble(args[1]);
        filename = args[2];
        radius = readRadius(filename);
        planets = readPlanets(filename);
        StdDraw.setScale(-radius, radius);
        drawUniverse(planets);
        while (time < T){
            double[] xForces = new double[planets.length];
            double[] yForces = new double[planets.length];
            for (int i = 0; i < planets.length; i++) {
                xForces[i] = planets[i].calcNetForceExertedByX(planets);
                yForces[i] = planets[i].calcNetForceExertedByY(planets);
            } 
            for (int i = 0; i < planets.length; i++) {
                planets[i].update(dt, xForces[i], yForces[i]);
            } 
            StdDraw.show(10);
            drawUniverse(planets);
            time += dt;
        }
        printUniverse(planets, radius);
    }

    private static void drawUniverse(Planet[] planets){
        StdDraw.picture(0 , 0, "images/starfield.jpg");
        for (int i = 0; i < planets.length; i++){
            planets[i].draw();
        }
    }

    private static void printUniverse(Planet[] planets, double radius){
        StdOut.printf("%d\n", planets.length);
        StdOut.printf("%.2e\n", radius);
        for (int i = 0; i < planets.length; i++) {
            StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n", planets[i].xxPos, planets[i].yyPos, planets[i].xxVel, planets[i].yyVel, planets[i].mass, planets[i].imgFileName);	
        }
    }
}
