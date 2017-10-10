package GraphLayout;

import GraphLayout.Vector;

import java.util.ArrayList;

public class Node {
    public Vector position;
    Vector oldposition;
    Vector velocity;
    Vector force;
    Vector oldforce;
    public String label;
   public  ArrayList<Vector> arrowList= new ArrayList<Vector>();

    public Node(int x, int y, String label) {
        position = new Vector(x, y);
        oldposition = new Vector(x,y);
        velocity = new Vector(0, 0);
        force = new Vector(0, 0);
        oldforce = new Vector(0,0);
        this.label = label;
    }

}