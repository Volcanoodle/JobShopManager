import java.util.List;

//Important: 
// the only concurrent or thread-safe classes that you 
// allowed to import for this class are the two shown below.

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;


public class JobShopManager implements JobShopInterface{        
    // Constructor
    public JobShopManager(String mode) {
        // FCFS = First Come First Served
        // SJF = Shortest Job First 
    }

    @Override
    public void specifyJobs(List<Job> jobs) {
        //Your code here
    }
    
    @Override
    public String thisMachineAvailable(String type, int ID) {
        //Your code here
        return "your return string here";
    }   
}
