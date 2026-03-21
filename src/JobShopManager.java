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
    private final LinkedList<Job> pendingJobs;
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
            trySchedule();
        } finally {
            lock.unlock();
        }
    }

    private int calculateTotalProcessingTime(Job job) {
        int totalTime = 0;
        for (Operation op : job.operations) {
            if (op != null && op.processingTime != null) {
                totalTime += op.processingTime;
            }
        }
        return totalTime;
    }

    private void addJobToQueue(Job newJob) {
        if ("SJF".equals(mode)) {
            int newJobTime = calculateTotalProcessingTime(newJob);
            int insertIndex = 0;
            for (Job existingJob : pendingJobs) {
                if (calculateTotalProcessingTime(existingJob) > newJobTime) {
                    break;
                }
                insertIndex++;
            }
            pendingJobs.add(insertIndex, newJob);
        } else {
            pendingJobs.add(newJob);
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

    private void allocateJob(Job job) {
        for (Operation op : job.operations) {
            String type = op.machineType;
            Queue<Integer> freeMachines = availableMachines.get(type);
            int machineID = freeMachines.poll(); 
            String machineKey = type + "-" + machineID;
            machineAllocations.put(machineKey, job.jobName);
            Condition myCondition = machineConditions.get(machineKey);
            if (myCondition != null) {
                myCondition.signal();
        }
    }

    private void trySchedule() {
        while (!pendingJobs.isEmpty()) {
            Job nextJob = pendingJobs.peek(); 
            if (canSatisfyJob(nextJob)) {
                pendingJobs.poll();
                allocateJob(nextJob);
            } else {
                break;
            }
        }
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
            trySchedule();
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