import java.util.ArrayList;

class Node {
    Vector position;
    Vector oldposition;
    Vector velocity;
    Vector force;
    Vector oldforce;
    String label;
    ArrayList<Vector> arrowList= new ArrayList<Vector>();

    Node(int x, int y, String label) {
        position = new Vector(x, y);
        oldposition = new Vector(x,y);
        velocity = new Vector(0, 0);
        force = new Vector(0, 0);
        oldforce = new Vector(0,0);
        this.label = label;
    }

}