package pro.taskana.impl;

import pro.taskana.Classification;
import pro.taskana.TaskState;
import pro.taskana.impl.TaskImpl;

public class TaskWrapper extends TaskImpl{
    
    private Classification classification;
    
    public TaskWrapper(WorkbasketWrapper workbasket, TaskState state) {
        setState(state);
        workbasket.addTask(this);
    }
    
    public void setClassification(Classification classification) {
        this.setClassificationKey(classification.getKey());
        this.setClassificationCategory(classification.getCategory());
        this.setClassificationSummary(classification.asSummary());
        this.classification = classification;
    }
    
    public Classification getClassification() {
        return this.classification;
    }
}
