import java.util.List;

//Important: 
// 1. The only concurrent or thread-safe classes that you 
//    allowed to import for this class are the two shown below.
// 2. This class must deal with all exceptions locally, i.e. 
//    it's public methods must not 'throw' any exceptions to the caller
//    otherwise our compilation of your code will fail.

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;


public class JobShopManager implements JobShopInterface{        

    private final String mode;
    private final ReentrantLock lock;
    private final Queue<Job> pendingJobs;
    private final HashMap<String, Integer> availableMachines;
    
    // Constructor
    public JobShopManager(String mode) {
        // FCFS = First Come First Served
        // SJF = Shortest Job First 
        this.mode = mode;
        this.lock = new ReentrantLock();
        this.pendingJobs = new LinkedList<>();
        this.availableMachines = new HashMap<>();
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
