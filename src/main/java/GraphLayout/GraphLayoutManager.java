package GraphLayout;

import FSM.FSMModel;

import java.util.ArrayList;
import static java.lang.Math.*;
/**
 * Created by BrokenGardener on 10/10/2017.
 */
public class GraphLayoutManager {

    double L = 100.0;

    public  GraphLayoutManager(ArrayList<Node> nodeList, ArrayList<Edge> edgeList){
        boolean updatingDone = false;
        while(!updatingDone){
            updatingDone = update(nodeList,edgeList);
        }
    }

    //return true if updating is done
    public boolean update(ArrayList<Node> nodeList, ArrayList<Edge> edgeList) {

        for (int i = 0; i < nodeList.size(); i++) {
            nodeList.get(i).force.reset();
            nodeList.get(i).arrowList.clear();
        }

        for (int i = 0; i < nodeList.size()-1; i++) {
            for (int j = i+1; j < nodeList.size(); j++) {

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

                    nodeList.get(i).force.x +=  w * deltax;
                    nodeList.get(j).force.x -=  w * deltax;
                    nodeList.get(i).force.y +=  w * deltay;
                    nodeList.get(j).force.y -=  w * deltay;

                }
            }
        }

     /*  for (int i = 0; i < nodeList.size(); i++){
            Numerator +=  nodeList.get(i).force.dot(nodeList.get(i).position-nodeList.get(i).oldposition);
            Denominator +=  nodeList.get(i).force.dot( nodeList.get(i).force);
        }

        double gamma = Math.abs(Numerator/Denominator);
        */
        double gamma= 0.01;

        double maxForce=0;

        for (int i = 0; i < nodeList.size(); i++) {
            nodeList.get(i).position.y += nodeList.get(i).force.y*gamma;
            nodeList.get(i).position.x += nodeList.get(i).force.x*gamma;
            maxForce = max(max(abs(nodeList.get(i).force.x),abs(nodeList.get(i).force.y)),maxForce);
        }

        return maxForce < 1;

    }

    public boolean isConnected(int i, int j, ArrayList<Edge> edgeList) {
        for (Edge edge : edgeList) {
            if (((edge.i == i) && (edge.j == j)) || ((edge.i == j) && (edge.j == i))) return true;
        }
        return false;
    }


}
