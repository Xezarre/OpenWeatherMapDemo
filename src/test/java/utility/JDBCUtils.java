package utility;

import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class JDBCUtils {

    @SneakyThrows
    public static List<HashMap<String, Object>> getResultSetAsList(final ResultSet rs) {
        List<HashMap<String, Object>> results = new ArrayList<>();
        if (isResultSetWithoutData(rs)) return results;

        ResultSetMetaData md = rs.getMetaData();
        int numCol = md.getColumnCount();
        while (rs.next()) {
            HashMap<String, Object> row = getRowDataAsMap(rs, md, numCol);
            results.add(row);
        }
        return results;
    }

    public static List<HashMap<String, String>> getResultSetAsListOfMapString(final ResultSet rs) {
        return getResultSetAsListOfMapString(rs, Integer.MAX_VALUE);
    }

    public static List<HashMap<String, Object>> getResultSetWithoutFieldNull(final ResultSet rs, String field) {
        return getResultSetAsList(rs).stream()
                .filter(x -> x.containsKey(field) && x.get(field) != null)
                .collect(Collectors.toList());
    }

    public static HashMap<String, String> getFirstDataFromResultSet(final ResultSet rs) {
        return getResultSetAsListOfMapString(rs, 1).get(0);
    }

    public static HashMap<String, String> getFirstDataWithExpectedValueFromResultSet(final ResultSet rs,
                                                                                     String field,
                                                                                     String value) {
        return getResultSetAsListOfMapString(rs).stream()
                .filter(x -> hasDataWithExpectedValue(x, field, value))
                .findFirst()
                .orElse(new HashMap<>());
    }

    public static HashMap<String, String> getFirstDataWithValueNotNullFromResultSet(final ResultSet rs, String field) {
        return getResultSetAsListOfMapString(rs).stream()
                .filter(x -> hasDataWithoutValueNull(x, field))
                .findFirst()
                .orElse(new HashMap<>());
    }

    @SneakyThrows
    private static boolean isResultSetWithoutData(final ResultSet rs) {
        return rs == null || !rs.isBeforeFirst();
    }

    @SneakyThrows
    private static HashMap<String, Object> getRowDataAsMap(final ResultSet rs, ResultSetMetaData md, int numCol) {
        HashMap<String, Object> rowData = new HashMap<>();
        for (int i = 1; i <= numCol; i++) {
            rowData.put(md.getColumnLabel(i), rs.getObject(i));
        }
        return rowData;
    }

    @SneakyThrows
    private static HashMap<String, String> getRowDataAsMapString(final ResultSet rs, ResultSetMetaData md, int numCol) {
        HashMap<String, String> rowData = new HashMap<>();
        for (int i = 1; i <= numCol; i++) {
            rowData.put(md.getColumnLabel(i), rs.getString(i));
        }
        return rowData;
    }

    @SneakyThrows
    private static List<HashMap<String, String>> getResultSetAsListOfMapString(final ResultSet rs, int limit) {
        List<HashMap<String, String>> results = new ArrayList<>();
        if (isResultSetWithoutData(rs)) return results;

        ResultSetMetaData md = rs.getMetaData();
        int numCol = md.getColumnCount();
        while (rs.next() && results.size() < limit) {
            HashMap<String, String> row = getRowDataAsMapString(rs, md, numCol);
            results.add(row);
        }
        return results;
    }

    private static boolean hasDataWithExpectedValue(HashMap<String, String> record, String field, String value) {
        return record.containsKey(field)
                && record.get(field).equalsIgnoreCase(value);
    }

    private static boolean hasDataWithoutValueNull(HashMap<String, String> record, String field) {
        return record.containsKey(field)
                && record.get(field) != null;
    }
}
