package core.base;
import org.openqa.selenium.Dimension;

public enum DEVICES {
    IPHONE(390, 844),
    IPAD(768, 1024),
    ANDROID(480, 800),
    DESKTOP(1280, 1024),
    MAXSIZE(1920, 1080),
    MINIMIZE(320, 480);

    private final int width;
    private final int height;

    DEVICES(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Dimension size() {
        return new Dimension(width, height);
    }
}