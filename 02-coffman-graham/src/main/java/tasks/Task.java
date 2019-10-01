package tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Task implements Comparable<Task>, Cloneable{
    private int taskNumber;
    private int taskLabel = 0;
    private int duration = 1;
    private int start = -1;
    private int finish = -1;
    private boolean finished = false;

    public List<Integer> sList = new ArrayList<>();

    public Task(int taskNumber){
        this.taskNumber = taskNumber;
    }

    public Task(int taskNumber, int duration){
        this.taskNumber = taskNumber;
        this.duration = duration;
    }

    public int getDuration(){
        return duration;
    }

    public int getTaskNumber(){
        return taskNumber;
    }

    public int getTaskLabel() {
        return taskLabel;
    }

    public void setTaskLabel(int taskLabel) {
        this.taskLabel = taskLabel;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getFinish() {
        return finish;
    }

    public void setFinish(int finish) {
        this.finish = finish;
    }

    @Override
    public String toString(){
        return "Z" + taskNumber + "{" + taskLabel + "}";
    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Task)) return false;
        Task otherTask = (Task)other;
        return (otherTask.getTaskNumber() == this.taskNumber);
    }

    @Override
    public int compareTo(Task o) {
        return Integer.compare(this.getTaskNumber(), o.getTaskNumber());
    }

    public int getMaxSListValue(){
        return Collections.max(sList);
    }

    public List<Integer> getSortedSList(){
        Collections.sort(sList, Collections.reverseOrder());
        return sList;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
