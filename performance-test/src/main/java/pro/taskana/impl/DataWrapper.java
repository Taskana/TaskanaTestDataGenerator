package pro.taskana.impl;

import java.util.ArrayList;
import java.util.List;

public class DataWrapper {

    public final List<WorkbasketWrapper> workbaskets;
    public final List<TaskWrapper> tasks;
    public final List<ClassificationWrapper> classifications;
    
    public DataWrapper(List<WorkbasketWrapper> workbaskets, List<TaskWrapper> tasks, List<ClassificationWrapper> classifications) {
        super();
        this.workbaskets = workbaskets;
        this.classifications = classifications;
        this.tasks = tasks;
    }
    
    public DataWrapper union(DataWrapper other) {
        if (other == null || this.equals(other)) {
            return this;
        }
        List<WorkbasketWrapper> allWorkbaskets = new ArrayList<>(workbaskets);
        allWorkbaskets.addAll(other.workbaskets);
        List<TaskWrapper> allTasks = new ArrayList<>(tasks);
        allTasks.addAll(other.tasks);
        List<ClassificationWrapper> allClassifications = new ArrayList<>();
        allClassifications.addAll(other.classifications);
        return new DataWrapper(allWorkbaskets, allTasks, allClassifications);
    }
    
}
