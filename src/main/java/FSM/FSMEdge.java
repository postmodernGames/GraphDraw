package FSM;

/**
 * Created by BrokenGardener on 10/9/2017.
 */
public class FSMEdge {
    public int toIndex;
  public  char match;
    public FSMEdge(int toIndex, char match){
      this.toIndex = toIndex;
      this.match = match;
  }
}
