import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Tests {

        public class MachineThread extends Thread {
                public final String machineType;
                public final int machineID;
                private final JobShopManager jobShopManager;

                public MachineThread(JobShopManager jobShopManager, String machineType, int machineID) {
                        this.jobShopManager = jobShopManager;   
                        this.machineType = machineType;
                        this.machineID = machineID;
                        this.setName("Machine-" + machineType + "-" + machineID);
                }

                @Override
                public void run() {
                        String assignedJob = jobShopManager.thisMachineAvailable(machineType, machineID);
                        System.out.println(machineType + " " + machineID + " machine proceeding for job: " + assignedJob);
                }
        }

        public void testUR1() {
                System.out.println("\n--- Running UR1 Test ---");
                JobShopManager jobShopManager = new JobShopManager("FCFS");

                System.out.println("Starting 5 FDM machines...");
                for (int i = 1; i <= 5; i++) {
                        new MachineThread(jobShopManager, "FDM", i).start();
                }
                try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }

                Job job1 = new Job(
                                "Job1",
                                List.of(new Operation("FDM", 1),
                                        new Operation("FDM", 1)));
                System.out.println("Submitting 1 job, requiring 2 FDM machines...");
                jobShopManager.specifyJobs(List.of(job1));
                
                try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }  

                System.out.println("Checking results:");
                System.out.println("Expected behavior: Only 2 FDM machines should print 'proceeding' on the console, the remaining 3 are safely blocked.");
                System.out.println("--- UR1 Test Finished ---\n");
        }

        public void testUR2() {
                System.out.println("\n--- Running UR2 Test ---");
                JobShopManager jobShopManager = new JobShopManager("FCFS");

                System.out.println("Starting mixed types of machines (6 FDM, 2 SLA)...");
                for (int i=1; i<=6; i++) new MachineThread(jobShopManager, "FDM", i).start();
                for (int i=1; i<=2; i++) new MachineThread(jobShopManager, "SLA", i).start();
                
                try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();} 
                Job job1 = new Job("Job1",
                                List.of(new Operation("FDM", 5),
                                        new Operation("FDM", 3),
                                        new Operation("FDM", 3),
                                        new Operation("SLA", 3)));
   
                Job job2 = new Job("Job2",
                                List.of(new Operation("FDM", 5),
                                        new Operation("FDM", 3)));

                System.out.println("Calling specifyJobs multiple times to submit multiple jobs...");
                jobShopManager.specifyJobs(List.of(job1));
                jobShopManager.specifyJobs(List.of(job2)); 
                try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}  

                System.out.println("Checking results:");
                System.out.println("Expected behavior: A total of 5 FDM and 1 SLA should print 'proceeding', the remaining 1 FDM and 1 SLA are blocked.");
                System.out.println("--- UR2 Test Finished ---\n");
        }

        public void testUR3() {
                System.out.println("\n--- Running UR3 Test ---");
                JobShopManager jobShopManager = new JobShopManager("FCFS");

                Job job1 = new Job("Job1",
                                List.of(new Operation("FDM", 5),
                                        new Operation("FDM", 3),
                                        new Operation("FDM", 3),
                                        new Operation("SLA", 3)));
   
                Job job2 = new Job("Job2",
                                List.of(new Operation("FDM", 5),
                                        new Operation("FDM", 3)));

                System.out.println("Submitting 2 job requests first...");
                jobShopManager.specifyJobs(List.of(job1));
                jobShopManager.specifyJobs(List.of(job2)); 
                try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}
                
                System.out.println("Subsequently starting mixed types of machines (6 FDM, 2 SLA)...");
                for (int i=1; i<=6; i++) new MachineThread(jobShopManager, "FDM", i).start();
                for (int i=1; i<=2; i++) new MachineThread(jobShopManager, "SLA", i).start();
                try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}  

                System.out.println("Checking results:");
                System.out.println("Expected behavior: Results should be exactly the same as UR2, a total of 5 FDM and 1 SLA print 'proceeding', the rest are blocked.");
                System.out.println("--- UR3 Test Finished ---\n");
        }

        public void testUR4() {
                System.out.println("\n--- Running UR4 Test ---");
                JobShopManager jobShopManager = new JobShopManager("FCFS");

                System.out.println("Phase 1: Starting 2 FDM machines...");
                new MachineThread(jobShopManager, "FDM", 1).start();
                new MachineThread(jobShopManager, "FDM", 2).start();
                try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

                Job job1 = new Job("Job1", List.of(
                        new Operation("FDM", 1), new Operation("FDM", 1), new Operation("FDM", 1)
                ));
                System.out.println("Phase 2: Submitting Job1 (requires 3 FDM)... (Currently only 2 available, should wait)");
                jobShopManager.specifyJobs(List.of(job1));
                try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

                System.out.println("Phase 3: Starting 2 more FDM machines... (Job1 should now be satisfied and wake up 3 machines, leaving 1 idle)");
                new MachineThread(jobShopManager, "FDM", 3).start();
                new MachineThread(jobShopManager, "FDM", 4).start();
                try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

                Job job2 = new Job("Job2", List.of(
                        new Operation("FDM", 1), new Operation("FDM", 1)
                ));
                System.out.println("Phase 4: Submitting Job2 (requires 2 FDM)... (Currently only 1 idle, should wait)");
                jobShopManager.specifyJobs(List.of(job2));
                try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

                System.out.println("Phase 5: Finally starting 1 FDM machine... (Job2 should now be satisfied and wake up remaining machines)");
                new MachineThread(jobShopManager, "FDM", 5).start();
                try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}

                System.out.println("Checking results:");
                System.out.println("Expected behavior: All 5 FDM machines print 'proceeding', and no deadlocks occur.");
                System.out.println("--- UR4 Test Finished ---\n");
        }

        public void testUR5() {
                System.out.println("\n--- Running UR5 Test ---");
                JobShopManager jobShopManager = new JobShopManager("FCFS");

                System.out.println("Starting 2 FDM and 1 SLA machines...");
                new MachineThread(jobShopManager, "FDM", 1).start();
                new MachineThread(jobShopManager, "FDM", 2).start();
                new MachineThread(jobShopManager, "SLA", 1).start();
                try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

                String uniqueJobName = "Special_UR5_Job";
                Job job1 = new Job(uniqueJobName, List.of(
                        new Operation("FDM", 1), new Operation("SLA", 1)
                ));
                
                System.out.println("Submitting job: " + uniqueJobName + " (requires 1 FDM, 1 SLA)...");
                jobShopManager.specifyJobs(List.of(job1));
                try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}

                System.out.println("Checking results:");
                System.out.println("Expected behavior: Exactly 1 FDM and 1 SLA must print 'proceeding for job: Special_UR5_Job'.");
                System.out.println("                   The remaining 1 FDM should remain blocked (print nothing).");
                System.out.println("--- UR5 Test Finished ---\n");
        }

        public void testUR6() {
                System.out.println("\n--- Running UR6 Test ---");
                JobShopManager jobShopManager = new JobShopManager("SJF");

                Job jobLong = new Job("Job_Long", List.of(
                        new Operation("FDM", 50), new Operation("FDM", 50)
                ));
                
                Job jobShort = new Job("Job_Short", List.of(
                        new Operation("FDM", 5), new Operation("FDM", 5)
                ));

                System.out.println("Submitting Job_Long (total time 100) first, then Job_Short (total time 10)...");
                jobShopManager.specifyJobs(List.of(jobLong));
                jobShopManager.specifyJobs(List.of(jobShort));
                
                try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

                System.out.println("Starting 2 FDM machines (limited resources, causing competition)...");
                new MachineThread(jobShopManager, "FDM", 1).start();
                new MachineThread(jobShopManager, "FDM", 2).start();
                try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}

                System.out.println("Checking results:");
                System.out.println("Expected behavior: Since SJF mode is enabled, although Job_Long queued first, machines should be prioritized for the shorter job.");
                System.out.println("                   The console must print 2 machines 'proceeding for job: Job_Short'.");
                System.out.println("--- UR6 Test Finished ---\n");
        }
}