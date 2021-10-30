package utility.database;

public class QueryDatabase extends Database {

    // region Queries for table A

    public static final String GET_SOMETHING = "SELECT name FROM table WHERE id = '%s'";
    public static final String UPDATE_SOMETHING = "UPDATE table  SET field = '%s ' WHERE id = '%s'";
    public static final String DELETE_SOMETHING = "DELETE from tableName WHERE id = '%s'";

    // endregion


}
