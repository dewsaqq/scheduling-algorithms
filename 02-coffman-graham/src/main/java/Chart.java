import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.IntervalCategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.gantt.GanttCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;
import tasks.GraphManager;

public class Chart extends ApplicationFrame {

    private static final long serialVersionUID = 1L;
    private static GraphManager gm;


    public Chart(final String title) {
        super(title);

        gm = new GraphManager();
        String filePath = ".//src//main//resources//data.csv";
//        String filePath = ".//src//main//resources//data2.csv";
        gm.initGraphFromFile(filePath);

        gm.coffmanGraham();

        final GanttCategoryDataset dataset = createDataset();
        final JFreeChart chart = createChart(dataset);

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1000, 600));
        setContentPane(chartPanel);

    }

    public static GanttCategoryDataset createDataset() {
        List<List<tasks.Task>> machines = gm.getMachines();
        int machine = machines.size();

        final TaskSeriesCollection collection = new TaskSeriesCollection();

        for(List<tasks.Task> list : machines){
            TaskSeries s1 = new TaskSeries("M" + machine);
            Task t1 = new Task("M" + machine, new SimpleTimePeriod(0, gm.getcMax()));

            for(tasks.Task task : list){
                Task st1 = new Task(task.toString(), new SimpleTimePeriod(task.getStart(), task.getFinish()));
//                t1.addSubtask(st1);
                s1.add(st1);
            }
//            s1.add(t1);
            collection.add(s1);

            machine--;
        }

        return collection;
    }

    private JFreeChart createChart(final GanttCategoryDataset dataset) {
        final JFreeChart chart = ChartFactory.createGanttChart(
                "Schedule Time ", // chart title
                "MACHINES", // domain axis label
                "TIME", // range axis label
                dataset, // data
                true, // include legend
                true, // tooltips
                false // urls
        );

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        MyGanttRenderer renderer = new MyGanttRenderer();
        plot.setRenderer(renderer);

        renderer.setDefaultItemLabelGenerator(new CategoryItemLabelGenerator() {

            public String generateLabel(CategoryDataset dataSet, int series, int categories) {
                /* your code to get the label */
                return "Z"+series;
            }

            public String generateColumnLabel(CategoryDataset dataset, int categories) {
                return dataset.getColumnKey(categories).toString();
            }

            public String generateRowLabel(CategoryDataset dataset, int series) {
                return dataset.getRowKey(series).toString();
            }
        });

        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.INSIDE6, TextAnchor.BOTTOM_CENTER));
        renderer.setDrawBarOutline(true);
        renderer.setSeriesOutlineStroke(0, new BasicStroke(2.5f));
        renderer.setSeriesOutlineStroke(1, new BasicStroke(2.5f));

        DateAxis axis = (DateAxis) plot.getRangeAxis();

        axis.setDateFormatOverride(new SimpleDateFormat("S"));
        return chart;
    }

    public static void main(final String[] args) {
        final Chart demo = new Chart("Chart");
        demo.pack();
        demo.setVisible(true);
    }
}