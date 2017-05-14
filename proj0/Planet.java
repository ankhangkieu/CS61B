public class Planet {
    public double xxPos;
    public double yyPos;
    public double xxVel;
    public double yyVel;
    public double mass;
    public String imgFileName;

    private final double G = 6.67e-11;
    private boolean isSamePlanet(Planet p){
        if (calcDistance(p) == 0)
            return true;
        return false;
    }
    
    public Planet(double xP, double yP, double xV, double yV, double m, String img){
        xxPos = xP;
        yyPos = yP;
        xxVel = xV;
        yyVel = yV;
        mass = m;
        imgFileName = img;
    }
    
    public Planet(Planet p){
        xxPos = p.xxPos;
        yyPos = p.yyPos;
        xxVel = p.xxVel;
        yyVel = p.yyVel;
        mass = p.mass;
        imgFileName = p.imgFileName;
    }

    public double calcDistance(Planet p){
        return Math.pow(Math.pow(xxPos - p.xxPos, 2) + Math.pow(yyPos - p.yyPos, 2), 0.5);
    }

    public double calcForceExertedBy(Planet p){
        return G * mass * p.mass / Math.pow(calcDistance(p), 2);
    }

    public double calcForceExertedByX(Planet p){
        return calcForceExertedBy(p) * (p.xxPos - xxPos) / calcDistance(p);
    }

    public double calcForceExertedByY(Planet p){
        return calcForceExertedBy(p) * (p.yyPos - yyPos) / calcDistance(p);
    }

    public double calcNetForceExertedByX(Planet[] allPlanets){
        double netForce = 0;
        for (int i = 0; i < allPlanets.length; i++){
            if (!isSamePlanet(allPlanets[i]))
                netForce += calcForceExertedByX(allPlanets[i]);
        }
        return netForce;
    }

    public double calcNetForceExertedByY(Planet[] allPlanets){
        double netForce = 0;
        for (int i = 0; i < allPlanets.length; i++){
            if (!isSamePlanet(allPlanets[i]))
                netForce += calcForceExertedByY(allPlanets[i]);
        }
        return netForce;
    }


    public void update(double dt, double fX, double fY){
        xxVel = xxVel + fX / mass * dt;
        yyVel = yyVel + fY / mass * dt;
        xxPos = xxPos + xxVel * dt;
        yyPos = yyPos + yyVel * dt;
    }

    public void draw(){
        StdDraw.picture(xxPos, yyPos, "images/" + imgFileName);
    }
}
