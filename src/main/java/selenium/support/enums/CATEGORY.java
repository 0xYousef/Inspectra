package selenium.support.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CATEGORY {

    WOMEN_DRESS(MAIN_CATEGORIES.WOMEN,"Dress"),
    WOMEN_TOPS(MAIN_CATEGORIES.WOMEN,"Tops"),
    WOMEN_SAREE(MAIN_CATEGORIES.WOMEN,"Saree"),
    MEN_TSHIRTS(MAIN_CATEGORIES.MEN,"Tshirts"),
    MEN_JEANS(MAIN_CATEGORIES.MEN,"Jeans"),
    KIDS_DRESS(MAIN_CATEGORIES.KIDS,"Dress"),
    KIDS_TOPS_SHIRTS(MAIN_CATEGORIES.KIDS,"Tops & Shirts");

    private final MAIN_CATEGORIES mainCategory;
    private final String text;

    @Override
    public String toString() {
        return text;
    }
}
