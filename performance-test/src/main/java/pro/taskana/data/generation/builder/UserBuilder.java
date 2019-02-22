package pro.taskana.data.generation.builder;

import java.util.ArrayList;
import java.util.List;

import pro.taskana.impl.UserWrapper;

/**
 * Class wraps the functionality for creating a new user as {@link UserWrapper}.
 * 
 * @author fe
 *
 */
public class UserBuilder {

    private final String domainName;
    private List<UserWrapper> generatedUsers;

    public UserBuilder(String domainName) {
        this.generatedUsers = new ArrayList<>();
        this.domainName = domainName;
    }

    /**
     * Created a new {@link UserWrapper} which encapsulate the user id.
     * 
     * @return new user as {@link UserWrapper}
     */
    public UserWrapper generateNewUser() {
        UserWrapper user = new UserWrapper(domainName);
        generatedUsers.add(user);
        return user;
    }

    /**
     * Supplies all generated {@link UserWrapper}.
     * 
     * @return created {@link UserWrapper}
     */
    public List<UserWrapper> getGeneratedUsers() {
        return generatedUsers;
    }

}
