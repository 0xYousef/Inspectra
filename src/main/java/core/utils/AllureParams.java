package core.utils;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Parameter;
import io.qameta.allure.util.ResultsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Flattens the DTOs passed to a test into named Allure parameters, so the report
 * renders them as a readable key/value table (e.g. email, password,
 * personal info - title, expectation - status code...) instead of a single raw
 * {@code toString()} value.
 */
public final class AllureParams {
    private static final Logger log = LoggerFactory.getLogger(AllureParams.class);

    private AllureParams() {
    }

    /**
     * Replaces the current test case parameters with the flattened key/value list.
     * Takes no effect if no test is running or no readable value was found.
     *
     * @param args   the test method argument objects (usually data-provider DTOs).
     * @param method the test method, used to recover readable names for bare arguments.
     */
    public static void report(Object[] args, Method method) {
        if (args == null || args.length == 0) {
            return;
        }

        String[] paramNames = methodParamNames(method);
        List<Parameter> params = new ArrayList<>();
        Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg == null) {
                continue;
            }
            String paramName = i < paramNames.length ? paramNames[i] : null;
            if (paramName != null && isSimple(arg.getClass())) {
                flatten(arg, humanize(paramName), params, visited);
            } else {
                flatten(arg, null, params, visited);
            }
        }

        if (params.isEmpty()) {
            return;
        }

        Allure.getLifecycle().updateTestCase(testResult -> {
            testResult.getParameters().clear();
            testResult.getParameters().addAll(params);
        });
    }

    private static String[] methodParamNames(Method method) {
        if (method == null) {
            return new String[0];
        }
        java.lang.reflect.Parameter[] parameters = method.getParameters();
        String[] names = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            String name = parameters[i].getName();
            names[i] = name == null || name.matches("arg\\d+") ? null : name;
        }
        return names;
    }

    private static void flatten(Object obj, String prefix, List<Parameter> params, Set<Object> visited) {
        if (obj == null) {
            return;
        }

        Class<?> type = obj.getClass();

        if (isSimple(type)) {
            params.add(ResultsUtils.createParameter(valueKey(prefix), obj));
            return;
        }
        if (obj instanceof Iterable || type.isArray()) {
            flattenElements(obj, prefix, params, visited);
            return;
        }
        if (obj instanceof Map) {
            params.add(ResultsUtils.createParameter(valueKey(prefix), join((Map<?, ?>) obj)));
            return;
        }
        if (!visited.add(obj)) {
            return;
        }

        for (Field field : getAllFields(type)) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value == null) {
                    continue;
                }

                String fieldName = appendField(prefix, field.getName());
                Class<?> fieldType = value.getClass();

                if (isSimple(fieldType)) {
                    params.add(ResultsUtils.createParameter(fieldName, value));
                } else if (value instanceof Iterable || fieldType.isArray()) {
                    flattenElements(value, fieldName, params, visited);
                } else if (value instanceof Map) {
                    params.add(ResultsUtils.createParameter(fieldName, join((Map<?, ?>) value)));
                } else {
                    flatten(value, fieldName, params, visited);
                }
            } catch (IllegalAccessException | RuntimeException e) {
                log.warn("Could not read Allure parameter field {}: {}", field, e.getMessage());
            }
        }
    }

    private static void flattenElements(Object value, String prefix, List<Parameter> params, Set<Object> visited) {
        List<Object> items = new ArrayList<>();
        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                items.add(Array.get(value, i));
            }
        } else {
            for (Object item : (Iterable<?>) value) {
                items.add(item);
            }
        }

        for (int i = 0; i < items.size(); i++) {
            Object item = items.get(i);
            if (item == null) {
                continue;
            }
            flatten(item, elementKey(item, prefix, i), params, visited);
        }
    }

    private static String elementKey(Object item, String prefix, int index) {
        String name = isSimple(item.getClass()) && prefix != null
                ? prefix
                : elementTypeName(item.getClass());
        return name + " " + (index + 1);
    }

    private static String elementTypeName(Class<?> type) {
        String simple = type.getSimpleName();
        if (simple.endsWith("Info")) {
            simple = simple.substring(0, simple.length() - "Info".length());
        }
        return humanize(simple);
    }

    private static String appendField(String prefix, String fieldName) {
        String readable = humanize(fieldName);
        return prefix == null ? readable : prefix + " - " + readable;
    }

    private static String humanize(String value) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '_') {
                result.append(' ');
                continue;
            }
            if (i > 0 && (Character.isUpperCase(c) && !Character.isUpperCase(value.charAt(i - 1))
                    || Character.isDigit(c) && Character.isLetter(value.charAt(i - 1)))) {
                result.append(' ');
            }
            result.append(Character.toLowerCase(c));
        }
        return result.toString().trim().replaceAll("\\s+", " ");
    }

    private static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        Set<String> names = new HashSet<>();
        for (Class<?> clazz = type; clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                    continue;
                }
                if (!names.add(field.getName())) {
                    continue;
                }
                fields.add(field);
            }
        }
        return fields;
    }

    private static boolean isSimple(Class<?> type) {
        return type.isPrimitive()
                || type.isEnum()
                || CharSequence.class.isAssignableFrom(type)
                || Number.class.isAssignableFrom(type)
                || type == Boolean.class
                || type == Character.class
                || type == UUID.class
                || type == BigDecimal.class
                || type == BigInteger.class
                || Date.class.isAssignableFrom(type)
                || TemporalAccessor.class.isAssignableFrom(type);
    }

    private static String join(Map<?, ?> map) {
        List<String> items = new ArrayList<>();
        map.forEach((key, value) -> items.add(key + "=" + value));
        return String.join(", ", items);
    }

    private static String valueKey(String prefix) {
        return prefix == null ? "value" : prefix;
    }
}
