package tasks;

public class Task implements Comparable<Task>, Cloneable{
    private int taskNumber;

    private int m1Duration;
    private int m2Duration;
    private int m3Duration;

    private int t1Time;
    private int t2Time;

    private int startTime;
    private int finishTime;

    public Task(int taskNumber, int startTime, int finishTime){
        this.taskNumber = taskNumber;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }

    public Task(int taskNumber, int m1Duration, int m2Duration, int m3Duration){
        this.taskNumber = taskNumber;

        this.m1Duration = m1Duration;
        this.m2Duration = m2Duration;
        this.m3Duration = m3Duration;

        t1Time = m1Duration + m2Duration;
        t2Time = m2Duration + m3Duration;
    }

    public int getTaskNumber(){
        return taskNumber;
    }

    @Override
    public String toString(){
        return "Z" + taskNumber;
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

    public int getM1Duration() {
        return m1Duration;
    }

    public int getM2Duration() {
        return m2Duration;
    }

    public int getM3Duration() {
        return m3Duration;
    }

    public int getT1Time() {
        return t1Time;
    }

    public int getT2Time() {
        return t2Time;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getFinishTime() {
        return finishTime;
    }
}
