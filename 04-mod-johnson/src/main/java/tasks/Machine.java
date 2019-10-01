package tasks;

import java.util.ArrayList;
import java.util.List;

public class Machine {
    private int machineNumber;
    private List<Task> tasks;
    private int earliestStart = 0;

    public Machine(int machineNumber){
        this.machineNumber = machineNumber;
        tasks = new ArrayList<>();
    }

    public void addTask(int taskNumber, int start, int duration){
        if(start < earliestStart) {
            start = earliestStart;
        }

        earliestStart = start + duration;
        tasks.add(new Task(taskNumber, start, earliestStart));
    }

    public List<Task> getTasks(){
        return tasks;
    }

    public int getMachineNumber() {
        return machineNumber;
    }

    public int getEarliestStart() {
        return earliestStart;
    }
}
