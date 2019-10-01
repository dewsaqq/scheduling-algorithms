package tasks;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class GraphManager {
    private List<Task> taskList;
    private List<List<Task>> machines;
    private ListenableGraph<Task, DefaultEdge> g;
    private int cMax;

    public GraphManager(){
        taskList = new ArrayList<>();
        machines = new ArrayList<>();
        g = new DefaultListenableGraph<>(new DirectedAcyclicGraph<>(DefaultEdge.class));
    }

    private Task newTask(Task task){
        for(Task myTask : taskList) {
            if(myTask.getTaskNumber() == task.getTaskNumber()) {
                return myTask;
            }
        }

        taskList.add(task);
        Collections.sort(taskList);
        g.addVertex(task);

        return task;
    }

    private void newLink(Task sourceTask, Task targetTask){
        if(sourceTask.getTaskNumber() > targetTask.getTaskNumber()) throw new IllegalArgumentException("Target task cannot be before source task!");
        g.addEdge(sourceTask, targetTask);
    }

    private void addConnection(int taskNumber, int taskDuration, int targetTaskNumber, int targetTaskDuration){
        if(taskNumber == targetTaskNumber) newTask(new Task(taskNumber, taskDuration));
        else {
            Task t1 = newTask(new Task(taskNumber, taskDuration));
            Task t2 = newTask(new Task(targetTaskNumber, targetTaskDuration));

            newLink(t1, t2);
        }
    }

    public void initGraphFromFile(String filePath){
        List<String[]> rows = readDataFromFile(filePath);

        for(String[] record : rows){
            addConnection(Integer.parseInt(record[0]),
                    Integer.parseInt(record[1]),
                    Integer.parseInt(record[2]),
                    Integer.parseInt(record[3]));
        }
    }

    private List<String[]> readDataFromFile(String filePath){
        List<String[]> rows = new ArrayList<>();

        try {
            Reader reader = Files.newBufferedReader(Paths.get(filePath));
            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
            rows = csvReader.readAll();
            reader.close();
            csvReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rows;
    }

    public ListenableGraph<Task, DefaultEdge> getGraph(){
        return g;
    }

    public List<List<Task>> getMachines(){
        return machines;
    }

    public int getcMax(){
        return cMax;
    }

    public void CPM(){
        calculateTaskTimes();
        printTimeSchedule(findCriticalPath());
    }

    private void calculateTaskTimes(){
        for(Task task : taskList){
            calculateTaskTime(task);
        }

        printStartTimes();
    }

    private void calculateTaskTime(Task task){
        if(g.incomingEdgesOf(task).isEmpty()){
            task.setStart(0);
            task.setFinish(task.getDuration());
        } else{
            int maxStart = 0;
            for(DefaultEdge de : g.incomingEdgesOf(task)){
                Task sourceTask = g.getEdgeSource(de);
                if(sourceTask.getStart() == -1) calculateTaskTime(sourceTask);
                if((sourceTask.getStart() + sourceTask.getDuration()) > maxStart) {
                    maxStart = sourceTask.getStart() + sourceTask.getDuration();
                }
            }
            task.setStart(maxStart);
            task.setFinish(maxStart + task.getDuration());
        }
    }

    private List<Task> findCriticalPath(){
        List<Task> criticalPath = new ArrayList<>();
        Task criticalTask = new Task(-1, -1);
        for(Task task : taskList){
            if(task.getFinish() > criticalTask.getFinish()) criticalTask = task;
        }
        criticalPath.add(criticalTask);
        findCriticalPathInSet(g.incomingEdgesOf(criticalTask), criticalPath);

        Collections.reverse(criticalPath);
        System.out.println("--Critical path in graph--\n" + criticalPath);

        return criticalPath;
    }

    private void findCriticalPathInSet(Set<DefaultEdge> tasksToCheck, List<Task> criticalPath){
        Task criticalTask = new Task(-1, -1);
        for(DefaultEdge de : tasksToCheck){
            Task sourceTask = g.getEdgeSource(de);
            if(sourceTask.getFinish() > criticalTask.getFinish()) criticalTask = sourceTask;
        }
        criticalPath.add(criticalTask);
        if(!g.incomingEdgesOf(criticalTask).isEmpty()) findCriticalPathInSet(g.incomingEdgesOf(criticalTask), criticalPath);
    }

    private void printStartTimes(){
        System.out.println("--Tasks calculated start time--");
        for(Task task : taskList){
            System.out.println(task + " - " + task.getStart());
        }
        System.out.println();
    }

    private void printTimeSchedule(List<Task> cp) {
        createMachinesList(cp);

        int machine = machines.size();

        System.out.println();
        System.out.println("--TIME SCHEDULE--");
        System.out.println("**Pattern**\n***********\nMachine: |StartTime [tasks.Task] FinishTime|\n***********");

        for(List<Task> list : machines){
            System.out.print("M" + machine + ": ");
            for(Task task : list){
                System.out.print("|" + task.getStart() + " [" + task + "] " + task.getFinish() + "| ");
            }
            System.out.println();
            machine--;
        }
    }

    private void createMachinesList(List<Task> cp){
        List<Task> taskListCopy = createTaskListCopy();

        for(Task task : taskListCopy){
            if(cp.contains(task)) taskListCopy.remove(task);
        }

        machines.add(cp);

        Collections.reverse(cp);
        cMax = cp.get(0).getFinish();
        Collections.reverse(cp);
        while (!taskListCopy.isEmpty()) {
            List<Task> currentList = new ArrayList<>();
            int max = 0;
            for (Task task : taskList) {
                if (taskListCopy.contains(task)) {
                    if (task.getStart() >= max && task.getFinish() < cMax) {
                        currentList.add(task);
                        max = task.getFinish();
                        taskListCopy.remove(task);
                    }
                }
            }
            machines.add(currentList);
        }

        Collections.reverse(machines);

    }

    private List<Task> createTaskListCopy(){
        List<Task> taskListCopy = new CopyOnWriteArrayList<>();
        Iterator<Task> iterator = taskList.iterator();

        while(iterator.hasNext()){
            taskListCopy.add((Task) iterator.next().clone());
        }

        return taskListCopy;
    }

}
