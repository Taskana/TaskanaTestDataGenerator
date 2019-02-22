package pro.taskana.data.generation.builder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import pro.taskana.Workbasket;
import pro.taskana.WorkbasketType;
import pro.taskana.data.generation.util.DateHelper;
import pro.taskana.impl.UserWrapper;
import pro.taskana.impl.WorkbasketWrapper;

/**
 * Class wraps the functionality for creating {@link Workbasket}.
 * 
 * @author fe
 *
 */
public class WorkbasketBuilder {

    public static int WORKBASKETS_IN_ORG_LVL_1 = 1;

    private final UserBuilder userBuilder;
    private final String domainName;
    private final DateHelper dateHelper;

    private List<WorkbasketWrapper> generatedWorkbaskets;

    public WorkbasketBuilder(String domainName, UserBuilder userBuilder) {
        this.dateHelper = new DateHelper();
        this.userBuilder = userBuilder;
        this.domainName = domainName;
        generatedWorkbaskets = new ArrayList<>();
        WORKBASKETS_IN_ORG_LVL_1 = 1;
    }

    /**
     * Creates new personal {@link Workbasket}.
     * 
     * @param amount
     *            number of new {@link Workbasket}
     * @return all created {@link Workbasket}
     */
    public List<WorkbasketWrapper> generateWorkbaskets(int amount) {
        List<WorkbasketWrapper> workbaskets = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            WorkbasketWrapper wb = generatePersonalWorkbasketWithOwner();
            workbaskets.add(wb);
        }
        return workbaskets;
    }

    /**
     * Creates new group {@link Workbasket}. The {@link Workbasket} have no owner.
     * 
     * @param amount
     *            number of new {@link Workbasket}
     * @return all created {@link Workbasket}
     */
    public List<WorkbasketWrapper> generateManagingWorkbaskets(int amount) {
        List<WorkbasketWrapper> workbaskets = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            workbaskets.add(generateGroupWorkbasketWithOwner());

        }
        return workbaskets;
    }

    /**
     * Supplies all generated {@link WorkbasketW}.
     * 
     * @return created {@link WorkbasketWrapper}
     */
    public List<WorkbasketWrapper> getGeneratedWorkbaskets() {
        generatedWorkbaskets.forEach(WorkbasketWrapper::initAttributes);
        return generatedWorkbaskets;
    }

    private WorkbasketWrapper generateGroupWorkbasketWithoutOwner() {
        WorkbasketWrapper wb = createWorkbasket(WorkbasketType.GROUP);
        return wb;
    }

    private WorkbasketWrapper generateGroupWorkbasketWithOwner() {
        WorkbasketWrapper wb = generateGroupWorkbasketWithoutOwner();
        wb.setUserAsOwner(userBuilder.generateNewUser());
        return wb;
    }

    private WorkbasketWrapper generatePersonalWorkbasketWithOwner() {
        WorkbasketWrapper wb = createWorkbasket(WorkbasketType.PERSONAL);
        UserWrapper uw = userBuilder.generateNewUser();
        wb.setUserAsOwner(uw);
        return wb;
    }

    private WorkbasketWrapper createWorkbasket(WorkbasketType type) {
        WorkbasketWrapper wb = new WorkbasketWrapper(type, domainName);
        Instant created = dateHelper.getNextTimestampForWorkbasket();
        wb.setCreated(created);
        wb.setModified(created);
        generatedWorkbaskets.add(wb);
        return wb;
    }

}
