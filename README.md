

# DO NOT FORK YET #

## F29OC 2025-26 1st Oportunity Coursework Stub project 

This project contains the stub files for the above.
The FAQ for this is located at https://canvas.hw.ac.uk/courses/31914/pages/coursework-specification?module_item_id=2328435 

- **You MUST FORK this project to your own private namespace BEFORE cloning it to your local repo**
- **The url of your fork must be https://gitlab-student.macs.hw.ac.uk/\<your-username>\/f29oc-2025-26-cw1**
- **You MUST NOT change the name of this project after you fork it**
- **You MUST NOT add any files or branches to your project**
- **Your submission must capable of compiling in java 21, with the original stub project code, without producing compile errors**

## Files that you MUST NOT edit:
 - Job.java 
 - Operation.java 
 - Interface.java 

## Files that you should edit:
- App.java
- JobShopManager.java 
- Tests.java (currently contains exampleUR2Test)

JobShopManager.java may only use the following concurrent or thread-safe classes
-  java.util.concurrent.locks.ReentrantLock;
 - java.util.concurrent.locks.Condition;
 - It must not through any exceptions (they must be delt with locally)

 Tests.java may use any Java 21 library


## What happens if you run the app as is
The example test will fail as there is no appropriate code in JobShopManager.

The output will be:

```
Start the machines:

FDM 3 machine proceeding
SLA 1 machine proceeding
FDM 6 machine proceeding
FDM 4 machine proceeding
FDM 2 machine proceeding
FDM 5 machine proceeding
SLA 2 machine proceeding
FDM 1 machine proceeding

Specify the Jobs: and note that processing time is not used in FCFS

        jobName='Job1'
        operations= [Operation{machineType='FDM', processingTime=5}, Operation{machineType='FDM', processingTime=3}, Operation{machineType='FDM', processingTime=3}, Operation{machineType='SLA', processingTime=3}]

        jobName='Job2'
        operations= [Operation{machineType='FDM', processingTime=5}, Operation{machineType='FDM', processingTime=3}]

Now examine the machines released:
        As there is no functional code in the JobShopManager yet
        all six FDM and two SLA machine threads have been released to proceed.
        The correct result would be to release five FDM and one SLA machines after the
        the two jobs had been submitted.
```


