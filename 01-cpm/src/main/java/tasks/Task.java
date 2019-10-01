package tasks;

public class Task implements Comparable<Task>, Cloneable{
    private int taskNumber;
    private int duration;
    private int start = -1;
    private int finish = -1;

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
        return "Z" + taskNumber + "::" + duration;
    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Task))return false;
        Task otherTask = (Task)other;
        return (otherTask.getTaskNumber() == this.taskNumber);
    }

    @Override
    public int compareTo(Task o) {
        return Integer.compare(this.getTaskNumber(), o.getTaskNumber());
    }

    @Override
    protected Object clone(){
        Task clone = null;
        try
        {
            clone = (Task) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
        return clone;
    }
}
