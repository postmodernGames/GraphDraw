package GraphDraw;

import FSM.FSMModel;
import FSM.FSMNode;
import FSM.FSMEdge;
import GraphLayout.Edge;
import GraphLayout.GraphLayoutManager;
import GraphLayout.Node;
import GraphLayout.Vector;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by BrokenGardener on 10/6/2017.
 */

public class GraphDraw extends JFrame {
    ArrayList<Node> nodeList = new ArrayList<Node>();
    ArrayList<Edge> edgeList = new ArrayList<Edge>();

    JFrame jfrm = new JFrame();



    double TIMESTEP =0.01;
    int nodeWidth = 24;
    int nodeHeight = nodeWidth;
    private BufferStrategy bs;
    Random rand = new Random();
    int frameWidth;
    int frameHeight;
    Canvas canvas;
    GamePanel jpnl;



    @Override
    public void paint(Graphics g){
        super.paintComponents(g);

        renderFrame();
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
        FSMModel fsmModel = new FSMModel();


        ArrayList<FSMEdge> fsmEdgeList = new ArrayList<FSMEdge>(Arrays.asList(new FSMEdge(2,'a'),new FSMEdge(4,'b'))  );
        fsmModel.fsmNodeList.add(new FSMNode(fsmEdgeList));

        fsmEdgeList = new ArrayList<FSMEdge>(Arrays.asList(new FSMEdge(1,'a'),new FSMEdge(4,'b'))  );
        fsmModel.fsmNodeList.add(new FSMNode(fsmEdgeList));

        fsmEdgeList = new ArrayList<FSMEdge>(Arrays.asList(new FSMEdge(3,'a'),new FSMEdge(2,'b'))  );
        fsmModel.fsmNodeList.add(new FSMNode(fsmEdgeList));

        fsmEdgeList = new ArrayList<FSMEdge>(Arrays.asList(new FSMEdge(4,'a'),new FSMEdge(1,'f'))  );
        fsmModel.fsmNodeList.add(new FSMNode(fsmEdgeList));

        fsmEdgeList = new ArrayList<FSMEdge>(Arrays.asList(new FSMEdge(0,'g'),new FSMEdge(4,'b'))  );
        fsmModel.fsmNodeList.add(new FSMNode(fsmEdgeList));






        for(int i=0;i<fsmModel.fsmNodeList.size();i++) {
            nodeList.add(new Node(randomWidth()/2, randomHeight()/2, "" +i));
            for(int j=0;j<fsmModel.fsmNodeList.get(i).outList.size();j++){
                edgeList.add(new Edge(i,fsmModel.fsmNodeList.get(i).outList.get(j).toIndex,fsmModel.fsmNodeList.get(i).outList.get(j).match));
            }
        }

        GraphLayoutManager graphLayoutManager = new GraphLayoutManager(nodeList,edgeList);



        canvas = new Canvas();
        canvas.setSize(500, 500);
        canvas.setBackground(Color.pink);

         jpnl = new GamePanel();
        jpnl.add(canvas);
        getContentPane().add(jpnl);


        setVisible(true);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLayout(new FlowLayout());

        canvas.createBufferStrategy(2);

        bs = canvas.getBufferStrategy();
        canvas.requestFocus();

    }



    private void renderFrame() {
        do {
            do {
                Graphics g = null;
                try {
                    g = bs.getDrawGraphics();
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
            frameWidth = this.getWidth();
            frameHeight = this.getHeight();
            jpnl.setSize(new Dimension(frameWidth,frameWidth));
            canvas.setSize(frameWidth,frameHeight);
            renderFrame();
        }

    }

    private void render(Graphics g){

        Graphics2D g2 = (Graphics2D)g;
        frameWidth = this.getWidth();
        frameHeight = this.getHeight();
        jpnl.setSize(frameWidth,frameHeight);
        canvas.setSize(frameWidth,frameHeight);
        canvas.setBackground(Color.pink);
        jpnl.repaint();
        this.repaint();
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
        g2.setPaint(Color.RED);
        for(Edge edge: edgeList){
            int fromx = (int)nodeList.get(edge.i).position.x+nodeWidth/2;
            int fromy = (int) nodeList.get(edge.i).position.y+nodeHeight/2;
            int tox =  (int)nodeList.get(edge.j).position.x+nodeWidth/2;
            int toy =  (int)nodeList.get(edge.j).position.y+nodeHeight/2;

            path.moveTo(fromx,fromy);
            path.lineTo(tox,toy);
            g2.drawString(edge.label+"",(tox+fromx)/2,(fromy+toy)/2);
        }
        g2.setPaint(Color.GREEN);
        g2.draw(path);

        GeneralPath arrowPath = new GeneralPath();
        for(int i=0;i<nodeList.size();i++){
            for(Vector arrow : nodeList.get(i).arrowList) {
                arrowPath.moveTo(nodeList.get(i).position.x, nodeList.get(i).position.y);
                arrowPath.lineTo(nodeList.get(i).position.x + arrow.x, nodeList.get(i).position.y +arrow.y / 10);
            }
        }
        g2.setColor(Color.BLUE);

        g2.draw(arrowPath);

    }

    public static void main(String[] args){
        final GraphDraw graphDraw = new GraphDraw();
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
           //             graphDraw.update();
                        lag -= MS_PER_UPDATE;
                //    }
                    try{
                        Thread.sleep(100);
                    }catch(Exception e){}

                }
            }
        });
    }
}
