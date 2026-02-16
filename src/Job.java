
//This file MUST not be changed in anyway

import java.util.List;

public class Job {
    public final String jobName;
    public final List<Operation> operations;

    public Job(String jobName, List<Operation> operations) {
        this.jobName = jobName;
        this.operations = List.copyOf(operations); // creates an immutable copy
    }

    public String toString2() {
        return "Job{" +
                "jobName='" + jobName + '\'' +
                ", operations=" + operations +
                '}';
    }
    
    @Override
    public String toString() {
        return "\n\tjobName='" + jobName + '\'' +
                "\n\toperations= " + operations;
    }
}

