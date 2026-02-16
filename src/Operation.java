
//This file MUST not be changed in anyway

public class Operation {
    public final String machineType;
    public final Integer processingTime;

    public Operation(String machineType, Integer processingTime) {
        this.machineType = machineType;
        this.processingTime = processingTime;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "machineType='" + machineType + '\'' +
                ", processingTime=" + processingTime +
                '}';
    }
}
