package core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import data.DTO.ExecutionRecord;
import data.exceptions.ExceptionHandler;
import data.mongo.ExecutionRepository;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import io.qameta.allure.testng.AllureTestNg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestNGListener;
import org.testng.ITestResult;
import org.testng.TestNG;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TestListener implements ISuiteListener, ITestListener {
    private static final Logger log = LoggerFactory.getLogger(TestListener.class);
    private static final boolean RECORDING_ENABLED = Boolean.parseBoolean(
            Configuration.get("execution.recording.enabled") == null
                    ? "true"
                    : Configuration.get("execution.recording.enabled"));
    private static final ObjectMapper mapper = new ObjectMapper();

    private final List<ExecutionRecord> executions =
            Collections.synchronizedList(new ArrayList<>());

    static {
        TestNG testng = new TestNG();
        testng.addListener((ITestNGListener) new AllureTestNg());
    }
    @Override
    public void onStart(ISuite suite) {
        log.info("Initializing Allure environment");
        AllureUtils.cleanAllureResultsDirectory();
        AllureUtils.createAllureResultsDirectory();
    }

    @Override
    public void onTestStart(ITestResult result) {
        AllureParams.report(result.getParameters(),
                result.getMethod().getConstructorOrMethod().getMethod());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        collectExecution(result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        collectExecution(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        collectExecution(result);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        collectExecution(result);
    }

    @Override
    public void onFinish(ISuite suite) {
        log.info("Finalizing Allure reporting");
        AllureUtils.logAllureResultsStatus();
        AllureUtils.generateAndOpenAllureReport();
        flushExecutions();
    }

    private void collectExecution(ITestResult result) {
        try {
            if (!RECORDING_ENABLED || result == null || result.getMethod() == null
                    || !result.getMethod().isTest()) {
                return;
            }

            Method method = result.getMethod().getConstructorOrMethod().getMethod();
            Class<?> testClass = result.getTestClass() == null
                    ? null
                    : result.getTestClass().getRealClass();
            long start = result.getStartMillis();
            long end = result.getEndMillis();

            ExecutionRecord record = ExecutionRecord.builder()
                    .suiteName(result.getTestContext() == null
                            || result.getTestContext().getSuite() == null
                            ? ""
                            : result.getTestContext().getSuite().getName())
                    .className(result.getTestClass() == null ? "" : result.getTestClass().getName())
                    .type(type(result))
                    .testName(result.getName())
                    .description(description(method, testClass))
                    .epic(annotationValue(method, testClass, Epic.class, a -> ((Epic) a).value()))
                    .feature(annotationValue(method, testClass, Feature.class, a -> ((Feature) a).value()))
                    .story(annotationValue(method, testClass, Story.class, a -> ((Story) a).value()))
                    .severity(annotationValue(method, testClass, Severity.class, a -> ((Severity) a).value().name()))
                    .status(status(result.getStatus()))
                    .parameters(parametersJson(result.getParameters()))
                    .environment(environmentJson())
                    .startTimeMillis(start)
                    .endTimeMillis(end)
                    .durationMs(Math.max(0, end - start))
                    .errorMessage(errorMessage(result.getThrowable()))
                    .stackTrace(stackTrace(result.getThrowable()))
                    .build();

            executions.add(record);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSilently(e, "collecting execution of " + result.getName());
        }
    }

    private void flushExecutions() {
        if (!RECORDING_ENABLED || executions.isEmpty()) {
            return;
        }

        List<ExecutionRecord> records = new ArrayList<>(executions);
        executions.clear();
        new ExecutionRepository().insertExecutions(records);
    }

    private static String type(ITestResult result) {
        String className = result.getTestClass() == null
                ? ""
                : result.getTestClass().getRealClass().getName();
        if (className.startsWith("api.")) {
            return "api";
        }
        if (className.startsWith("selenium.")) {
            return "ui";
        }
        String suiteName = result.getTestContext() == null
                || result.getTestContext().getSuite() == null
                ? ""
                : result.getTestContext().getSuite().getName();
        String upper = suiteName.toUpperCase();
        if (upper.contains("UI")) {
            return "ui";
        }
        if (upper.contains("API")) {
            return "api";
        }
        return "unknown";
    }

    private static String status(int status) {
        switch (status) {
            case ITestResult.SUCCESS:
                return "PASS";
            case ITestResult.FAILURE:
                return "FAIL";
            case ITestResult.SKIP:
                return "SKIP";
            case ITestResult.SUCCESS_PERCENTAGE_FAILURE:
                return "SUCCESS_PERCENTAGE_FAILURE";
            default:
                return String.valueOf(status);
        }
    }

    private static String annotationValue(Method method, Class<?> testClass,
                                          Class<? extends Annotation> type,
                                          Function<Annotation, String> extractor) {
        Annotation annotation = method == null ? null : method.getAnnotation(type);
        if (annotation == null && testClass != null) {
            annotation = testClass.getAnnotation(type);
        }
        return annotation == null ? null : extractor.apply(annotation);
    }

    private static String description(Method method, Class<?> testClass) {
        String description = annotationValue(method, testClass, jdk.jfr.Description.class,
                a -> ((jdk.jfr.Description) a).value());
        if (description == null) {
            description = annotationValue(method, testClass, io.qameta.allure.Description.class,
                    a -> ((io.qameta.allure.Description) a).value());
        }
        return description;
    }

    private static String parametersJson(Object[] parameters) {
        try {
            return mapper.writeValueAsString(parameters == null ? new Object[0] : parameters);
        } catch (Exception e) {
            return parameters == null ? "" : java.util.Arrays.toString(parameters);
        }
    }

    private static String environmentJson() {
        try {
            Map<String, String> environment = new LinkedHashMap<>();
            environment.put("api.base.url", Configuration.get("api.base.url"));
            environment.put("ui.base.url", Configuration.get("ui.base.url"));
            return mapper.writeValueAsString(environment);
        } catch (Exception e) {
            return Configuration.get("api.base.url");
        }
    }

    private static String errorMessage(Throwable throwable) {
        return throwable == null ? null : throwable.toString();
    }

    private static String stackTrace(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        String[] lines = writer.toString().split("\n");
        int limit = Math.min(lines.length, 20);
        return String.join("\n", java.util.Arrays.copyOfRange(lines, 0, limit));
    }
}
