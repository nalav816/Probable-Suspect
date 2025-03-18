package gameMath;

public class Vector {
    private double x, y;

    public Vector(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public void setX(double x){
        this.x = x;
    }

    public void setY(double y){
        this.y = y;
    }
    
    public Vector scale(double num){
        return new Vector((double)(x * num) , (double)(y * num));
    }

    public Vector subtract(Vector other){
        return new Vector(x - other.getX(), y - other.getY());
    }

    public Vector add(Vector other){
        return new Vector(x + other.getX(), y + other.getY());
    }

    public double magnitude(){
        return Math.pow(Math.pow(getX(), 2) + Math.pow(getY(), 2), .5);
    }

    public double distance(Vector other){
        Vector difference = subtract(other);
        return difference.magnitude();
    }

    public static Vector copy(Vector v){
        return new Vector(v.getX(), v.getY());
    }

    //To make up for double margin of error
    public boolean doubleEquals(double a, double b){
        return Math.abs(a - b) < .0001;
    }
    public boolean equals(Vector other){
        return doubleEquals(x, other.getX()) && doubleEquals(y, other.getY());
    }
    
}
