package tasks;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class TaskManager {
    private static final int NUMBER_OF_MACHINES = 3;
    private List<Machine> machines;

    private List<Task> taskList;

    private List<Task> n1Set;
    private List<Task> n2Set;

    private List<Integer> m1Times;
    private List<Integer> m2Times;
    private List<Integer> m3Times;

    private int cMax = 10;

    public TaskManager(){
        machines = new ArrayList<>();
        taskList = new CopyOnWriteArrayList<>();
    }

    private void newTask(Task task){
        for(Task currentTask : taskList) {
            if(currentTask.getTaskNumber() == task.getTaskNumber()) {
                throw new IllegalArgumentException("Task with this number already exists!");
            }
        }

        taskList.add(task);
    }

    public void initGraphFromFile(String filePath){
        List<String[]> rows = readDataFromFile(filePath);

        for(String[] record : rows){
            newTask(new Task(Integer.parseInt(record[0]),
                    Integer.parseInt(record[1]),
                    Integer.parseInt(record[2]),
                    Integer.parseInt(record[3])));
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

    public int getCMax(){
        return cMax;
    }

    public void modifiedJohnson(){
        if(!isM2Dominated()) throw new IllegalArgumentException("Machine 2 is not dominated!");

        splitTasksIntoTwoSets();

        printTimeSchedule();
    }

    private boolean isM2Dominated(){
        initMachineTimes();

        int m1Min = Collections.min(m1Times);
        int m3Min = Collections.min(m3Times);
        int m2Max = Collections.max(m2Times);

        return m1Min >= m2Max || m3Min >= m2Max;
    }

    private void initMachineList(){
        for(int i = 1; i <= NUMBER_OF_MACHINES; i++){
            machines.add(new Machine(i));
        }
    }

    private void initMachineTimes(){
        m1Times = new ArrayList<>();
        m2Times = new ArrayList<>();
        m3Times = new ArrayList<>();

        for(Task task : taskList){
            m1Times.add(task.getM1Duration());
            m2Times.add(task.getM2Duration());
            m3Times.add(task.getM3Duration());
        }
    }

    private void splitTasksIntoTwoSets(){
        n1Set = new ArrayList<>();
        n2Set = new ArrayList<>();

        for(Task task : taskList){
            if(task.getT1Time() < task.getT2Time()) n1Set.add(task);
            else n2Set.add(task);
        }

        sortSets();
    }

    private void sortSets(){
        n1Set.sort(Comparator.comparingInt(Task::getT1Time));
        n2Set.sort(Comparator.comparingInt(Task::getT2Time));
        Collections.reverse(n2Set);
    }

    private void scheduleOnMachines(){
        initMachineList();

        n1Set.addAll(n2Set);

        int m1TaskStart = 0;
        int currentTaskStart;

        for(Task task : n1Set){
            currentTaskStart = m1TaskStart + task.getM1Duration();
            machines.get(0).addTask(task.getTaskNumber(),
                    m1TaskStart,
                    task.getM1Duration());

            machines.get(1).addTask(task.getTaskNumber(),
                    currentTaskStart,
                    task.getM2Duration());

            currentTaskStart += task.getM2Duration();
            machines.get(2).addTask(task.getTaskNumber(),
                    currentTaskStart,
                    task.getM3Duration());

            m1TaskStart += task.getM1Duration();
        }

        countCMax();
    }

    private void countCMax(){
        cMax = machines.get(0).getEarliestStart();
        for(int i = 1; i < NUMBER_OF_MACHINES; i++){
            if(machines.get(i).getEarliestStart() > cMax) cMax = machines.get(i).getEarliestStart();
        }
    }

    private void printTimeSchedule() {
        scheduleOnMachines();


        System.out.println();
        System.out.println("--TIME SCHEDULE--");
        System.out.println("**Pattern**\n***********\nMachine: |StartTime [Task] FinishTime|\n***********");

        for(Machine list : machines){
            System.out.print("M" + list.getMachineNumber() + ": ");
            for(Task task : list.getTasks()){
                System.out.print("|" + task.getStartTime() + " [" + task + "] " + task.getFinishTime() + "| ");
            }
            System.out.println();
        }

        System.out.println("***********\nCMax: " + cMax);
    }

    public List<Machine> getMachines(){
        return machines;
    }

    public int getNumberOfTasks(){
        return taskList.size();
    }
}
