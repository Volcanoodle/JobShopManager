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
                        jobShopManager.thisMachineAvailable(machineType, machineID);
                        System.out.println(machineType + " " + machineID + " machine proceeding");
                }
        }


        // UR2 example test
        public void exampleUR2Test() {
                //Map<String, Integer> expectedResult = Map.of("FDM",5,"SLA",1);
                JobShopManager jobShopManager = new JobShopManager("FCFS");

                System.out.println("\nStart the machines: \n");
                //Sart three machines of type FDM and two SLA:
                for (int i=1; i<=6; i++) new MachineThread(jobShopManager, "FDM", i).start();
                for (int i=1; i<=2; i++) new MachineThread(jobShopManager, "SLA", i).start();
                try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();} //to allow machine threads to start and run

                //Specify job 1
                Job job1 = new Job(
                                "Job1",
                                List.of(new Operation("FDM", 5),
                                        new Operation("FDM", 3),
                                        new Operation("FDM", 3),
                                        new Operation("SLA", 3)));
   
                //Specify job 2
                Job job2 = new Job(
                                "Job2",
                                List.of(new Operation("FDM", 5),
                                        new Operation("FDM", 3)));

                //Print out and submit jobs
                System.out.println("\nSpecify the Jobs ()   (and note that processing time is not used in FCFS)");
                System.out.println(job1);
                System.out.println(job2);     
                jobShopManager.specifyJobs(List.of(job1, job2));
                //Allow job specifier to run and release machines
                try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}  

                System.out.println("\nNow examine the machines released:\n" 
                        + "\tAs there is no functional code in the JobShopManager yet\n\t" 
                        + "all six FDM and two SLA machine threads have been released to proceed.\n\t"
                        + "The correct result would be to release five FDM and one SLA machines after the\n\t"
                        + "the two jobs had been submitted.\n"

                );    
        }

}
