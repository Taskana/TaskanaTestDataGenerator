package pro.taskana.data.generation.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import pro.taskana.Task;
import pro.taskana.TaskState;
import pro.taskana.data.enums.ClassificationType;
import pro.taskana.impl.ClassificationWrapper;
import pro.taskana.impl.TaskWrapper;
import pro.taskana.impl.WorkbasketWrapper;
import pro.taskana.impl.ClassificationImpl;

/**
 * Builder for creating {@link Task}.
 * 
 * @author fe
 *
 */
public class TaskBuilder {
    
	private static final int NUMBER_OF_CALLBACK_INFOS = 5;
	private static final int NUMBER_OF_CUSTOM_ATTRIBUTES = 20;
	private static final Map<String, String> CALLBACK_INFO = new HashMap<>();
	private static final Map<String, String> CUSTOM_ATTRIBUTES = new HashMap<>();
    private List<ClassificationWrapper> taskClassifications;
    private Random rnd;
    private List<WorkbasketWrapper> affectedWorkbaskets;
    private Map<TaskState, Integer> taskDistribution;
    
    private AttachmentBuilder attachmentBuilder;
    private ObjectReferenceBuilder objectReferenceBuilder;
    
    private int numberOfAttachments;
    
	static {
		for (int i = 1; i <= NUMBER_OF_CALLBACK_INFOS; i++) {
			CALLBACK_INFO.put("Property_" + i,
					"Property Value of Property_" + i);
		}
		for (int i = 1; i <= NUMBER_OF_CUSTOM_ATTRIBUTES; i++) {
			CUSTOM_ATTRIBUTES.put("Custom attribute_" + i,
					"Property value of custom attribute" + i);
		}
	}

    public TaskBuilder(Map<ClassificationType, List<ClassificationWrapper>> classifications, int numberOfDifferentPOR, int maxAttachments) {
        this.taskDistribution = new HashMap<>();
        this.affectedWorkbaskets = new ArrayList<>();
        this.taskClassifications = classifications.get(ClassificationType.AUFGABENTYP);
        this.rnd = new Random();
        attachmentBuilder = new AttachmentBuilder(classifications, maxAttachments);
        this.numberOfAttachments = 0;
        objectReferenceBuilder = new ObjectReferenceBuilder(numberOfDifferentPOR);
    }
    
    public TaskBuilder(Map<ClassificationType, List<ClassificationWrapper>> classifications, int numberOfPOR) {
        this(classifications, numberOfPOR, 0);
    }
    
    
    public TaskBuilder affect(List<WorkbasketWrapper> workbaskets) {
        this.affectedWorkbaskets = workbaskets;
        this.taskDistribution = new HashMap<>();
        this.numberOfAttachments = 0;
        return this;
    }
    
    public TaskBuilder addTasks(TaskState state, int numberOfTasks) {
        int tasks = numberOfTasks;
        if(taskDistribution.containsKey(state) && taskDistribution.get(state) != null) {
            tasks += taskDistribution.get(state);
        }
        taskDistribution.put(state, tasks);
        return this;
    }
    
    public TaskBuilder withAttachments(int numberOfAttachments) {
        this.numberOfAttachments = numberOfAttachments;
        return this;
    }
    
    public List<TaskWrapper> build() {
        List<TaskWrapper> generatedTaks = new ArrayList<>();
        for (WorkbasketWrapper wb : affectedWorkbaskets) {
           generatedTaks.addAll(generateTasksForWorkbasket(wb));
        }
        return generatedTaks;
    }
    
    private List<TaskWrapper> generateTasksForWorkbasket(WorkbasketWrapper workbasket) {
        List<TaskWrapper> tasksInWb = new ArrayList<>();
        for (TaskState state : taskDistribution.keySet()) {
            List<TaskWrapper> tasksInState = generateTasks(workbasket, state, taskDistribution.get(state));
            tasksInWb.addAll(tasksInState);
        }
        return tasksInWb;
    }
    
    private List<TaskWrapper> generateTasks(WorkbasketWrapper workbasket, TaskState state, int quantity) {
        List<TaskWrapper> tasks = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            TaskWrapper task = new TaskWrapper(workbasket, state);
            task.setOwner(workbasket.getOwner());
            task.setNote(workbasket.getOwner());
            task.setWorkbasketKey(workbasket.getKey());
            task.setWorkbasketSummary(workbasket.asSummary());
            task.setDomain(workbasket.getDomain());
            int rndIndex = rnd.nextInt(taskClassifications.size());
            ClassificationImpl taskClassification = taskClassifications.get(rndIndex);
            task.setClassification(taskClassification);
            
            task.setPrimaryObjRef(objectReferenceBuilder.getObjectReference());
            task.setAttachments(attachmentBuilder.getAttachments(numberOfAttachments));
            tasks.add(task);
			task.setCallbackInfo(CALLBACK_INFO);
			task.setCustomAttributes(CUSTOM_ATTRIBUTES);
        }
        return tasks;
    }

}
