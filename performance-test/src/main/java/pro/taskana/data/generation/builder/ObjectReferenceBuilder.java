package pro.taskana.data.generation.builder;

import pro.taskana.ObjectReference;

import java.util.ArrayList;
import java.util.List;


/**
 * Class wraps the functionality for creating {@link ObjectReference}.
 * 
 * @author fe
 *
 */
public class ObjectReferenceBuilder {

    private static final String COMPANY = "PerformanceTest Company";
    private static final String SYSTEM = "PerformanceTest System";
    private static final String INSTANCE = "PerformanceTest Instance";
    private static final String TYPE = "Object Type";

    private List<ObjectReference> builtObjectReferences;
    private int nextObjectRefIndex;

    public ObjectReferenceBuilder(int numberOfAvailableObjectReferences) {
        builtObjectReferences = new ArrayList<>();
        for (int i = 0; i < numberOfAvailableObjectReferences; i++) {
            builtObjectReferences.add(createObjectReference(COMPANY, SYSTEM, INSTANCE, TYPE, String.valueOf(i)));
        }
        nextObjectRefIndex = 0;
    }

    public ObjectReference getObjectReference() {
        return getNextObjectReference();
    }

    /**
     * Supplies a set of {@link ObjectReference}.
     * 
     * @param numberOfObjectReferences
     *            number of {@link ObjectReference} to be returned.
     * @return set of {@link ObjectReference}.
     */
    public List<ObjectReference> getObjectReferences(int numberOfObjectReferences) {
        List<ObjectReference> references = new ArrayList<>();
        for (int i = 0; i < numberOfObjectReferences; i++) {
            references.add(getNextObjectReference());
        }
        return references;
    }

    private ObjectReference createObjectReference(String company, String system, String systemInstance, String type,
            String value) {
        ObjectReference objectReference = new ObjectReference();
        objectReference.setCompany(company);
        objectReference.setSystem(system);
        objectReference.setSystemInstance(systemInstance);
        objectReference.setType(type);
        objectReference.setValue(value);

        return objectReference;
    }

    private ObjectReference getNextObjectReference() {
        ObjectReference nextOR = builtObjectReferences.get(nextObjectRefIndex);
        nextObjectRefIndex++;
        if (nextObjectRefIndex >= builtObjectReferences.size()) {
            nextObjectRefIndex = nextObjectRefIndex % builtObjectReferences.size();
        }
        return nextOR;
    }
}
