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
        public void testUR2() {
                System.out.println("\n--- 运行 UR2 测试 ---");
                JobShopManager jobShopManager = new JobShopManager("FCFS");

                System.out.println("启动混合类型的机器 (6台 FDM, 2台 SLA)...");
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

                System.out.println("多次调用 specifyJobs 提交多个作业...");
                jobShopManager.specifyJobs(List.of(job1));
                jobShopManager.specifyJobs(List.of(job2)); 
                try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}  

                System.out.println("检查结果：");
                System.out.println("预期行为：控制台上应该共有 5 台 FDM 和 1 台 SLA 打印 'proceeding'，其余 1 台 FDM 和 1 台 SLA 阻塞。");
                System.out.println("--- UR2 测试结束 ---\n");
        }

}
