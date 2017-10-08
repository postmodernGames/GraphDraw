/**
 * Created by BrokenGardener on 10/6/2017.
 */
public class Vector {
    double x;
    double y;

    public Vector(){
        this.x =0;
        this.y =0;
    }

    public Vector(double x, double y){
        this.x = x;
        this.y = y;


    }
    public void reset(){
        this.x =0;
        this.y=0;
    }

    public void add(Vector v){
        x+=v.x;
        y+=v.y;
    }
    public double dot(Vector v){
     return x*v.x+y*v.y;
    }
    static double normDiff(Vector u, Vector v){
        return Math.sqrt(Math.pow(u.x-v.x,2) +Math.pow(u.y-v.y,2));
    }
}
