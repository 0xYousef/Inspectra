package data.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ExecutionRecord {
    private String suiteName;
    private String className;
    private String type;
    private String testName;
    private String description;
    private String epic;
    private String feature;
    private String story;
    private String severity;
    private String status;
    private String parameters;
    private String environment;
    private long startTimeMillis;
    private long endTimeMillis;
    private long durationMs;
    private String errorMessage;
    private String stackTrace;
}
