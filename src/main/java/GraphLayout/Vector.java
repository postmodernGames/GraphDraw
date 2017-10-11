package GraphLayout;

/**
 * Created by BrokenGardener on 10/6/2017.
 */
public class Vector {
    public double x;
    public double y;

    public Vector(){
        this.x =0;
        this.y =0;
    }

    public Vector(double x, double y){
        this.x = x;
        this.y = y;
    }
    public Vector(Vector v){
        this.x= v.x;
        this.y=v.y;
    }
    public void reset(){
        this.x =0;
        this.y=0;
    }

    public Vector add(Vector v){
        x+=v.x;
        y+=v.y;
        return this;
    }
    public Vector minus(Vector v){
        x-=v.x;
        y-=v.y;
        return this;
    }
    public Vector scale(double c){
        x*=c;
        y*=c;
        return this;
    }
    public Vector clone(Vector v){
        this.x= v.x;
        this.y=v.y;
        return this;
    }
    public double dot(Vector v){
     return x*v.x+y*v.y;
    }

    public double norm(){
        return Math.sqrt(x*x+y*y);
    }
    public void normalize(){
        this.scale(1/this.norm());
    }
    static double normDiff(Vector u, Vector v){
        return Math.sqrt(Math.pow(u.x-v.x,2) +Math.pow(u.y-v.y,2));
    }
}
