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
import java.awt.geom.Path2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class GraphDraw extends JFrame {
    ArrayList<Node> nodeList = new ArrayList<Node>();
    ArrayList<Edge> edgeList = new ArrayList<Edge>();
    ArrayList<Edge> selfLoopList = new ArrayList<Edge>();

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
        Graphics2D g2 = (Graphics2D)g;
        RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        renderingHints.add(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2.setRenderingHints(renderingHints);

        renderFrame();
    }



    public GeneralPath getArrow(double fromX, double fromY, double toX, double toY){
        GeneralPath path = new GeneralPath(Path2D.WIND_EVEN_ODD);
        path.moveTo(fromX,fromY);
        path.lineTo(toX, toY);
        double lambda=0.9;
        double slx= (1-lambda)*fromX + lambda*toX;
        double sly= (1-lambda)*fromY + lambda*toY;
        Vector perp = new Vector(-(toY-fromY),(toX-fromX));
        perp.normalize();
        double w = 5;
        double v1x= slx + w*perp.x;
        double v1y= sly + w*perp.y;
        double v2x= slx - w*perp.x;
        double v2y= sly - w*perp.y;
        path.lineTo(v1x,v1y);
        path.moveTo(toX,toY);
        path.lineTo(v2x,v2y);
        return path;
    }
    public GeneralPath getDoubleArrow(double fromX, double fromY, double toX, double toY){
        GeneralPath path = new GeneralPath(Path2D.WIND_EVEN_ODD);
        double lambda=0.9;
        double slx= (1-lambda)*fromX + lambda*toX;
        double sly= (1-lambda)*fromY + lambda*toY;
        lambda=0.1;
        double tlx= (1-lambda)*fromX + lambda*toX;
        double tly= (1-lambda)*fromY + lambda*toY;
        Vector perp = new Vector(-(toY-fromY),(toX-fromX));
        perp.normalize();
        double w = 5;
        double v1x= slx + w*perp.x;
        double v1y= sly + w*perp.y;
        double ux= tlx - w*perp.x;
        double uy= tly - w*perp.y;

        path.moveTo(fromX,fromY);
        path.lineTo(ux, uy);
        path.moveTo(fromX,fromY);
        path.lineTo(toX, toY);

        path.lineTo(v1x,v1y);
        path.moveTo(toX,toY);

        return path;
    }

    public GeneralPath getSelfLoop(Vector position,ArrayList<Vector> outNodePositionList){
        GeneralPath path = new GeneralPath(Path2D.WIND_EVEN_ODD);
        ArrayList<Double> angleList = new ArrayList<>();
        for(Vector v: outNodePositionList){
            double deltax = v.x-position.x;
            double deltay = v.y-position.y;
            double theta = Math.atan2(deltay,deltax);
            angleList.add(theta);
        }
        Collections.sort(angleList);
        double lag =0;
        int lowerIndexOfMaxSpace=0;
        for(int i=1;i<angleList.size();i++){
            if(lag < angleList.get(i)-angleList.get(i-1)){
                lag=angleList.get(i)-angleList.get(i-1);
                lowerIndexOfMaxSpace = i;
            }
        }
    /*    int i = angleList.size()-1;
        if(lag < 2*Math.PI+ angleList.get(0)-angleList.get(i)){
            lowerIndexOfMaxSpace = i;
        }*/

        try{
            lag=angleList.get(lowerIndexOfMaxSpace+1)-angleList.get(lowerIndexOfMaxSpace);
        }catch(IndexOutOfBoundsException e){
            lag=2*3.141;
        }
        lag/=3;
        double radius = nodeHeight/2;
        double startAngle = angleList.get(lowerIndexOfMaxSpace)+lag;
        double endAngle = angleList.get(lowerIndexOfMaxSpace)+lag*2;
        double startX = position.x+radius*Math.cos(startAngle);
        double startY = position.y+radius*Math.sin(startAngle);
        double endX = position.x+radius*Math.cos(endAngle);
        double endY = position.y+radius*Math.sin(endAngle);
        path.moveTo(startX+radius,startY+radius);
        double midX = (startX+endX)/2;
        double midY = (startY+endY)/2;
        double controlX = midX + (midY-startY)/Math.sqrt(Math.pow(midX-startX,2)+Math.pow(midY-startY,2))*5*radius;
        double controlY = midY - (midX-startX)/Math.sqrt(Math.pow(midX-startX,2)+Math.pow(midY-startY,2))*5*radius;
        path.quadTo(controlX+radius,controlY+radius,endX+radius,endY+radius);
        return path;
    }

    public int containsEdge(ArrayList<FSMEdge> outList,int i){
        for(int j=0;j<outList.size();j++)
            if(outList.get(j).toIndex==i) return j;
        return -1;
    }

    public GraphDraw(FSMModel fsmModel) {

        frameHeight = 500;
        frameWidth = 500;


        for(int i=0;i<fsmModel.fsmNodeList.size();i++) {
            nodeList.add(new Node((rand.nextInt(frameWidth) + 1)/2, (rand.nextInt(frameHeight) + 1)/2, "" +i));
            for(int j=0;j<fsmModel.fsmNodeList.get(i).outList.size();j++){
                int toIndex = fsmModel.fsmNodeList.get(i).outList.get(j).toIndex;
                if(i!=toIndex) {
                    int outListIndex = containsEdge( fsmModel.fsmNodeList.get(toIndex).outList,i);
                    if(outListIndex!=-1  ) {
                        if (i < toIndex) {
                            edgeList.add(new Edge(i, toIndex, fsmModel.fsmNodeList.get(i).outList.get(j).match, fsmModel.fsmNodeList.get(j).outList.get(outListIndex).match));
                        }
                    }
                    else  //there is a simple connection
                        edgeList.add(new Edge(i, fsmModel.fsmNodeList.get(i).outList.get(j).toIndex, fsmModel.fsmNodeList.get(i).outList.get(j).match));
                }

                else
                    selfLoopList.add(new Edge(i,i,fsmModel.fsmNodeList.get(i).outList.get(j).match));
            }
        }

        GraphLayoutManager graphLayoutManager = new GraphLayoutManager(nodeList,edgeList);

        canvas = new Canvas();
        canvas.setSize(500, 500);
        canvas. setBackground(new Color((float)255/255,(float)227/255,(float)175/255));

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


    public void renderFrame() {
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

        int radius = nodeHeight/2;
        for(int i=0;i<nodeList.size();i++){
            Node node = nodeList.get(i);
            Ellipse2D ellipse = new Ellipse2D.Double(node.position.x,node.position.y,nodeWidth,nodeHeight);
            g2.setColor(new Color((float)193/255,(float)193/255,(float)187/255));
            g2.fill(ellipse);
            g2.setColor(new Color((float)157/255,(float)158/255,(float)154/255));
            g2.draw(ellipse);
            g2.setPaint(Color.RED);
            g2.drawString(node.label,(int)(node.position.x+radius),(int)(node.position.y+radius));
        }


        //Edges
        GeneralPath path = new GeneralPath();
        g2.setStroke(new BasicStroke(2.0f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g2.setPaint(Color.BLACK);
        for(Edge edge: edgeList){

            int fromx = (int)nodeList.get(edge.i).position.x ;
            int fromy = (int) nodeList.get(edge.i).position.y ;
            int tox =  (int)nodeList.get(edge.j).position.x ;
            int toy =  (int)nodeList.get(edge.j).position.y ;

            fromx = (int) (fromx + Math.floor((radius)*(tox-fromx)/Math.sqrt((tox-fromx)*(tox-fromx)+(toy-fromy)*(toy-fromy))));
            fromy = (int) (fromy + Math.floor((radius)*(toy-fromy)/Math.sqrt((tox-fromx)*(tox-fromx)+(toy-fromy)*(toy-fromy))));
            tox = (int) (tox - Math.floor((radius)*(tox-fromx)/Math.sqrt((tox-fromx)*(tox-fromx)+(toy-fromy)*(toy-fromy))));
            toy = (int) (toy - Math.floor((radius)*(toy-fromy)/Math.sqrt((tox-fromx)*(tox-fromx)+(toy-fromy)*(toy-fromy))));
            if(!edge.doubleFlag){
                path = getArrow(fromx+radius,fromy+radius,tox+radius,toy+radius);

                g2.drawString(edge.label+"",(tox+fromx)/2+radius,(fromy+toy)/2+radius);
            }
            else{
                path = getDoubleArrow(fromx+radius,fromy+radius,tox+radius,toy+radius);
                g2.drawString(edge.label+"",(tox+fromx)/2+radius,(fromy+toy)/2+radius);
                 g2.drawString(edge.secondLabel+"",(tox+fromx)/2+radius+20,(fromy+toy)/2+radius+20);
            }
            g2.draw(path);
        }

        ArrayList<Vector> outNodePositionList = new ArrayList<>();
        for(Edge edge: selfLoopList){
            Vector position = nodeList.get(edge.i).position;
            for(Edge e: edgeList){
                if((edge.i==e.i)&&(edge.i!=e.j)){
                    outNodePositionList.add(nodeList.get(e.j).position);
                }
                /*else if((edge.i==e.j)&&(edge.i!=e.i)){
                    outNodePositionList.add(nodeList.get(e.i).position);
                }*/
            }

            path = getSelfLoop(position, outNodePositionList);
            g2.draw(path);
         //   g2.drawString(edge.label+"",(tox+fromx)/2,(fromy+toy)/2);

        }
/*
        GeneralPath arrowPath = new GeneralPath();
        for(int i=0;i<nodeList.size();i++){
            for(Vector arrow : nodeList.get(i).arrowList) {
                arrowPath.moveTo(nodeList.get(i).position.x, nodeList.get(i).position.y);
                arrowPath.lineTo(nodeList.get(i).position.x + arrow.x, nodeList.get(i).position.y +arrow.y / 10);
            }
        }
        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(0.5f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g2.draw(arrowPath);*/

    }

}
