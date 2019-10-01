import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.*;
import org.jgrapht.*;
import org.jgrapht.ext.*;
import org.jgrapht.graph.*;

import javax.swing.*;
import java.awt.*;

import com.mxgraph.util.mxConstants;
import tasks.GraphManager;
import tasks.Task;

public class App extends JApplet{
    private static final long serialVersionUID = 2202072534703043194L;
    private static final Dimension DEFAULT_SIZE = new Dimension(1000, 600);

    private JGraphXAdapter<Task, DefaultEdge> jgxAdapter;

    public static void main(String[] args){
        App applet = new App();
        applet.init();

        JFrame frame = new JFrame();
        frame.getContentPane().add(applet);
        frame.setTitle("Modified Liu Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void init(){
        GraphManager gm = new GraphManager();
        String filePath = ".//src//main//resources//data.csv";
//        String filePath = ".//src//main//resources//data2.csv";
        gm.initGraphFromFile(filePath);

        gm.modifiedLiu();

        ListenableGraph<Task, DefaultEdge> g = gm.getGraph();

        jgxAdapter = new JGraphXAdapter<>(g);
        jgxAdapter.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_NOLABEL, "1");

        setPreferredSize(DEFAULT_SIZE);
        mxGraphComponent component = new mxGraphComponent(jgxAdapter);
        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(false);
        getContentPane().add(component);
        resize(DEFAULT_SIZE);

        mxHierarchicalLayout layout = new mxHierarchicalLayout(jgxAdapter);

        layout.setInterRankCellSpacing(80);
        layout.setIntraCellSpacing(80);
        layout.setFineTuning(true);
        layout.setOrientation(SwingConstants.WEST);

        layout.execute(jgxAdapter.getDefaultParent());
    }
}