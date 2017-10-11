import FSM.FSMEdge;
import FSM.FSMModel;
import FSM.FSMNode;
import GraphDraw.GraphDraw;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by BrokenGardener on 10/10/2017.
 */
public class GraphDrawTest {


    public static void main(String[] args){
        FSMModel fsmModel = new FSMModel();

        ArrayList<FSMEdge> fsmEdgeList = new ArrayList<FSMEdge>(Arrays.asList(new FSMEdge(0,'z'),new FSMEdge(1,'a'),new FSMEdge(4,'b'))  );
        fsmModel.fsmNodeList.add(new FSMNode(fsmEdgeList));


        fsmEdgeList = new ArrayList<FSMEdge>(Arrays.asList(new FSMEdge(4,'a'),new FSMEdge(0,'b'))  );
        fsmModel.fsmNodeList.add(new FSMNode(fsmEdgeList));

        fsmEdgeList = new ArrayList<FSMEdge>(Arrays.asList(new FSMEdge(3,'b'))  );
        fsmModel.fsmNodeList.add(new FSMNode(fsmEdgeList));

        fsmEdgeList = new ArrayList<FSMEdge>(Arrays.asList(new FSMEdge(2,'a'),new FSMEdge(1,'f'))  );
        fsmModel.fsmNodeList.add(new FSMNode(fsmEdgeList));

        fsmEdgeList = new ArrayList<FSMEdge>(Arrays.asList(new FSMEdge(0,'g'),new FSMEdge(3,'b'))  );
        fsmModel.fsmNodeList.add(new FSMNode(fsmEdgeList));

        final GraphDraw graphDraw = new GraphDraw(fsmModel);
        SwingUtilities.invokeLater(new Runnable() {

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
