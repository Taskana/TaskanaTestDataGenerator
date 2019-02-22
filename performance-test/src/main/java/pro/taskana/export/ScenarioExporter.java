package pro.taskana.export;

import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import pro.taskana.impl.ClassificationWrapper;
import pro.taskana.impl.DataWrapper;
import pro.taskana.impl.TaskWrapper;
import pro.taskana.impl.WorkbasketWrapper;
import pro.taskana.export.io.FileType;
import pro.taskana.export.io.FileUtils;

public class ScenarioExporter {

    /**
     * Generate files containing informations of the build test data.
     * 
     * @param data
     *            which was persisted.
     * @param outputDir
     *            output directory for new files.
     */
    public static void exportData(DataWrapper data, Path outputDir) {
        Random rnd = new Random();
        FileUtils fileUtils = new FileUtils(outputDir, FileType.CSV);

        DataExporter<WorkbasketWrapper> ownerKeyExistingTasks = new DataExporter<>(data.workbaskets);
        ownerKeyExistingTasks.addPredicate(wb -> !wb.getTasks().isEmpty());
        ownerKeyExistingTasks.addPredicate(wb -> wb.getDomain().equals("C"));
        
        ownerKeyExistingTasks.addLineValueProducer(wb -> wb.getKey());
        ownerKeyExistingTasks.addLineValueProducer(wb -> wb.getDomain());
        ownerKeyExistingTasks.addLineValueProducer(wb -> wb.getOwner());
        fileUtils.createFile("00_auslesen_einer_aufgabe_aus_einem_postkorb",
                ownerKeyExistingTasks.generateFileContent());

        DataExporter<TaskWrapper> taskIDOwner = new DataExporter<>(data.tasks);
        taskIDOwner.maxLines(100000);
        taskIDOwner.addPredicate(t -> t.getDomain().equals("C"));
        taskIDOwner.addLineValueProducer(t -> t.getId());
        taskIDOwner.addLineValueProducer(t -> data.workbaskets.stream()
                .filter(wb -> wb.getKey().equals(t.getWorkbasketKey())).findFirst().get().getOwner());
        fileUtils.createFile("01_auslesen_einer_aufgabe_per_id", taskIDOwner.generateFileContent());

        DataExporter<TaskWrapper> porOwner = new DataExporter<>(data.tasks);
        taskIDOwner.maxLines(100000);
        porOwner.addPredicate(t -> t.getDomain().equals("C"));
        porOwner.addLineValueProducer(t -> t.getPrimaryObjRef().getValue());
        porOwner.addLineValueProducer(t -> "superUser");
        fileUtils.createFile("02_suchen_von_aufgaben_mit_ordnungsbegriff", porOwner.generateFileContent());

        DataExporter<WorkbasketWrapper> ownerWbKey = new DataExporter<>(data.workbaskets);
        ownerWbKey.addPredicate(wb -> wb.getDomain().equals("C"));
        ownerWbKey.addLineValueProducer(wb -> wb.getKey());
        ownerWbKey.addLineValueProducer(wb -> wb.getDomain());
        ownerWbKey.addLineValueProducer(wb -> wb.getOwner());
        fileUtils.createFile("03_00_lesen_der_daten_eines_postkorbs", ownerWbKey.generateFileContent());
        
        DataExporter<WorkbasketWrapper> ownerWbId = new DataExporter<>(data.workbaskets);
        ownerWbId.addPredicate(wb -> wb.getDomain().equals("C"));
        ownerWbId.addLineValueProducer(wb -> wb.getId());
        ownerWbId.addLineValueProducer(wb -> wb.getOwner());
        fileUtils.createFile("03_01_lesen_der_daten_eines_postkorbs_per_id", ownerWbId.generateFileContent());

        DataExporter<TaskWrapper> typeCategoryCustomDomain = new DataExporter<>(data.tasks);
        typeCategoryCustomDomain.maxLines(100000);
        typeCategoryCustomDomain.addPredicate(t -> t.getDomain().equals("C"));
        typeCategoryCustomDomain.addLineValueProducer(t -> t.getClassificationSummary().getType());
        typeCategoryCustomDomain.addLineValueProducer(t -> t.getClassificationCategory());       
        typeCategoryCustomDomain.addLineValueProducer(t -> {
            String[] values = t.getClassification().getCustom1().split(",");
            return values[rnd.nextInt(values.length-1)];
        });
        typeCategoryCustomDomain.addLineValueProducer(t -> t.getDomain());
        typeCategoryCustomDomain.addConstantLineValue(data.workbaskets.iterator().next().getOwner());
        fileUtils.createFile("04_00_suchen_einer_klassifikation", typeCategoryCustomDomain.generateFileContent());
        
        DataExporter<TaskWrapper> classificationId = new DataExporter<>(data.tasks);
        classificationId.maxLines(100000);
        classificationId.addPredicate(t -> t.getDomain().equals("C"));
        classificationId.addLineValueProducer(t -> t.getClassificationSummary().getId());
        classificationId.addConstantLineValue(data.workbaskets.iterator().next().getOwner());
        fileUtils.createFile("04_01_suchen_einer_klassifikation_per_id", classificationId.generateFileContent());

        DataExporter<TaskWrapper> keyDomain = new DataExporter<>(data.tasks);
        typeCategoryCustomDomain.maxLines(100000);
        keyDomain.addPredicate(t -> t.getDomain().equals("C"));
        keyDomain.addLineValueProducer(t -> t.getClassificationKey());
        keyDomain.addLineValueProducer(t -> t.getDomain());
        keyDomain.addConstantLineValue(data.workbaskets.iterator().next().getOwner());
        fileUtils.createFile("05_lesen_einer_klassifikation", keyDomain.generateFileContent());
        
        DataExporter<WorkbasketWrapper> createUpdateTransferCompleteTask = new DataExporter<>(data.workbaskets);
        createUpdateTransferCompleteTask.addPredicate(wb -> !wb.getDirectChildren().isEmpty());
        createUpdateTransferCompleteTask.addPredicate(wb -> wb.getDomain().equals("C"));
        createUpdateTransferCompleteTask.addLineValueProducer(wb -> wb.getKey());
        createUpdateTransferCompleteTask.addLineValueProducer(wb -> wb.getId());
        createUpdateTransferCompleteTask.addLineValueProducer(wb -> wb.getDomain());
        createUpdateTransferCompleteTask.addLineValueProducer(wb -> {
            ClassificationWrapper rndClassification = data.classifications.get(rnd.nextInt(data.classifications.size()-1));
            return rndClassification.getKey() + "," + rndClassification.getCategory();
        });
        createUpdateTransferCompleteTask.addLineValueProducer(wb -> wb.getDirectChildren().iterator().next().getKey());
        createUpdateTransferCompleteTask.addLineValueProducer(wb -> wb.getDirectChildren().iterator().next().getId());
        createUpdateTransferCompleteTask.addLineValueProducer(wb -> wb.getOwner());
        fileUtils.createFile("06_aufgabe_erstellen_claimen_aktualisieren_abschliessen_weiterleiten", createUpdateTransferCompleteTask.generateFileContent());

        DataExporter<WorkbasketWrapper> ownerKeyForPermission = new DataExporter<>(data.workbaskets);
        ownerKeyForPermission.addPredicate(wb -> wb.getDirectOrIndirectChildren().size() < 25);
        ownerKeyForPermission.addPredicate(wb -> wb.getDomain().equals("C"));
        ownerKeyForPermission.addLineValueProducer(wb -> wb.getOwner());
        List<List<String>> content = ownerKeyForPermission.generateFileContent();
        fileUtils.createFile("07_postkoerbe_suchen_auf_die_der_aufrufer_das_recht_open_hat", content);
        fileUtils.createFile("08_postkoerbe_suchen_auf_die_der_aufrufer_das_recht_append_hat", content);
    }

}
