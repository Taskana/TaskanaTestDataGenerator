package pro.taskana.data.generation;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskState;
import pro.taskana.adapter.TaskanaAPI;
import pro.taskana.data.enums.AccessType;
import pro.taskana.data.enums.ClassificationType;
import pro.taskana.data.generation.builder.ClassificationBuilder;
import pro.taskana.data.generation.builder.TaskBuilder;
import pro.taskana.data.generation.builder.WorkbasketStructureBuilder;
import pro.taskana.data.generation.util.ElementStack;
import pro.taskana.impl.ClassificationWrapper;
import pro.taskana.impl.DataWrapper;
import pro.taskana.impl.TaskWrapper;
import pro.taskana.impl.WorkbasketAccessItemWrapper;
import pro.taskana.impl.WorkbasketWrapper;
import pro.taskana.export.ScenarioExporter;

/**
 * Class for generate, persist and export test data.
 *
 * @author fe
 * @author el
 *
 */
public class DataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataGenerator.class);

    private static final String OUPUT_PATH_IDENTIFIER = "-o";
    private static TaskanaAPI taskana;

    /**
     * Generate, persist and export test data.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Path outputDir = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(OUPUT_PATH_IDENTIFIER) && args.length > i + 1) {
                outputDir = Paths.get(args[i + 1]);
            }
        }

        taskana = new TaskanaAPI();

        DataWrapper generatedData;
        generatedData = buildDomainA();
        generatedData = generatedData.union(buildDomainB());
        generatedData = generatedData.union(buildDomainC());

        if (outputDir != null) {
            if (outputDir != null && !Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }
            ScenarioExporter.exportData(generatedData, outputDir);
        }
    }

    private static DataWrapper buildDomainA() throws Exception {
        // Build workbaskets
        WorkbasketStructureBuilder structureBuilder = new WorkbasketStructureBuilder("A");
        ElementStack<WorkbasketWrapper> personalWorkbaskets = structureBuilder.createSimpleWorkbaskets(1);
        List<WorkbasketWrapper> layer0 = structureBuilder.newLayer().withWb(1).withNumberOfDistTargets(1)
                .selectFrom(personalWorkbaskets).build();
        List<WorkbasketWrapper> uppermostLayer = structureBuilder.newLayer().withWb(1).withDistTargets(layer0).build();
        persistDomain(structureBuilder);

        // Build classifications
        Map<ClassificationType, List<ClassificationWrapper>> classificationsByType = createClassificationsForDomain(
                "A");

        // Build tasks
        TaskBuilder taskBuilder = new TaskBuilder(classificationsByType, 1);

        WorkbasketWrapper root = uppermostLayer.iterator().next();
        List<WorkbasketWrapper> wbsWithTasks = new ArrayList<>(root.getDirectOrIndirectChildren());
        wbsWithTasks.add(root);

        List<TaskWrapper> tasks = taskBuilder.affect(wbsWithTasks).addTasks(TaskState.COMPLETED, 1)
                .addTasks(TaskState.CLAIMED, 1).addTasks(TaskState.READY, 1).build();
        taskana.createTasks(tasks);

        return new DataWrapper(structureBuilder.getGeneratedWorkbaskets(), tasks,
                classificationsByType.values().stream().flatMap(List::stream).collect(Collectors.toList()));
    }

    private static DataWrapper buildDomainB() throws Exception {
        WorkbasketStructureBuilder structureBuilder = new WorkbasketStructureBuilder("B");

        ElementStack<WorkbasketWrapper> personalWorkbaskets = structureBuilder.createSimpleWorkbaskets(1);

        List<WorkbasketWrapper> layer0 = structureBuilder.newLayer().withWb(1).withNumberOfDistTargets(1)
                .selectFrom(personalWorkbaskets).build();
        ElementStack<WorkbasketWrapper> layer0Wbs = new ElementStack<>(layer0);

        List<WorkbasketWrapper> layer1 = structureBuilder.newLayer().withWb(1).withNumberOfDistTargets(1)
                .selectFrom(layer0Wbs).build();

        List<WorkbasketWrapper> uppermostLayer = structureBuilder.newLayer().withWb(1).withDistTargets(layer1).build();

        persistDomain(structureBuilder);

        // Build classifications
        Map<ClassificationType, List<ClassificationWrapper>> classificationsByType = createClassificationsForDomain(
                "B");

        WorkbasketWrapper root = uppermostLayer.iterator().next();
        List<WorkbasketWrapper> wbsWithTasks = new ArrayList<>(root.getDirectOrIndirectChildren());
        wbsWithTasks.add(root);

        // Build tasks
        TaskBuilder taskBuilder = new TaskBuilder(classificationsByType, 1);
        List<TaskWrapper> tasks = taskBuilder.affect(wbsWithTasks).addTasks(TaskState.COMPLETED, 1)
                .addTasks(TaskState.CLAIMED, 1).addTasks(TaskState.READY, 1).withAttachments(1).build();

        taskana.createTasks(tasks);
        return new DataWrapper(structureBuilder.getGeneratedWorkbaskets(), tasks,
                classificationsByType.values().stream().flatMap(List::stream).collect(Collectors.toList()));
    }

    private static DataWrapper buildDomainC() throws Exception {
        WorkbasketStructureBuilder structureBuilder = new WorkbasketStructureBuilder("C");

        ElementStack<WorkbasketWrapper> personalWorkbaskets = structureBuilder.createSimpleWorkbaskets(99);

        List<WorkbasketWrapper> layer0FwdTo10 = structureBuilder.newLayer().withWb(18).withNumberOfDistTargets(1)
                .selectFrom(personalWorkbaskets).build();

        ElementStack<WorkbasketWrapper> layer1Wb = new ElementStack<>(layer0FwdTo10);

        List<WorkbasketWrapper> layer2FwdTo10 = structureBuilder.newLayer().withWb(7).withNumberOfDistTargets(2)
                .selectFrom(layer1Wb).build();
        ElementStack<WorkbasketWrapper> layer2Wb = new ElementStack<>(layer2FwdTo10);

        List<WorkbasketWrapper> uppermostLayer = structureBuilder.newLayer().withWb(3).withNumberOfDistTargets(2)
                .selectFrom(layer2Wb).build();

        structureBuilder.createUserWithAccessTo("superUser", structureBuilder.getGeneratedWorkbaskets(),
                AccessType.values());
        persistDomain(structureBuilder);

        // Build classifications
        Map<ClassificationType, List<ClassificationWrapper>> classificationsByType = createClassificationsForDomain(
                "C");

        List<WorkbasketWrapper> wbsWith0Attachments = uppermostLayer.get(0).getDirectOrIndirectChildren();
        List<WorkbasketWrapper> wbsWith1Attachment = uppermostLayer.get(1).getDirectOrIndirectChildren();
        List<WorkbasketWrapper> wbsWith2Attachments = uppermostLayer.get(2).getDirectOrIndirectChildren();

        // Build tasks
        TaskBuilder taskBuilder = new TaskBuilder(classificationsByType, 300);

        List<TaskWrapper> tasks = taskBuilder.affect(wbsWith0Attachments).addTasks(TaskState.COMPLETED, 5)
                .addTasks(TaskState.CLAIMED, 2).addTasks(TaskState.READY, 2).withAttachments(0).build();
        taskana.createTasks(tasks);

        tasks = taskBuilder.affect(wbsWith1Attachment).addTasks(TaskState.COMPLETED, 5).addTasks(TaskState.CLAIMED, 2)
                .addTasks(TaskState.READY, 2).withAttachments(1).build();
        taskana.createTasks(tasks);

        tasks = taskBuilder.affect(wbsWith2Attachments).addTasks(TaskState.COMPLETED, 5)
                .addTasks(TaskState.CLAIMED, 2).addTasks(TaskState.READY, 2).withAttachments(2).build();
        taskana.createTasks(tasks);
        return new DataWrapper(structureBuilder.getGeneratedWorkbaskets(), tasks,
                classificationsByType.values().stream().flatMap(List::stream).collect(Collectors.toList()));
    }

    private static Map<ClassificationType, List<ClassificationWrapper>> createClassificationsForDomain(String domain)
            throws Exception {
        ClassificationBuilder classificationBuilder = new ClassificationBuilder(domain);
        classificationBuilder.newClassificationCategory("MASCHINELL").withType(ClassificationType.AUFGABENTYP)
                .withChildren(100).build();
        classificationBuilder.newClassificationCategory("MANUELL").withType(ClassificationType.AUFGABENTYP)
                .withChildren(100).build();
        classificationBuilder.newClassificationCategory("EXTERN").withType(ClassificationType.AUFGABENTYP)
                .withChildren(100).build();
        classificationBuilder.newClassificationCategory("DOKTYP_EXTERN").withType(ClassificationType.DOKUMENTTYP)
                .withChildren(100).build();
        taskana.createClassification(classificationBuilder.getAllGeneratedClassifications());
        return classificationBuilder.getClassificationsByType();
    }

    private static void persistDomain(WorkbasketStructureBuilder domainBuilder) throws Exception {
        LOGGER.info("Persisting domain {}", domainBuilder.getDomainName());
        List<WorkbasketWrapper> workbaskets = domainBuilder.getGeneratedWorkbaskets();
        List<WorkbasketAccessItemWrapper> workbasketAccessItems = domainBuilder.getGeneratedAccessItems();

        taskana.createWorkbaskets(workbaskets);
        taskana.createDistributionTargets(workbaskets);
        taskana.createWorkbasketAccesItem(workbasketAccessItems);
        LOGGER.info("Domain {} successfully persisted", domainBuilder.getDomainName());
    }

}
