package FSM;

import java.util.ArrayList;

/**
 * Created by BrokenGardener on 10/9/2017.
 */
public class FSMNode  {
    boolean acceptFlag = false;
    ArrayList<FSMEdge> outList = new ArrayList<FSMEdge>();
    ArrayList<FSMNode> inList = new ArrayList<FSMNode>();
}
