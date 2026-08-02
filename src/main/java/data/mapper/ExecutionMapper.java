package data.mapper;

import data.DTO.ExecutionRecord;
import org.bson.Document;

public class ExecutionMapper {
    public static Document toDocument(ExecutionRecord record) {
        Document doc = new Document();
        doc.append("suite", record.getSuiteName())
                .append("class", record.getClassName())
                .append("test", record.getTestName())
                .append("description", record.getDescription())
                .append("epic", record.getEpic())
                .append("feature", record.getFeature())
                .append("story", record.getStory())
                .append("severity", record.getSeverity())
                .append("status", record.getStatus())
                .append("parameters", record.getParameters())
                .append("environment", record.getEnvironment())
                .append("startTime", record.getStartTimeMillis())
                .append("endTime", record.getEndTimeMillis())
                .append("durationMs", record.getDurationMs())
                .append("errorMessage", record.getErrorMessage())
                .append("stackTrace", record.getStackTrace());
        return doc;
    }
}
