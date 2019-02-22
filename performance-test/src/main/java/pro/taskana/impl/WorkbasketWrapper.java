package pro.taskana.impl;

import java.util.ArrayList;
import java.util.List;

import pro.taskana.Task;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketType;
import pro.taskana.data.generation.builder.WorkbasketBuilder;
import pro.taskana.data.generation.util.Formatter;
import pro.taskana.impl.WorkbasketImpl;

/**
 * Class wraps the {@link WorkbasketImpl} to generate the key according to the
 * following pattern: Domain + WB + parent organisation layers + member id
 *
 * E.g. root {@link Workbasket} in domain "C" has the key CWB01. The
 * distribution targets of CWB01 has the keys CWB0101, CWB0102, CWB0103 and so
 * on. The distirbution targets of CWB0101 has the keys CWB010101, CWB010102,
 * ...
 * 
 * @author fe
 *
 */
public class WorkbasketWrapper extends WorkbasketImpl {

    public static final int NUMBER_LENGTH_IN_ID = 2;

    private static final int ID_LENGTH = 40;
    private static final String WORKBASKET_ID_PREFIX = "WB";
    private static final int INITIAL_MEMBER_ID = 1;

    private Integer memberId;
    private int layer;
    private String formattedOrgLevel;

    private WorkbasketWrapper parent;
    private List<WorkbasketWrapper> directOrIndirectChildren;
    private List<WorkbasketWrapper> directChildren;
    private UserWrapper userWrapper;
    private List<Task> tasks;

    public WorkbasketWrapper(WorkbasketType type, String domain) {
        this.tasks = new ArrayList<>();
        this.setType(type);
        this.setDomain(domain);
        this.layer = 0;
        directOrIndirectChildren = new ArrayList<>();
        directChildren = new ArrayList<>();
        parent = null;
        formattedOrgLevel = null;
        memberId = null;
    }

    /**
     * Sets the layer of this {@link Workbasket}.
     *
     * @param layer
     *            index
     */
    public void setLayer(int layer) {
        this.layer = layer;
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Set the id and return an instance of {@link Workbasket}.
     *
     * @return this as {@link Workbasket}
     */
    public void initAttributes() {
        if (getId() == null || getId().isEmpty()) {
            calculateOrgLvl();
            if (!directChildren.isEmpty()) {
                directChildren.forEach(WorkbasketWrapper::initAttributes);
            }
            setOwner(userWrapper.getId());
            generateAndSetId();
            setName(getId());
            setDescription(getId());
        }
    }

    /**
     * Supplies a list of {@link Workbasket} which represents the direct children of
     * this {@link Workbasket} in the tree. Direct children are also the direct
     * distribution targets of this {@link Workbasket}.
     *
     * @return list of {@link Workbasket}
     */
    public List<WorkbasketWrapper> getDirectChildren() {
        return directChildren;
    }

    /**
     * Provides information about the layer of this {@link Workbasket}
     *
     * @return layer index of this {@link Workbasket}
     */
    public int getLayer() {
        return layer;
    }

    /**
     * Provides all direct or indirect children. Direct children are the
     * distribution targets. Indirect children are the direct children of the
     * distribution targets.
     *
     * @return direct or indirect connected {@link Workbasket} of a lower level
     */
    public List<WorkbasketWrapper> getDirectOrIndirectChildren() {
        return directOrIndirectChildren;
    }

    /**
     * Sets the parent of this {@link Workbasket}.
     *
     * @param parent
     *            {@link Workbasket}
     */
    public void setParent(WorkbasketWrapper parent) {
        this.parent = parent;
    }

    /**
     * Sets the distribution targets of this {@link Workbasket}. The distribution
     * target will be set as direct children of this {@link Workbasket}.
     *
     * @param distributionTargets
     *            distribution targets
     */
    public void addDistributionTargets(List<WorkbasketWrapper> distributionTargets) {
        this.directChildren.addAll(distributionTargets);
        distributionTargets.forEach(dt -> dt.setParent(this));

        this.directOrIndirectChildren.addAll(distributionTargets);
        distributionTargets.forEach(child -> this.directOrIndirectChildren.addAll(child.getDirectOrIndirectChildren()));
    }

    /**
     * Set owner as {@link UserWrapper}.
     *
     * @param user
     *            {@link UserWrapper} providing the user id
     */
    public void setUserAsOwner(UserWrapper user) {
        this.userWrapper = user;
        this.userWrapper.newOwnerOfWorkbasket(layer);
    }

    /**
     * Returns the owner as {@link UserWrapper}.
     *
     * @return owner as {@link UserWrapper}
     */
    public UserWrapper getOwnerAsUser() {
        return userWrapper;
    }

    /**
     * Returns the organisation level of this {@link Workbasket}.
     *
     * @return organisation level.
     */
    public String getOrgLvl() {
        if ((formattedOrgLevel == null || formattedOrgLevel.isEmpty())) {
            calculateOrgLvl();
        }
        return formattedOrgLevel;
    }

    /**
     * Returns the member id of this {@link Workbasket}. Member id is the index of
     * this {@link Workbasket} within the list of the parents direct children.
     * 
     * @return id
     */
    public int getMemberId() {
        if (parent == null) {
            if (memberId == null) {
                memberId = INITIAL_MEMBER_ID;
            }
        } else if (memberId == null) {
            memberId = parent.getDirectChildren().indexOf(this) + INITIAL_MEMBER_ID;
        }
        return memberId;
    }

    private void calculateOrgLvl() {
        if (getOrgLevel1() == null || getOrgLevel1().isEmpty()) {
            if (parent == null) {
                int orgLevelValue = WorkbasketBuilder.WORKBASKETS_IN_ORG_LVL_1++;
                formattedOrgLevel = Formatter.format(orgLevelValue, NUMBER_LENGTH_IN_ID);
                setOrgLevel1(formattedOrgLevel);
            } else {
                String parentOrgLvl = parent.getOrgLvl();
                String formattedMemberId = Formatter.format(getMemberId(), NUMBER_LENGTH_IN_ID);
                formattedOrgLevel = parentOrgLvl + formattedMemberId;
                copyOrgLvlFromParent();
                writeCurrentOrgLevelValue(formattedMemberId);
            }
            userWrapper.setOrgLvl(formattedOrgLevel, getType());
        }
    }

    private void copyOrgLvlFromParent() {
        setOrgLevel1(parent.getOrgLevel1());
        setOrgLevel2(parent.getOrgLevel2());
        setOrgLevel3(parent.getOrgLevel3());
        setOrgLevel4(parent.getOrgLevel4());
    }

    private void writeCurrentOrgLevelValue(String orgLvlValue) {
        if (getOrgLevel2() == null || getOrgLevel2().isEmpty()) {
            setOrgLevel2(orgLvlValue);
        } else if (getOrgLevel3() == null || getOrgLevel3().isEmpty()) {
            setOrgLevel3(orgLvlValue);
        } else if (getOrgLevel4() == null || getOrgLevel4().isEmpty()) {
            setOrgLevel4(orgLvlValue);
        }
    }

    private void generateAndSetId() {
        if (getId() == null || getId().isEmpty()) {
            StringBuilder sb = new StringBuilder(getDomain());
            sb.append(WORKBASKET_ID_PREFIX);
            sb.append(formattedOrgLevel);
            String id = sb.toString();
            setKey(id);
            setId(Formatter.fitToExpectedLength(id, ID_LENGTH));
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((directChildren == null) ? 0 : directChildren.hashCode());
        result = prime * result + ((directOrIndirectChildren == null) ? 0 : directOrIndirectChildren.hashCode());
        result = prime * result + ((formattedOrgLevel == null) ? 0 : formattedOrgLevel.hashCode());
        result = prime * result + layer;
        result = prime * result + ((memberId == null) ? 0 : memberId.hashCode());
        result = prime * result + ((parent == null) ? 0 : parent.hashCode());
        result = prime * result + ((userWrapper == null) ? 0 : userWrapper.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        WorkbasketWrapper other = (WorkbasketWrapper) obj;
        if (formattedOrgLevel == null) {
            if (other.formattedOrgLevel != null)
                return false;
        } else if (!formattedOrgLevel.equals(other.formattedOrgLevel))
            return false;
        if (layer != other.layer)
            return false;
        if (memberId == null) {
            if (other.memberId != null)
                return false;
        } else if (!memberId.equals(other.memberId))
            return false;
        if (parent == null) {
            if (other.parent != null)
                return false;
        } else if (!parent.equals(other.parent))
            return false;
        if (userWrapper == null) {
            if (other.userWrapper != null)
                return false;
        } else if (!userWrapper.equals(other.userWrapper))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        sb.append("id=");
        sb.append(getId());
        sb.append(", ");
        sb.append(userWrapper.toString());
        sb.append("]");
        return sb.toString();
    }

}
