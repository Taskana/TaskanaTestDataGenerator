package pro.taskana.data.generation.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pro.taskana.Attachment;
import pro.taskana.data.enums.ClassificationType;
import pro.taskana.impl.AttachmentWrapper;
import pro.taskana.impl.ClassificationWrapper;
import pro.taskana.impl.AttachmentImpl;
import pro.taskana.impl.ClassificationImpl;

/**
 * Class wraps the functionality for creating {@link Attachment}.
 * 
 * @author fe
 *
 */
public class AttachmentBuilder {

    private static final int DEFAULT_AMOUNT_OBJECT_REFERENCES = 50;

    private List<ClassificationWrapper> documentClassifications;
    private ObjectReferenceBuilder objectReferenceBuilder;

    private int currentClassificationCounter;
    private int maxAmountAttachments;
    private int currentAttachmentAmount;

    public AttachmentBuilder(Map<ClassificationType, List<ClassificationWrapper>> classifications) {
        this(classifications, new ObjectReferenceBuilder(DEFAULT_AMOUNT_OBJECT_REFERENCES));
    }

    public AttachmentBuilder(Map<ClassificationType, List<ClassificationWrapper>> classifications,
            int maxAmountAttachments) {
        this(classifications, maxAmountAttachments, new ObjectReferenceBuilder(DEFAULT_AMOUNT_OBJECT_REFERENCES));
    }

    public AttachmentBuilder(Map<ClassificationType, List<ClassificationWrapper>> classifications,
            ObjectReferenceBuilder objectReferenceBuilder) {
        this(classifications, 0, objectReferenceBuilder);
    }

    public AttachmentBuilder(Map<ClassificationType, List<ClassificationWrapper>> classifications,
            int maxAmountAttachments, ObjectReferenceBuilder objectReferenceBuilder) {
        this.documentClassifications = classifications.get(ClassificationType.DOKUMENTTYP);
        this.objectReferenceBuilder = objectReferenceBuilder;
        this.maxAmountAttachments = maxAmountAttachments;
    }

    /**
     * Generates a set of {@link Attachment}.
     * 
     * @param numberOfAttachments
     *            number of {@link Attachment} to be generated.
     * @return set of {@link Attachment}
     */
    public List<Attachment> getAttachments(int numberOfAttachments) {
        List<Attachment> attachements = new ArrayList<>();

        for (int i = 0; i < numberOfAttachments; i++) {
            if (currentAttachmentAmount < maxAmountAttachments || maxAmountAttachments == 0) {
                attachements.add(generateAttachement());
            }
        }

        return attachements;
    }

    private Attachment generateAttachement() {
        currentAttachmentAmount++;
        AttachmentImpl attachment = new AttachmentWrapper();

        attachment.setObjectReference(objectReferenceBuilder.getObjectReference());
        attachment.setClassificationSummary(pickClassification().asSummary());

        return attachment;
    }

    private ClassificationImpl pickClassification() {
        return documentClassifications.get(currentClassificationCounter++ % documentClassifications.size());
    }
}
