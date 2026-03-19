//Important: 
// 1. The only concurrent or thread-safe classes that you 
//    allowed to import for this class are the two shown below.
// 2. This class must deal with all exceptions locally, i.e. 
//    it's public methods must not 'throw' any exceptions to the caller
//    otherwise our compilation of your code will fail.
import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class JobShopManager implements JobShopInterface {        
    
    private final String mode;
    private final ReentrantLock lock;
    private final Queue<Job> pendingJobs;
    private final HashMap<String, Queue<Integer>> availableMachines;
    private final HashMap<String, Condition> machineConditions;
    private final HashMap<String, String> machineAllocations;

    // Constructor
    public JobShopManager(String mode) {
        // FCFS = First Come First Served
        // SJF = Shortest Job First 
        this.mode = mode;
        this.lock = new ReentrantLock();
        this.pendingJobs = new LinkedList<>();
        this.availableMachines = new HashMap<>();
        this.machineConditions = new HashMap<>();
        this.machineAllocations = new HashMap<>(); 
    }

    @Override
    public void specifyJobs(List<Job> jobs) {
        lock.lock();
        try {
            pendingJobs.addAll(jobs);
        //todo:(FCFS or SJF)
        } finally {
            lock.unlock();
        } finally {
            lock.unlock();
        }
    }

    private boolean canSatisfyJob(Job job) {
        HashMap<String, Integer> requiredMachines = new HashMap<>();
        for (Operation op : job.operations) {
            requiredMachines.put(op.machineType, requiredMachines.getOrDefault(op.machineType, 0) + 1);
        }
        for (String type : requiredMachines.keySet()) {
            Queue<Integer> freeMachines = availableMachines.get(type);
            int availableCount = (freeMachines == null) ? 0 : freeMachines.size();
            if (availableCount < requiredMachines.get(type)) {
                return false; 
            }
        }
        return true; 
    }

    @Override
    public String thisMachineAvailable(String type, int ID) {
        lock.lock();
        try {
            if (!availableMachines.containsKey(type)) {
                availableMachines.put(type, new LinkedList<>());
            }
            availableMachines.get(type).add(ID);
            String machineKey = type + "-" + ID;
            if (!machineConditions.containsKey(machineKey)) {
                machineConditions.put(machineKey, lock.newCondition());
            }
            Condition myCondition = machineConditions.get(machineKey);

            while (!machineAllocations.containsKey(machineKey)) {
                try {
                    myCondition.await(); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            String allocatedJobName = machineAllocations.remove(machineKey);
            return allocatedJobName; 
        } finally {
            lock.unlock();
        }
    } 
}