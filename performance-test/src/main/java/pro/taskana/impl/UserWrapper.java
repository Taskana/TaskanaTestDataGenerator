package pro.taskana.impl;

import pro.taskana.Workbasket;
import pro.taskana.WorkbasketType;

/**
 * Class wraps the informations to generate a user.
 *
 *
 * @author fe
 *
 */
public class UserWrapper {

    public static final int NUMBER_LENGTH_IN_ID = 2;
    
    private String orglvl;
    private String domain;
    private int highestWorkbasketLevel;
    private String generatedId;

    public UserWrapper(String id, boolean useStaticId) {
        this.generatedId = id;
    }

    public UserWrapper(String domain) {
        this.domain = domain;
        this.generatedId = null;
        this.orglvl = null;
    }

    /**
     * Called by {@link WorkbasketWrapper} when the owner is set. The
     * {@link Workbasket} informs his user about its {@link WorkbasketType} and
     * level.
     *
     * @param level
     *            of the {@link Workbasket} owned by the this user
     */
    public void newOwnerOfWorkbasket(int level) {
        if (highestWorkbasketLevel < level) {
            highestWorkbasketLevel = level;
        }
    }

    /**
     * Updates the organisation level of this user.
     *
     * @param orgLvl organisation level
     * @param ownedWbType {@link WorkbasketType}
     */
    public void setOrgLvl(String orgLvl, WorkbasketType ownedWbType) {
        if (this.orglvl != null) {
            int lvl = this.orglvl.length();
            int newLvl = orglvl.length();
            if (newLvl < lvl) {
                this.orglvl = orgLvl;
            } 
        } else {
            this.orglvl = orgLvl;
        }
    }

    /**
     * Generated the id of this user with pattern: domain + role + organisation
     * level of highest owned {@link Workbasket}
     * 
     * E.g. user owns only one {@link Workbasket} with key CWB0101010205 then the
     * user id is CU0101010205. If the user owns more than one {@link Workbasket}
     * like CWB010102, CWB01020305 and CWB0102 then the user id is CU0102 because of
     * CWB0102 is the {@link Workbasket} with the highest organisation level.
     *
     * @return id
     */
    public String getId() {
        if (generatedId == null || generatedId.isEmpty()) {
            StringBuilder sb = new StringBuilder(domain);
            sb.append("U");
            if (orglvl == null || orglvl.isEmpty()) {
                throw new IllegalStateException("Orglevel should be set!");
            }
            sb.append(orglvl);
            generatedId = sb.toString();
        }
        return generatedId;
    }
    
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder("user=");
    	sb.append(generatedId);
    	return sb.toString();
    }

}
