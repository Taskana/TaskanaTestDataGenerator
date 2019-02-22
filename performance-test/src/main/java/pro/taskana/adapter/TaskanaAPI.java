package pro.taskana.adapter;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.ClassificationService;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.TaskanaEngine;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketService;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.impl.ClassificationWrapper;
import pro.taskana.impl.TaskWrapper;
import pro.taskana.impl.WorkbasketAccessItemWrapper;
import pro.taskana.impl.WorkbasketWrapper;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.persistence.DataSourceHandler;

public class TaskanaAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaAPI.class);
    private static final String DROP_SCHEMA_SQL = "/clear-database.sql";
    protected static final String SCHEMA_NAME = "taskana";

    private TaskanaEngine taskanaEngine;
    private TaskService taskService;
    private ClassificationService classificationService;
    private WorkbasketService workbasketService;

    public TaskanaAPI() throws FileNotFoundException, NoSuchFieldException, SQLException {
        DataSource dataSource = DataSourceHandler.getDataSource();
        clearDatabase(dataSource);

        TaskanaEngineConfiguration taskanaConfiguration = new TaskanaEngineConfiguration(dataSource, false, false, SCHEMA_NAME);

        this.taskanaEngine = TaskanaEngineImpl.createTaskanaEngine(taskanaConfiguration);
        taskanaEngine.setConnectionManagementMode(TaskanaEngine.ConnectionManagementMode.AUTOCOMMIT);
        this.taskService = taskanaEngine.getTaskService();
        this.classificationService = taskanaEngine.getClassificationService();
        this.workbasketService = taskanaEngine.getWorkbasketService();
    }

    private void clearDatabase(DataSource dataSource) throws SQLException {
        ScriptRunner scriptRunnner = new ScriptRunner(dataSource.getConnection());
        InputStream is = this.getClass().getResourceAsStream(DROP_SCHEMA_SQL);
        InputStreamReader isr = new InputStreamReader(is);

        try {
            scriptRunnner.runScript(isr);
        } catch (Exception e) {
            LOGGER.debug("Cannot clear database", e);
        }
    }

    /**
     * Persists all given Tasks via the Taskana API.
     * 
     * @param tasks
     *            The Tasks to persist.
     * @throws InvalidArgumentException
     * @throws TaskAlreadyExistException
     * @throws NotAuthorizedException
     * @throws ClassificationNotFoundException
     * @throws WorkbasketNotFoundException
     */
    public void createTasks(List<TaskWrapper> tasks)
            throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
            TaskAlreadyExistException, InvalidArgumentException {
        for (Task task : tasks) {
            taskService.createTask(task);
        }
    }

    /**
     * Persists all given Classifications via the Taskana API.
     * 
     * @param classifications
     *            The {@link pro.taskana.Classification}s to persist.
     * @throws InvalidArgumentException
     * @throws NotAuthorizedException
     * @throws ClassificationAlreadyExistException
     * @throws DomainNotFoundException
     */
    public void createClassification(List<ClassificationWrapper> classifications)
            throws DomainNotFoundException, ClassificationAlreadyExistException,
            NotAuthorizedException, InvalidArgumentException {
        ClassificationWrapper parent = classifications.remove(0);
        parent.setKey(parent.getCategory());
        String parentId = classificationService.createClassification(parent).getId();
        int i = 0;
        for (ClassificationWrapper classification : classifications) {
            classification.setParentId(parentId);
            classification.setKey(classification.getCategory() + i);
            classificationService.createClassification(classification);
            i++;
        }
    }

    /**
     * Persists all given Workbaskets via the Taskana API.
     * 
     * @param workbaskets
     *            The {@link Workbasket}s to persist.
     * @throws WorkbasketAlreadyExistException
     * @throws NotAuthorizedException
     * @throws InvalidWorkbasketException
     * @throws DomainNotFoundException
     * @throws WorkbasketNotFoundException
     */
    public void createWorkbaskets(List<WorkbasketWrapper> workbaskets) throws DomainNotFoundException,
            InvalidWorkbasketException, NotAuthorizedException, WorkbasketAlreadyExistException {
        for (WorkbasketWrapper workbasket : workbaskets) {
            workbasketService.createWorkbasket(workbasket);
        }
    }

    /**
     * Create distribution targets for existing {@link Workbasket}.
     * 
     * @param workbaskets
     *            Already perisited {@link Workbasket}s.
     * @throws WorkbasketNotFoundException
     * @throws NotAuthorizedException
     */
    public void createDistributionTargets(List<WorkbasketWrapper> workbaskets)
            throws WorkbasketNotFoundException, NotAuthorizedException {
        for (WorkbasketWrapper workbasketWrapper : workbaskets) {
            for (WorkbasketWrapper distributionTarget : workbasketWrapper.getDirectChildren()) {
                workbasketService.addDistributionTarget(workbasketWrapper.getId(), distributionTarget.getId());
            }
        }
    }

    /**
     * Persists all given Workbaskets via the Taskana API.
     * 
     * @param workbasketAccessItems
     *            The {@link WorkbasketAccessItem}s to persist.
     * @throws NotAuthorizedException
     * @throws InvalidArgumentException
     * @throws WorkbasketNotFoundException
     */
    public void createWorkbasketAccesItem(List<WorkbasketAccessItemWrapper> workbasketAccessItems)
            throws WorkbasketNotFoundException, InvalidArgumentException, NotAuthorizedException {
        for (WorkbasketAccessItemWrapper workbasketAccessItem : workbasketAccessItems) {
            workbasketService.createWorkbasketAccessItem(workbasketAccessItem);
        }
    }
}
