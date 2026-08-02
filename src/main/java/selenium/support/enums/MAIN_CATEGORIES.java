package selenium.support.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MAIN_CATEGORIES {
    WOMEN("Women"), MEN("Men"), KIDS("Kids");
    private final String mainCategory;

    @Override
    public String toString() {
        return mainCategory;
    }
}
