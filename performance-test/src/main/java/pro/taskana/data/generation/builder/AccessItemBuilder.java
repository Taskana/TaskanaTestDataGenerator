package pro.taskana.data.generation.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.data.enums.AccessType;
import pro.taskana.impl.UserWrapper;
import pro.taskana.impl.WorkbasketAccessItemWrapper;
import pro.taskana.impl.WorkbasketWrapper;

/**
 * Class wraps the functionality for creating {@link WorkbasketAccessItem}.
 * 
 * @author fe
 *
 */
public class AccessItemBuilder {

    private static Map<AccessType, BiConsumer<WorkbasketAccessItemWrapper, Boolean>> accessMapper;

    private List<UserWrapper> currentUsers;
    private List<WorkbasketWrapper> accessibleWorkbaskets;
    private List<WorkbasketAccessItemWrapper> generatedAccessItems;
    private Map<AccessType, Boolean> currentAccessTypes;

    static {
        accessMapper = new HashMap<>();
        accessMapper.put(AccessType.APPEND, (accessItem, access) -> accessItem.setPermAppend(access));
        accessMapper.put(AccessType.READ, (accessItem, access) -> accessItem.setPermRead(access));
        accessMapper.put(AccessType.DISTRIBUTE, (accessItem, access) -> accessItem.setPermDistribute(access));
        accessMapper.put(AccessType.OPEN, (accessItem, access) -> accessItem.setPermOpen(access));
        accessMapper.put(AccessType.TRANSFER, (accessItem, access) -> accessItem.setPermTransfer(access));
    }

    public AccessItemBuilder() {
        currentAccessTypes = new HashMap<>();
        for (AccessType accessType : AccessType.values()) {
            currentAccessTypes.put(accessType, false);
        }
        accessibleWorkbaskets = new ArrayList<>();
        generatedAccessItems = new ArrayList<>();
    }

    /**
     * Sets the {@link UserWrapper} who should get the permissions.
     * 
     * @param user
     *            user id as {@link UserWrapper}
     * @return configured {@link AccessItemBuilder} instance
     */
    public AccessItemBuilder forUser(UserWrapper user) {
        return forUsers(Arrays.asList(user));
    }

    /**
     * Sets the {@link UserWrapper} who should get the permissions.
     * 
     * @param users
     *            user ids as {@link UserWrapper}
     * @return configured {@link AccessItemBuilder} instance
     */
    public AccessItemBuilder forUsers(List<UserWrapper> users) {
        this.currentUsers = users;
        this.accessibleWorkbaskets = new ArrayList<>();
        return this;
    }

    /**
     * Sets {@link Workbasket} on which the user has access.
     * 
     * @param accessibleWorkbaksets
     *            {@link Workbasket} the user can access to
     * @return configured {@link AccessItemBuilder} instance
     */
    public AccessItemBuilder to(List<WorkbasketWrapper> accessibleWorkbaksets) {
        this.accessibleWorkbaskets.clear();
        this.accessibleWorkbaskets.addAll(accessibleWorkbaksets);
        return this;
    }

    /**
     * Sets {@link Workbasket} on which the user has access. The user also receives
     * the access right on all {@link Workbasket} that lie below the specified
     * {@link Workbasket} in the hierarchy (whereby the distribute relation connects
     * the {@link Workbasket} in the tree).
     * 
     * @param accessibleWorkbaksets
     *            {@link Workbasket}
     * @return configured {@link AccessItemBuilder} instance
     */
    public AccessItemBuilder transitiveTo(List<WorkbasketWrapper> accessibleWorkbaksets) {
        to(accessibleWorkbaksets);
        accessibleWorkbaksets.forEach(aWb -> this.accessibleWorkbaskets.addAll(aWb.getDirectOrIndirectChildren()));
        return this;
    }

    /**
     * Sets {@link Workbasket} on which the user has access.
     * 
     * @param accessibleWorkbakset
     *            {@link Workbasket} the user has access
     * @return configured {@link AccessItemBuilder} instance
     */
    public AccessItemBuilder to(WorkbasketWrapper accessibleWorkbakset) {
        return to(Arrays.asList(accessibleWorkbakset));
    }

    /**
     * Sets the {@link AccessType} which the {@link UserWrapper} has on the defined
     * {@link Workbasket}.
     * 
     * @param grantedAccessTypes
     *            all {@link AccessType} of the user
     * @return configured {@link AccessItemBuilder} instance
     */
    public AccessItemBuilder hasAccess(AccessType... grantedAccessTypes) {
        for (AccessType accessType : grantedAccessTypes) {
            currentAccessTypes.put(accessType, true);
        }
        return this;
    }

    /**
     * Generate the {@link WorkbasketAccessItem}.
     */
    public void build() {
        for (UserWrapper user : currentUsers) {
            generatedAccessItems.addAll(generateAccessItems(user, accessibleWorkbaskets));
        }
    }

    /**
     * Supplies all generated {@link WorkbasketAccessItemt}.
     * 
     * @return created {@link WorkbasketAccessItem}
     */
    public List<WorkbasketAccessItemWrapper> getGeneratedAccessItems() {
        generatedAccessItems.forEach(WorkbasketAccessItemWrapper::initAttributes);
        return generatedAccessItems;
    }

    private List<WorkbasketAccessItemWrapper> generateAccessItems(UserWrapper user, List<WorkbasketWrapper> accessed) {
        List<WorkbasketAccessItemWrapper> accessItems = new ArrayList<>();
        for (WorkbasketWrapper accessedWorkbasket : accessed) {
            accessItems.add(generateAccessItem(user, accessedWorkbasket));
        }
        return accessItems;
    }

    private WorkbasketAccessItemWrapper generateAccessItem(UserWrapper user, WorkbasketWrapper accessed) {
        WorkbasketAccessItemWrapper wbAI = new WorkbasketAccessItemWrapper();
        wbAI.setWorkbasketWrapper(accessed);
        wbAI.grantAccessToUser(user);
        setAccess(wbAI);
        wbAI.setPermAppend(true);
        wbAI.setPermRead(true);
        return wbAI;
    }

    private void setAccess(WorkbasketAccessItemWrapper wbAi) {
        for (AccessType accessType : accessMapper.keySet()) {
            if (currentAccessTypes.containsKey(accessType)) {
                accessMapper.get(accessType).accept(wbAi, currentAccessTypes.get(accessType));
            }
        }
    }
}
