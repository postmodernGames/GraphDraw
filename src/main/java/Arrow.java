import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class Arrow  extends ApplicationFrame{
    Arrow(){
        super("Arrow");
        setSize(400,400);
        center();
        setBackground(new Color((float)232/255,(float)237/255,(float)175/255));
        setVisible(true);
    }




    public GeneralPath getArrow(double fromX, double fromY, double toX, double toY){
        GeneralPath path = new GeneralPath(Path2D.WIND_EVEN_ODD);
        path.moveTo(fromX,fromY);
        path.lineTo(toX, toY);
        double lambda=0.9;
        double slx= (1-lambda)*fromX + lambda*toX;
        double sly= (1-lambda)*fromY + lambda*toY;
        double m_inverse = - (toX-fromX)/(toY-fromY);
        double w = 5;
        double v1x= slx + w;
        double v1y= sly + w*m_inverse;
        double v2x= slx - w;
        double v2y= sly - w*m_inverse;
        path.lineTo(v1x,v1y);
        path.moveTo(toX,toY);
        path.lineTo(v2x,v2y);
        return path;
    }

    Point2D point = new Point2D.Double();
    public Point2D toPolarRadians(Point2D cart){
        return new Point2D.Double(cart.distance(0,0),Math.atan2(cart.getY(),cart.getX()));
    }

    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        renderingHints.add(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2.setRenderingHints(renderingHints);


        g2.setColor(new Color((float)193/255,(float)193/255,(float)187/255));
        Ellipse2D.Double circle1 = new Ellipse2D.Double(75,75,50,50);
        Ellipse2D.Double circle2 = new Ellipse2D.Double(289,159,50,50);
        g2.fill(circle1);
        g2.fill(circle2);
        g2.setColor(new Color((float)157/255,(float)158/255,(float)154/255));
        g2.draw(circle1);

        double deltax = circle2.getCenterX()-circle1.getCenterX();
        double deltay = circle2.getCenterY()-circle1.getCenterY();
        double Fx = circle1.getCenterX()+25*Math.cos(Math.atan2(deltay,deltax));
        double Fy = circle1.getCenterY()+25*Math.sin(Math.atan2(deltay,deltax));
        double Tx = circle2.getCenterX()-25*Math.cos(Math.atan2(deltay,deltax));
        double Ty = circle2.getCenterY()-25*Math.sin(Math.atan2(deltay,deltax));

        g2.setPaint(Color.black);
        g2.setStroke(new BasicStroke(2.0f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));

        g2.draw((GeneralPath)getArrow(Fx,Fy,Tx,Ty));

    }

    public static void main(String args[]) {
        new Arrow();

    }

}
