package utility.enums;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Environments {

    PRODUCTION("production"),
    QA("qa"),
    INVALID("invalid env");

    private final String name;

    public static Environments fromName(String name) {
        return Arrays.stream(Environments.values())
                .filter(x -> x.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(INVALID);
    }

    public boolean startWithQA() {
        return startWith("qa");
    }

    public boolean startWithStage() {
        return startWith("stage");
    }

    public boolean startWithStageOrQA() {
        return startWithQA() || startWithStage();
    }

    private boolean startWith(String str) {
        return this.name.toLowerCase().startsWith(str);
    }
}
