package FSM;

import java.util.ArrayList;

/**
 * Created by BrokenGardener on 10/9/2017.
 */
public class FSMNode  {
    boolean acceptFlag = false;
    public ArrayList<FSMEdge> outList = new ArrayList<FSMEdge>();
    ArrayList<FSMNode> inList = new ArrayList<FSMNode>();

    public FSMNode(ArrayList<FSMEdge> outList){
        this.outList = outList;
    }
}
