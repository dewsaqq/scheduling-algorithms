import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.gantt.GanttCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;
import tasks.Machine;
import tasks.TaskManager;

public class Chart extends ApplicationFrame {

    private int number = 0;
    private int maxNumber;

    private static final long serialVersionUID = 1L;
    private static TaskManager tm;


    public Chart(final String title) {
        super(title);

        tm = new TaskManager();
        String filePath = ".//src//main//resources//data.csv";
        tm.initGraphFromFile(filePath);
        tm.modifiedJohnson();
        maxNumber = tm.getNumberOfTasks();

        final GanttCategoryDataset dataset = createDataset();
        final JFreeChart chart = createChart(dataset);

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1000, 600));
        setContentPane(chartPanel);

    }

    public static GanttCategoryDataset createDataset() {
        List<Machine> machines = tm.getMachines();

        final TaskSeriesCollection collection = new TaskSeriesCollection();

        for(Machine list : machines){
            TaskSeries s1 = new TaskSeries("M" + list.getMachineNumber());
            Task t1 = new Task("M" + list.getMachineNumber(), new SimpleTimePeriod(0, tm.getCMax()));

            for(tasks.Task task : list.getTasks()){
                Task st1 = new Task(task.toString() + "", new SimpleTimePeriod(task.getStartTime(), task.getFinishTime()));
                t1.addSubtask(st1);
            }
            s1.add(t1);

            collection.add(s1);
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
                TaskSeriesCollection collection = (TaskSeriesCollection) dataSet;
                String desc = collection.getSeries(series).get("M"+(series + 1)).getSubtask(number++).getDescription();

                if(number == maxNumber) number = 0;
                return desc;
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