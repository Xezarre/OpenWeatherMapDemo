package utility.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

import static common.GlobalVariables.ENVIRONMENT;

@Getter
@AllArgsConstructor
public enum Database {

    SAMPLE(DatabaseType.POSTGRESQL, "db-name-prefix", "5432", "db-name", "-suffix", "ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory");

    private final DatabaseType type;
    private final String domainNamePrefix;
    private final String postNumber;
    private final String dataBaseName;
    private final String productionNameSuffix;
    private final String properties;

    public String getURL() {
        String dbServer = this.getDBServer();
        return String.format("jdbc:%s://%s:%s/%s?%s",
                DatabaseType.POSTGRESQL.sqlServer, dbServer, postNumber, dataBaseName, properties);
    }

    private String getDBServer() {
        boolean isProductionType = Environments.fromName(ENVIRONMENT) == Environments.PRODUCTION;
        String productionNameSuffix = isProductionType ? this.productionNameSuffix : "";
        String env = isProductionType ? ENVIRONMENT.concat("-dr") : ENVIRONMENT;

        return String.format("%s%s.%s.testing.com",
                domainNamePrefix, productionNameSuffix, env);
    }

    public static Database fromURL(String url) {
        return Arrays.stream(Database.values())
                .filter(x -> url.contains(x.domainNamePrefix))
                .findFirst()
                .orElse(null);
    }
}

