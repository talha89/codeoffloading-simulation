package ee.ut.simulation;

public class Task {

    private int instructionCount; // in Millions
    private double dataSize; // in MBs

    public Task(int instructionCount, double dataSize) {
        this.instructionCount = instructionCount;
        this.dataSize = dataSize;
    }

    public int getInstructionCount() {
        return instructionCount;
    }

    public void setInstructionCount(int instructionCount) {
        this.instructionCount = instructionCount;
    }

    public double getDataSize() {
        return dataSize;
    }

    public void setDataSize(double dataSize) {
        this.dataSize = dataSize;
    }

}
