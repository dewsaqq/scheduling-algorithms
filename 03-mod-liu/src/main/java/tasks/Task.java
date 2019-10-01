package tasks;

public class Task implements Comparable<Task>, Cloneable{
    private int taskNumber;
    private int duration;
    private int dueDate;
    private int modifiedDueDate;
    private int releaseTime;

    private int start = -1;
    private int finish = -1;

    private boolean finished = false;

    public Task(int taskNumber){
        this.taskNumber = taskNumber;
    }

    public Task(int taskNumber, int duration, int dueDate, int releaseTime){
        this.taskNumber = taskNumber;
        this.duration = duration;
        this.dueDate = dueDate;
        this.releaseTime = releaseTime;
    }

    public Task(int taskNumber, int start, int finish){
        this.taskNumber = taskNumber;
        this.start = start;
        this.finish = finish;
    }

    public int getDuration(){
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getTaskNumber(){
        return taskNumber;
    }

    public int getDueDate() {
        return dueDate;
    }

    public void setDueDate(int dueDate) {
        this.dueDate = dueDate;
    }

    public int getModifiedDueDate() {
        return modifiedDueDate;
    }

    public void setModifiedDueDate(int modifiedDueDate) {
        this.modifiedDueDate = modifiedDueDate;
    }

    public int getStart() {
        return start;
    }

    public int getFinish() {
        return finish;
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
        return Integer.compare(this.getModifiedDueDate(), o.getModifiedDueDate());
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public int getReleaseTime() {
        return releaseTime;
    }
}
