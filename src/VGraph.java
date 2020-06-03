import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.util.*;

public class VGraph {

    private Digraph<String, String> graph;
    private SmartPlacementStrategy strategy;
    private SmartGraphPanel<String, String> graphView;
    private SFG sfg;
    private Stack<String> froms;
    private Stack<String> tos;
    private Stack<String> weights;
    private static VGraph vGraph;
    private Set<String> set;

    private VGraph(){
        graph = new DigraphEdgeList<>();
        strategy = new SmartCircularSortedPlacementStrategy();
        graphView = new SmartGraphPanel<>(graph, strategy);
        graphView.setPrefSize(1000,620);
        graphView.setLayoutX(120);
        graphView.setLayoutY(0);
        froms = new Stack<>();
        tos = new Stack<>();
        weights = new Stack<>();
        set = new HashSet<>();
    }

    public static VGraph getInstance(){
        if (vGraph == null) {
            vGraph = new VGraph();
        }
        return vGraph;
    }

    public void draw(Parent root){
        ((VBox)root).getChildren().add(graphView);
    }

    public void addEdge(String v1, String v2, String weight){
        froms.push(v1);
        tos.push(v2);
        weights.push(weight);
        if (!set.contains(v1)) {
            set.add(v1);
            graph.insertVertex(v1);
        }
        if (!set.contains(v2)) {
            set.add(v2);
            graph.insertVertex(v2);
        }
        graph.insertEdge(v1,v2,weight);
    }

    public void back(){
        if (froms.empty()) return;
        froms.pop();
        tos.pop();
        weights.pop();
        Stack<String> froms1 = new Stack<>();
        Stack<String> tos1 = new Stack<>();
        Stack<String> weights1 = new Stack<>();
        Iterator<String> iteratorFrom = froms.iterator();
        Iterator<String> iteratorTo = tos.iterator();
        Iterator<String> iteratorWeight = weights.iterator();
        while(iteratorFrom.hasNext()) {
            froms1.push(iteratorFrom.next());
            tos1.push(iteratorTo.next());
            weights1.push(iteratorWeight.next());
        }
        clear();
        iteratorFrom = froms1.iterator();
        iteratorTo = tos1.iterator();
        iteratorWeight = weights1.iterator();
        while(iteratorFrom.hasNext()) {
            addEdge(iteratorFrom.next(),iteratorTo.next(),iteratorWeight.next());
        }
    }

    public String solve(){
        sfg = new SFG();
        Iterator<String> iteratorFrom = froms.iterator();
        Iterator<String> iteratorTo = tos.iterator();
        Iterator<String> iteratorWeight = weights.iterator();
        while(iteratorFrom.hasNext()) {
            try {
                sfg.addEdge(iteratorFrom.next(),iteratorTo.next(),Double.parseDouble(iteratorWeight.next()));
            } catch (Exception e) {
                return "Error, string weight";
            }
        }
        if (!sfg.checkOut()) {
            return "There is no sink node";
        } else if (!sfg.checkIn()) {
            return "There is no input node";
        } else if (!sfg.checkConnected()) {
            return "The graph is not connected";
        }
        return String.valueOf(sfg.cal());
    }

    public void clear() {
        Collection<Edge<String,String>> edges = graph.edges();
        Collection<Vertex<String>> vertices =  graph.vertices();
        for(Edge<String,String> edge: edges){
            graph.removeEdge(edge);
        }
        for(Vertex<String> vertex: vertices) {
            graph.removeVertex(vertex);
            set.remove(vertex.element());
        }
        froms.clear();
        tos.clear();
        weights.clear();
    }

    public void init(){
        graphView.init();
    }

    public void update(){
        graphView.update();
    }

    public void auto(boolean flag) {
        graphView.setAutomaticLayout(flag);
    }
}
