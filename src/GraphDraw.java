import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

/**
 * Created by BrokenGardener on 10/6/2017.
 */
public class GraphDraw extends JFrame {
    ArrayList<Node> nodeList = new ArrayList<Node>();
    ArrayList<Edge> edgeList = new ArrayList<Edge>();

    double kspring =.1;
    double kelec = 1;
    int L = 100;
    double TIMESTEP = 0.1;
    int nodeWidth = 24;
    int nodeHeight = nodeWidth;
    private BufferStrategy bs;

    class Node {
        Vector position;
        Vector velocity;
        Vector force;

        Node(int x, int y) {
            position = new Vector(x, y);
            velocity = new Vector(0, 0);
            force = new Vector(0, 0);
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

    GraphDraw() {
        nodeList.add(new Node(50, 100));
        nodeList.add(new Node(150, 100));
        nodeList.add(new Node(250, 200));
        nodeList.add(new Node(50, 200));
        nodeList.add(new Node(150, 300));

        edgeList.add(new Edge(0, 1));
        edgeList.add(new Edge(0, 2));
        edgeList.add(new Edge(0, 4));
        edgeList.add(new Edge(1, 3));
        edgeList.add(new Edge(2, 3));
        edgeList.add(new Edge(2, 4));
        edgeList.add(new Edge(3, 4));


        Canvas canvas = new Canvas();
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
        for (int i = 0; i < nodeList.size(); i++) {
            for (int j = 0; j < nodeList.size(); j++) {
                if(i!=j){
                    if (isConnected(i, j, edgeList)) {
                        nodeList.get(i).force.add(springForce(nodeList.get(i).position, nodeList.get(j).position));
                    }
                    else nodeList.get(i).force.add(elecForce(nodeList.get(i).position, nodeList.get(j).position));


                }
            }
        }
        int accumx =0;
        int accumy = 0;
        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            node.velocity.x += node.force.x * TIMESTEP;
            node.velocity.y += node.force.y * TIMESTEP;
            node.position.x += node.velocity.x * TIMESTEP;
            node.position.y += node.velocity.y * TIMESTEP;
            node.force.reset();
            accumx+=node.position.x;
            accumy+=node.position.y;
        }
        accumx/=nodeList.size();
        accumy/=nodeList.size();

        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            node.position.x-=accumx;
            node.position.x+=250;
            node.position.y-=accumy;
            node.position.y+=250;
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
        for(int i=0;i<nodeList.size();i++){
            Node node = nodeList.get(i);
            Ellipse2D ellipse = new Ellipse2D.Double(node.position.x,node.position.y,nodeWidth,nodeHeight);
            g2.setPaint(Color.GREEN);
            g2.fill(ellipse);
        }
        GeneralPath path = new GeneralPath();
        for(Edge edge: edgeList){
            path.moveTo(nodeList.get(edge.i).position.x+nodeWidth/2,nodeList.get(edge.i).position.y+nodeHeight/2);
            path.lineTo(nodeList.get(edge.j).position.x+nodeWidth/2,nodeList.get(edge.j).position.y+nodeHeight/2);
        }
        g2.draw(path);

    }

    public static void main(String[] args){
        GraphDraw graphDraw = new GraphDraw();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final int MS_PER_UPDATE = 17;
                double previous = System.currentTimeMillis();
                double lag = 0.0;
                while(true) {
                    double current = System.currentTimeMillis();
                    double elapsed = current - previous;
                    previous = current;
                    lag += elapsed;

                    while(lag >= MS_PER_UPDATE){
                        graphDraw.update();
                        lag -= MS_PER_UPDATE;
                    }
                    graphDraw.renderFrame();

                }
            }
        });
    }
}
