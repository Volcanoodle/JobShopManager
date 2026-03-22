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


        public void testUR1() {
                System.out.println("\n--- 运行 UR1 测试 ---");
                JobShopManager jobShopManager = new JobShopManager("FCFS");

                System.out.println("启动 5 台 FDM 机器...");
                for (int i = 1; i <= 5; i++) {
                        new MachineThread(jobShopManager, "FDM", i).start();
                }
                try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }

                Job job1 = new Job(
                                "Job1",
                                List.of(new Operation("FDM", 1),
                                        new Operation("FDM", 1)));
                System.out.println("提交 1 个作业，需要 2 台 FDM 机器...");
                jobShopManager.specifyJobs(List.of(job1));
                
                try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }  

                System.out.println("检查结果：");
                System.out.println("预期行为：控制台上应该只有 2 台 FDM 机器打印 'proceeding'，其余 3 台被安全阻塞。");
                System.out.println("--- UR1 测试结束 ---\n");
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
