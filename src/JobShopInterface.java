
//This interface MUST not be changed in anyway

import java.util.List;

public interface JobShopInterface {
    //Changing this interface will likely cause a compile failure when we try to compile your JobShopInterface.java
    //and if so, will not be marked
    
    public void specifyJobs(List<Job> jobs);
    public String thisMachineAvailable(String type, int ID);
}


