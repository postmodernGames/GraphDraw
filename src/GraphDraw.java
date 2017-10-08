import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by BrokenGardener on 10/6/2017.
 */

public class GraphDraw extends JFrame {
    ArrayList<Node> nodeList = new ArrayList<Node>();
    ArrayList<Edge> edgeList = new ArrayList<Edge>();

    double kspring =.1;
    double kelec = 1;
    double L = 100.0;
    double TIMESTEP = 1;
    int nodeWidth = 24;
    int nodeHeight = nodeWidth;
    private BufferStrategy bs;
    Random rand = new Random();
    int frameWidth;
    int frameHeight;
    Canvas canvas;

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

    class Edge {
        int i;
        int j;

        Edge(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }


    public int randomWidth(){
        return rand.nextInt(frameWidth) + 1;
    }

    public int randomHeight(){
        return rand.nextInt(frameHeight) + 1;
    }

    GraphDraw() {

        frameHeight = 500;
        frameWidth = 500;

        for(int i=0;i<5;i++) {
            nodeList.add(new Node(randomWidth()/2, randomHeight()/2, "" +i));
        }
     //   edgeList.add(new Edge(0, 1));
        edgeList.add(new Edge(0, 2));
     //   edgeList.add(new Edge(0, 3));
       edgeList.add(new Edge(0, 4));
        edgeList.add(new Edge(1, 2));
        edgeList.add(new Edge(1, 3));
        edgeList.add(new Edge(1, 4));
      //  edgeList.add(new Edge(2, 3));
        edgeList.add(new Edge(2, 4));
        edgeList.add(new Edge(3, 4));
        edgeList.add(new Edge(2, 4));
      //  edgeList.add(new Edge(3, 4));


        canvas = new Canvas();
        canvas.setSize(500, 500);
        canvas.setBackground(Color.pink);
        canvas.setIgnoreRepaint(true);
        GamePanel jpnl = new GamePanel();
        jpnl.add(canvas);
        getContentPane().add(jpnl);

        setIgnoreRepaint(true);
        setVisible(true);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLayout(new FlowLayout());

        canvas.createBufferStrategy(2);

        bs = canvas.getBufferStrategy();
        canvas.requestFocus();

    }


    public void update() {
        canvas.setSize(frameWidth,frameHeight);
        double Numerator =0;
        double Denominator=0;

        for (int i = 0; i < nodeList.size(); i++) {
            nodeList.get(i).force.reset();
            nodeList.get(i).arrowList.clear();
        }

        for (int i = 0; i < nodeList.size()-1; i++) {
            for (int j = i+1; j < nodeList.size(); j++) {
                double sx=1, sy=1;

                if(isConnected(i,j,edgeList)){
                    double n = Vector.normDiff(nodeList.get(i).position,nodeList.get(j).position);
                    double w = -(1.0-L/n);
                    double deltax = nodeList.get(i).position.x-nodeList.get(j).position.x;
                    double deltay = nodeList.get(i).position.y-nodeList.get(j).position.y;

                    nodeList.get(i).arrowList.add(new Vector(w*deltax,w*deltay));
                    nodeList.get(j).arrowList.add(new Vector(-w*deltax,-w*deltay));
                    nodeList.get(i).force.x += w*deltax;
                    nodeList.get(i).force.y += w*deltay;
                    nodeList.get(j).force.x -= w*deltax;
                    nodeList.get(j).force.y -= w*deltay;
                }
                else {
                    double n = Vector.normDiff(nodeList.get(i).position,nodeList.get(j).position);

                    double w = 200/ Math.pow(n,1.5);
                    double deltax = nodeList.get(i).position.x - nodeList.get(j).position.x;
                    double deltay = nodeList.get(i).position.y - nodeList.get(j).position.y;

                    nodeList.get(i).arrowList.add(new Vector(w*deltax,w*deltay));
                    nodeList.get(j).arrowList.add(new Vector(-w*deltax,-w*deltay));

                    if (Math.abs(deltax) > 0.05) {
                        nodeList.get(i).force.x +=  w * deltax;
                        nodeList.get(j).force.x -=  w * deltax;
                    }
                    if (Math.abs(deltay) > 0.05) {
                        nodeList.get(i).force.y +=  w * deltay;
                        nodeList.get(j).force.y -=  w * deltay;
                    }

                }
            }
        }

     /*   for (int i = 0; i < nodeList.size(); i++){
            Numerator +=  nodeList.get(i).force.dot(nodeList.get(i).position-nodeList.get(i).oldposition);
            Denominator +=  nodeList.get(i).force.dot( nodeList.get(i).force);
        }

        double gamma = Math.abs(Numerator/Denominator);
        gamma /= 10;*/
        double gamma= 0.01;


        for (int i = 0; i < nodeList.size(); i++) {
            nodeList.get(i).position.x += gamma* nodeList.get(i).force.x;
            nodeList.get(i).position.y += gamma* nodeList.get(i).force.y;
        }


    }

    public Vector springForce(Vector u, Vector v) {
        double mag = kspring * Math.abs(Vector.normDiff(u, v) - L);
        return new Vector(-(int) Math.floor(mag * (u.x - v.x)/Math.abs(Vector.normDiff(u, v))), -(int) Math.floor(mag * (u.y - v.y))/Math.abs(Vector.normDiff(u, v)));
    }

    public Vector elecForce(Vector u, Vector v) {
        double mag = kelec / Math.pow(Vector.normDiff(u, v),3);
        return new Vector( mag *( u.x - v.x),  mag *(u.y - v.y));
    }

    public boolean isConnected(int i, int j, ArrayList<Edge> edgeList) {
        for (Edge edge : edgeList) {
            if (((edge.i == i) && (edge.j == j)) || ((edge.i == j) && (edge.j == i))) return true;
        }
        return false;
    }

    public void center() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        int x = (screenSize.width - frameSize.width) / 2;
        int y = (screenSize.height - frameSize.height) / 2;
        setLocation(x, y);
    }


    private void renderFrame() {
        do {
            do {
                Graphics g = null;
                try {
                    g = bs.getDrawGraphics();
                    g.setColor(Color.magenta);
                    g.fillOval(100, 100, 100, 100);
                    g.clearRect(0, 0, getWidth(), getHeight());
                    render(g);
                } finally {
                    if (g != null) {
                        g.dispose();
                    }
                }
            } while (bs.contentsRestored());
            bs.show();
        } while (bs.contentsLost());
    }

    class GamePanel extends JPanel {


        public void paint(Graphics g) {
            super.paint(g);
            render(g);
        }

    }
    private void render(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        g2.translate(frameWidth/2,frameHeight/2);
        double accumx=0;
        double accumy=0;
        for (int i = 0; i < nodeList.size(); i++) {
            accumx+= nodeList.get(i).position.x;
            accumy+= nodeList.get(i).position.y;
        }
        accumx/=nodeList.size();
        accumy/=nodeList.size();

        for (int i = 0; i < nodeList.size(); i++) {
            nodeList.get(i).position.x -= accumx;
            nodeList.get(i).position.y -= accumy;
        }


        for(int i=0;i<nodeList.size();i++){
            Node node = nodeList.get(i);
            Ellipse2D ellipse = new Ellipse2D.Double(node.position.x,node.position.y,nodeWidth,nodeHeight);
            g2.setPaint(Color.GREEN);
            g2.fill(ellipse);
            g2.setPaint(Color.RED);
            g2.drawString(node.label,(int)node.position.x,(int)node.position.y);
            g2.setPaint(Color.GREEN);
        }
        GeneralPath path = new GeneralPath();
        for(Edge edge: edgeList){
            path.moveTo(nodeList.get(edge.i).position.x+nodeWidth/2,nodeList.get(edge.i).position.y+nodeHeight/2);
            path.lineTo(nodeList.get(edge.j).position.x+nodeWidth/2,nodeList.get(edge.j).position.y+nodeHeight/2);
        }
        g2.draw(path);

        GeneralPath arrowPath = new GeneralPath();
        for(int i=0;i<nodeList.size();i++){
            for(Vector arrow : nodeList.get(i).arrowList) {
                arrowPath.moveTo(nodeList.get(i).position.x, nodeList.get(i).position.y);
                arrowPath.lineTo(nodeList.get(i).position.x + arrow.x, nodeList.get(i).position.y +arrow.y / 10);
            }
        }
        g2.setColor(Color.BLACK);

        g2.draw(arrowPath);

    }

    public static void main(String[] args){
        GraphDraw graphDraw = new GraphDraw();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final int MS_PER_UPDATE = 170;
                double previous = System.currentTimeMillis();
                double lag = 0.0;
                while(true) {
                    double current = System.currentTimeMillis();
                    double elapsed = current - previous;
                    previous = current;
                    lag += elapsed;
                    graphDraw.renderFrame();
                 //   while(lag >= MS_PER_UPDATE){
                        graphDraw.update();
                        lag -= MS_PER_UPDATE;
                //    }


                }
            }
        });
    }
}
