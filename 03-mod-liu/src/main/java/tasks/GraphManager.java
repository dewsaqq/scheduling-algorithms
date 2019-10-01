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
    private ListenableGraph<Task, DefaultEdge> g;
    private int cMax = 10;

    public GraphManager(){
        taskList = new CopyOnWriteArrayList<>();
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

    private void addConnection(int taskNumber, int targetTaskNumber, int taskDuration, int targetTaskDuration, int taskDueDate, int taskReleaseTime, int targetTaskDueDate, int targetTaskReleaseTime){
        if(taskNumber == targetTaskNumber) newTask(new Task(taskNumber));
        else {
            Task t1 = newTask(new Task(taskNumber, taskDuration, taskDueDate, taskReleaseTime));
            Task t2 = newTask(new Task(targetTaskNumber, targetTaskDuration, targetTaskDueDate, targetTaskReleaseTime));

            newLink(t1, t2);
        }
    }

    public void initGraphFromFile(String filePath){
        List<String[]> rows = readDataFromFile(filePath);

        for(String[] record : rows){
            addConnection(Integer.parseInt(record[0]),
                    Integer.parseInt(record[1]),
                    Integer.parseInt(record[2]),
                    Integer.parseInt(record[3]),
                    Integer.parseInt(record[4]),
                    Integer.parseInt(record[5]),
                    Integer.parseInt(record[6]),
                    Integer.parseInt(record[7]));
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

    public int getcMax(){
        return cMax;
    }

    public List<Task> modifiedLiu(){
        countModifiedDueDates();

        Collections.sort(taskList);

        return scheduleOnMachine();
    }

    private void countModifiedDueDates(){
        for(Task task : taskList){
            countModifiedDueDate(task);
        }
    }

    private void countModifiedDueDate(Task task){
        List<Integer> dueDates = new ArrayList<>();
        dueDates.add(task.getDueDate());

        for(DefaultEdge de : g.outgoingEdgesOf(task)) {
            Task targetTask = g.getEdgeTarget(de);
            dueDates.add(targetTask.getDueDate());

            getOutgoingDueDates(dueDates, targetTask);
        }

        task.setModifiedDueDate(Collections.min(dueDates));
    }

    private void getOutgoingDueDates(List<Integer> dueDates, Task task){
        for(DefaultEdge de : g.outgoingEdgesOf(task)) {
            Task targetTask = g.getEdgeTarget(de);
            dueDates.add(targetTask.getDueDate());
            getOutgoingDueDates(dueDates, targetTask);
        }
    }

    private List<Task> scheduleOnMachine(){
        List<Task> machine = new ArrayList<>();
        int t = 0;

        while(!taskList.isEmpty()){
            if(!getPresentTasks(t).isEmpty()) {
                Task task = getPresentTasks(t).get(0);
                if(allParentsHaveEnded(task)){
                    task.setDuration(task.getDuration() - 1);
                    machine.add(new Task(task.getTaskNumber(), t, (t+1)));

                    if(task.getDuration() == 0) {
                        task.setFinished(true);
                        taskList.remove(task);
                    }
                }
            }

            t++;
        }

        cMax = t;

        printTimeSchedule(squashTasksOnMachine(machine));
        return squashTasksOnMachine(machine);
    }

    private List<Task> getPresentTasks(int time){
        List<Task> presentTasks = new ArrayList<>();

        for(Task task : taskList){
            if(task.getReleaseTime() <= time){
                presentTasks.add(task);
            }
        }

        return presentTasks;
    }

    private List<Task> squashTasksOnMachine(List<Task> machine){
        List<Task> squashedMachine = new ArrayList<>();
        int i = 1;
        Task currentTask = machine.get(0);

        for(Task task : machine.subList(1, machine.size())){
            if(task.getTaskNumber() == currentTask.getTaskNumber()) {
                i++;
                continue;
            }
            if(task.getTaskNumber() != currentTask.getTaskNumber()){
                squashedMachine.add(new Task(currentTask.getTaskNumber(), currentTask.getStart(), currentTask.getStart()+i));
                currentTask = task;
                i = 1;
            }
        }
        squashedMachine.add(new Task(currentTask.getTaskNumber(), currentTask.getStart(), currentTask.getStart()+i));


        return squashedMachine;
    }

    private void printTimeSchedule(List<Task> machine) {
        System.out.println();
        System.out.println("--TIME SCHEDULE--");
        System.out.println("**Pattern**\n***********\nMachine: |StartTime [tasks.Task] FinishTime|\n***********");

        System.out.print("M1: ");
        for(Task task : machine){
            System.out.print("|" + task.getStart() + " [" + task + "] " + task.getFinish() + "| ");
        }
        System.out.println();
    }


    private boolean allParentsHaveEnded(Task task){
        for(DefaultEdge de : g.incomingEdgesOf(task)){
            Task sourceTask = g.getEdgeSource(de);
            if(!sourceTask.isFinished()) return false;
        }
        return true;
    }
}
