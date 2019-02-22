package pro.taskana.impl;

import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.impl.WorkbasketAccessItemImpl;

/**
 * Class wraps the {@link WorkbasketAccessItem}.
 * 
 * @author fe
 *
 */
public class WorkbasketAccessItemWrapper extends WorkbasketAccessItemImpl {

    private static int ACCESS_ITEM_ID = 0;

    private final int uniqueNumber;
    private UserWrapper proficientUser;
    private WorkbasketWrapper wbWrapper;

    public WorkbasketAccessItemWrapper() {
        this.uniqueNumber = ACCESS_ITEM_ID;
        ACCESS_ITEM_ID++;
    }

    /**
     * Sets the user which provides the used access id.
     * 
     * @param user
     *            user id as {@link UserWrapper}
     */
    public void grantAccessToUser(UserWrapper user) {
        this.proficientUser = user;
    }

    /**
     * Sets the workbasket as {@link WorkbasketWrapper} which this access item
     * affects.
     * 
     * @param wbWrapper
     *            {@link WorkbasketWrapper}
     */
    public void setWorkbasketWrapper(WorkbasketWrapper wbWrapper) {
        this.wbWrapper = wbWrapper;
    }

    /**
     * Sets the access-id, {@link Workbasket}-id and returns the
     * {@link WorkbasketAccessItem} instance.
     * 
     * @return {@link WorkbasketAccessItemWrapper}
     */
    public void initAttributes() {
        setAccessId(proficientUser.getId());
        setWorkbasketId(wbWrapper.getId());
        buildAndSetId();
    }

    private void buildAndSetId() {
        if (this.getId() == null || this.getId().isEmpty()) {
            setId(Integer.toString(uniqueNumber));
        }
    }

}
