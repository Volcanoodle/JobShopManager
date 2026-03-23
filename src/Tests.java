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

        public void testUR3() {
                System.out.println("\n--- 运行 UR3 测试 ---");
                JobShopManager jobShopManager = new JobShopManager("FCFS");

                Job job1 = new Job("Job1",
                                List.of(new Operation("FDM", 5),
                                        new Operation("FDM", 3),
                                        new Operation("FDM", 3),
                                        new Operation("SLA", 3)));
   
                Job job2 = new Job("Job2",
                                List.of(new Operation("FDM", 5),
                                        new Operation("FDM", 3)));

                System.out.println("先提交 2 个作业请求...");
                jobShopManager.specifyJobs(List.of(job1));
                jobShopManager.specifyJobs(List.of(job2)); 
                try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}
                System.out.println("随后启动混合类型的机器 (6台 FDM, 2台 SLA)...");
                for (int i=1; i<=6; i++) new MachineThread(jobShopManager, "FDM", i).start();
                for (int i=1; i<=2; i++) new MachineThread(jobShopManager, "SLA", i).start();
                try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}  

                System.out.println("检查结果：");
                System.out.println("预期行为：结果应与 UR2 完全一致，共有 5 台 FDM 和 1 台 SLA 打印 'proceeding'，其余阻塞。");
                System.out.println("--- UR3 测试结束 ---\n");
        }

        public void testUR4() {
                System.out.println("\n--- 运行 UR4 测试 ---");
                JobShopManager jobShopManager = new JobShopManager("FCFS");

                System.out.println("第一阶段：启动 2 台 FDM...");
                new MachineThread(jobShopManager, "FDM", 1).start();
                new MachineThread(jobShopManager, "FDM", 2).start();
                try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

                Job job1 = new Job("Job1", List.of(
                        new Operation("FDM", 1), new Operation("FDM", 1), new Operation("FDM", 1)
                ));
                System.out.println("第二阶段：提交 Job1 (需 3 台 FDM)... (目前只有 2 台，应该等待)");
                jobShopManager.specifyJobs(List.of(job1));
                try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

                System.out.println("第三阶段：再启动 2 台 FDM... (此时 Job1 应该满足并唤醒 3 台，剩下 1 台闲置等待)");
                new MachineThread(jobShopManager, "FDM", 3).start();
                new MachineThread(jobShopManager, "FDM", 4).start();
                try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

                Job job2 = new Job("Job2", List.of(
                        new Operation("FDM", 1), new Operation("FDM", 1)
                ));
                System.out.println("第四阶段：提交 Job2 (需 2 台 FDM)... (目前只有 1 台闲置，应该等待)");
                jobShopManager.specifyJobs(List.of(job2));
                try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

                System.out.println("第五阶段：最后启动 1 台 FDM... (此时 Job2 应该满足并唤醒剩余机器)");
                new MachineThread(jobShopManager, "FDM", 5).start();
                try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}

                System.out.println("检查结果：");
                System.out.println("预期行为：5 台 FDM 机器全部打印 'proceeding'，且没有死锁。");
                System.out.println("--- UR4 测试结束 ---\n");
        }

        public void testUR5() {
                System.out.println("\n--- 运行 UR5 测试 ---");
                JobShopManager jobShopManager = new JobShopManager("FCFS");

                System.out.println("启动 2 台 FDM 和 1 台 SLA...");
                new MachineThread(jobShopManager, "FDM", 1).start();
                new MachineThread(jobShopManager, "FDM", 2).start();
                new MachineThread(jobShopManager, "SLA", 1).start();
                try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

                String uniqueJobName = "Special_UR5_Job";
                Job job1 = new Job(uniqueJobName, List.of(
                        new Operation("FDM", 1), new Operation("SLA", 1)
                ));
                
                System.out.println("提交作业: " + uniqueJobName + " (需 1 台 FDM, 1 台 SLA)...");
                jobShopManager.specifyJobs(List.of(job1));
                try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}

                System.out.println("检查结果：");
                System.out.println("预期行为：控制台上必须有 1 台 FDM 和 1 台 SLA 打印 'proceeding for job: Special_UR5_Job'。");
                System.out.println("          剩余的 1 台 FDM 应该继续保持阻塞（不打印任何内容）。");
                System.out.println("--- UR5 测试结束 ---\n");
        }

}
