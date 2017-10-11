package GraphLayout;

public class Edge {
    public    int i;
    public      int j;
    public     char label;
    public boolean doubleFlag;
    public char secondLabel;
    public   Edge(int i, int j, char label){
        this.i = i;
        this.j = j;
        this.label = label;
        this.doubleFlag = false;
    }
    public   Edge(int i, int j, char label,char secondLabel){
        this.i = i;
        this.j = j;
        this.label = label;
        this.secondLabel = secondLabel;
        this.doubleFlag = true;
    }
}
