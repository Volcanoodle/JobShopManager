public class App {
    public static void main(String[] args) throws Exception {
        Tests tests = new Tests();
        
        System.out.println("========== Starting F29OC Coursework Concurrency Tests ==========");
        
        tests.testUR1();
        tests.testUR2();
        tests.testUR3();
        tests.testUR4();
        tests.testUR5();
        tests.testUR6();
        
        System.out.println("========== All Test Cases Executed Successfully ==========");
        System.exit(0);
    }	
}
