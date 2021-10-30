package utility.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DatabaseType {
    POSTGRESQL("postgresql", "org.postgresql.Driver");

    String sqlServer;
    String jdbcDriver;
}
