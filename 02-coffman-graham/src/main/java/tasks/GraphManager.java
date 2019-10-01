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
    private List<Task> collectionA;
    private List<List<Task>> machines;
    private ListenableGraph<Task, DefaultEdge> g;
    private int cMax = 10;

    public GraphManager(){
        taskList = new CopyOnWriteArrayList<>();
        machines = new ArrayList<>();
        collectionA = new CopyOnWriteArrayList<>();
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

    private void addConnection(int taskNumber, int targetTaskNumber){
        if(taskNumber == targetTaskNumber) newTask(new Task(taskNumber));
        else {
            Task t1 = newTask(new Task(taskNumber));
            Task t2 = newTask(new Task(targetTaskNumber));

            newLink(t1, t2);
        }
    }

    public void initGraphFromFile(String filePath){
        List<String[]> rows = readDataFromFile(filePath);

        for(String[] record : rows){
            addConnection(Integer.parseInt(record[0]),
                    Integer.parseInt(record[1]));
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

    public void coffmanGraham(){
        countLabels();
        printTimeSchedule();
    }

    private void countLabels(){
        int i = 1;

        Collections.reverse(taskList);

        for(Task task : taskList){
            if(g.outgoingEdgesOf(task).isEmpty()){
                task.setTaskLabel(i++);
                countCollectionA(task);
            }
        }

        while(!collectionA.isEmpty()){
            Task myTask = getEarliestTask();
            myTask.setTaskLabel(i++);
            countCollectionA(myTask);
            collectionA.remove(myTask);
        }
    }

    private void countSList(){
        for(Task task : collectionA){
            if(task.sList.isEmpty()){
                for(DefaultEdge de : g.outgoingEdgesOf(task)) {
                    Task targetTask = g.getEdgeTarget(de);
                    task.sList.add(targetTask.getTaskLabel());
                }
            }
        }
    }

    private void countCollectionA(Task task){
        for(DefaultEdge de : g.incomingEdgesOf(task)) {
            Task sourceTask = g.getEdgeSource(de);
            if(allChildrenHaveLabel(sourceTask)) {
                collectionA.add(sourceTask);
            }
        }

        countSList();
    }

    private boolean allChildrenHaveLabel(Task task){
        for(DefaultEdge de : g.outgoingEdgesOf(task)){
            Task targetTask = g.getEdgeTarget(de);
            if(targetTask.getTaskLabel() == 0) return false;
        }
        return true;
    }

    private Task getEarliestTask(){
        Task earliestTask = collectionA.get(0);
        for(int i = 1; i < collectionA.size(); i++){
            earliestTask = compareTaskLists(earliestTask, collectionA.get(i));
        }

        return earliestTask;
    }

    private Task compareTaskLists(Task task1, Task task2){
        int counter;
        Task shorterListTask;

        if(task2.sList.size() >= task1.sList.size()) {
            counter = task1.sList.size();
            shorterListTask = task1;
        } else {
            counter = task2.sList.size();
            shorterListTask = task2;
        }

        for(int i = 0; i < counter; i++){
            int result = task1.getSortedSList().get(i).compareTo(task2.getSortedSList().get(i));
            if(result < 0) return task1;
            else if(result > 0) return task2;
            else continue;
        }

        return shorterListTask;
    }


    private void printTimeSchedule() {
        createMachinesList(2);

        int machine = machines.size();
        Collections.reverse(machines);

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

    private void createMachinesList(int numberOfMachines){
        for(int i = 0; i < numberOfMachines; i++) machines.add(new ArrayList<>());

        Collections.sort(taskList, Comparator.comparingInt(Task::getTaskLabel));
        Collections.reverse(taskList);

        int counter = 0;
        while(!taskList.isEmpty()){
            for(int j = 0; j < numberOfMachines; j++){
                Task currentTask = taskList.get(0);
                if(!allParentsHaveEnded(currentTask)) currentTask = taskList.get(j+j+1);

                machines.get(j).add(currentTask);
                currentTask.setFinished(true);
                currentTask.setStart(counter);
                currentTask.setFinish(counter + 1);
                taskList.remove(currentTask);
            }
            counter++;
        }

        cMax = counter;
    }

    private boolean allParentsHaveEnded(Task task){
        for(DefaultEdge de : g.incomingEdgesOf(task)){
            Task sourceTask = g.getEdgeSource(de);
            if(!sourceTask.isFinished()) return false;
        }
        return true;
    }
}
