package pro.taskana.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.pooled.PooledDataSource;

/**
 * This class is used to configure a DB2 database connection. 
 * 
 * @author EL
 */
public class DataSourceHandler {

    private static final String JDBC_DRIVER = "jdbcDriver";
    private static final String JDBC_URL = "jdbcUrl";
    private static final String DB_USER_NAME = "dbUserName";
    private static final String DB_PASSWORD = "dbPassword";
    
    private static final String[] PROPERTIES_FOR_DS = new String[]{JDBC_DRIVER, JDBC_URL, DB_USER_NAME, DB_PASSWORD};

    private static final String PROPERTIES_FILENAME = "taskanaPerformanceTest.properties";
    private static final String HOME_DIRECTORY = "user.home";

    private static DataSource dataSource;

    /**
     * Returns a {@link DataSource} object with given properties.
     * 
     * @return The {@link DataSource} object.
     * @throws FileNotFoundException If there is no properties file available.
     * @throws NoSuchFieldException If the properties file is available but there are some properties missing. 
     */
    public static DataSource getDataSource() throws FileNotFoundException, NoSuchFieldException {
        if (dataSource == null) {
            String userHomeDirectroy = System.getProperty(HOME_DIRECTORY);
            String propertiesFileName = userHomeDirectroy + "/" + PROPERTIES_FILENAME;
            File file = new File(propertiesFileName);
            if (file.exists() && !file.isDirectory()) {
                dataSource = createDataSourceFromProperties(propertiesFileName);
            } else {
                throw new FileNotFoundException("File " + PROPERTIES_FILENAME + " is required but doesnt exist");
            }
        }
        return dataSource;
    }
    
    private static DataSource createDataSourceFromProperties(String propertiesFileName) throws NoSuchFieldException {
        DataSource currentDatasource = null;
        try (InputStream input = new FileInputStream(propertiesFileName)) {
            Properties properties = new Properties();
            properties.load(input);
            boolean propertiesFileIsComplete = true;
            String warningMessage = "";

            for (String property : PROPERTIES_FOR_DS) {
                String propertyValue = properties.getProperty(property);
                if (propertyValue == null || propertyValue.isEmpty()) {
                    warningMessage += ", " + property + " property missing";
                    propertiesFileIsComplete = false;
                }
            }

            if (propertiesFileIsComplete) {
                ClassLoader driverClassLoader = Thread.currentThread().getContextClassLoader();
                currentDatasource = new PooledDataSource(driverClassLoader, 
                        properties.getProperty(JDBC_DRIVER), 
                        properties.getProperty(JDBC_URL),
                        properties.getProperty(DB_USER_NAME), 
                        properties.getProperty(DB_PASSWORD)
                );
                ((PooledDataSource) currentDatasource).forceCloseAll(); 
            } else {
                throw new NoSuchFieldException("propertiesFile " + propertiesFileName + " is incomplete" + warningMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException("createDataSourceFromProperties caught Exception " + e);
        }
        return currentDatasource;
    }
}