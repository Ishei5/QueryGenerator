package com.roadtosenior;

import com.roadtosenior.annotations.Column;
import com.roadtosenior.annotations.PK;
import com.roadtosenior.annotations.Table;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.StringJoiner;

public class QueryGenerator {
    public String getAll(Class<?> clazz) {
        StringBuilder stringBuilder = new StringBuilder("SELECT ");

        String table_name = getTableName(clazz);

        String annotatedFields = getAllAnnotatedFields(clazz);

        stringBuilder.append(annotatedFields);
        stringBuilder.append(" FROM ");
        stringBuilder.append(table_name);
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    public String getById(Class<?> clazz, Object id) throws ClassCastException {
        StringBuilder stringBuilder = new StringBuilder(getAll(clazz));
        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        stringBuilder.append(" WHERE id = ");
        stringBuilder.append(validateId(id));
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    public String insert(Object value) {
        Class clazz = value.getClass();
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO ");
        stringBuilder.append(getTableName(clazz) + " ");
        stringBuilder.append(bracketsWrapper(getAllAnnotatedFields(clazz)));
        stringBuilder.append(" VALUES ");
        StringJoiner insertedValues = new StringJoiner(", ", "(", ")");
        for (Field declaredField : clazz.getDeclaredFields()) {
            insertedValues.add(callGetter(declaredField, value));
        }
        stringBuilder.append(insertedValues);
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

//    Assume that fetch by primary key
    public String update(Object value) {
        Class clazz = value.getClass();
        StringBuilder stringBuilder = new StringBuilder("UPDATE ");
        stringBuilder.append(getTableName(clazz) + " ");
        stringBuilder.append("SET ");
        StringJoiner columnNames = new StringJoiner(", ", "(", ")");
        StringJoiner newValues = new StringJoiner(", ", "(", ")");
        StringJoiner primaryKeyValues = new StringJoiner(" AND ");
        for (Field declaredField : clazz.getDeclaredFields()) {
            Column columnAnnotation = declaredField.getAnnotation(Column.class);
            PK primaryKeyAnnotation = declaredField.getAnnotation(PK.class);
            if (columnAnnotation != null) {
                String columnName = columnAnnotation.name().isEmpty() ? declaredField.getName() :
                        columnAnnotation.name();
                if (primaryKeyAnnotation == null) {
                    columnNames.add(columnName);
                    newValues.add(callGetter(declaredField, value));
                } else {
                    primaryKeyValues.add(columnName + " = " + callGetter(declaredField, value));
                }

            }
        }

        stringBuilder.append(columnNames);
        stringBuilder.append(" = ");
        stringBuilder.append(newValues);
        stringBuilder.append(" WHERE ");
        stringBuilder.append(primaryKeyValues);
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    public String delete(Class<?> clazz, Object id) {
        StringBuilder stringBuilder = new StringBuilder("DELETE FROM ");
        stringBuilder.append(getTableName(clazz));
        stringBuilder.append(" WHERE id = ");
        stringBuilder.append(validateId(id));
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    private String getTableName(Class<?> clazz) {
        Table annotation = clazz.getAnnotation(Table.class);

        if (annotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }

        String tableName = annotation.name().isEmpty() ? clazz.getName() : annotation.name();

        return tableName;
    }

    private String getAllAnnotatedFields(Class<?> clazz) {
        StringJoiner stringJoiner = new StringJoiner(", ");

        for (Field declaredField : clazz.getDeclaredFields()) {
            Column columnAnnotation = declaredField.getAnnotation(Column.class);
            if (columnAnnotation != null) {
                String columnName = columnAnnotation.name().isEmpty() ? declaredField.getName() :
                        columnAnnotation.name();
                stringJoiner.add(columnName);
            }
        }
        return stringJoiner.toString();
    }

    private StringJoiner bracketsWrapper(String values) {
        StringJoiner stringJoiner = new StringJoiner("", "(", ")");
        stringJoiner.add(values);

        return stringJoiner;
    }

//    Not sure if the processing of exceptions is correct
    private String callGetter(Field field, Object value) {
        String fieldName = field.getName();
        String getMethod = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        String returnedValue = null;
        try {
            returnedValue = value.getClass().getMethod(getMethod).invoke(value).toString();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        if (String.class.isAssignableFrom(field.getType())) {
            StringBuilder stringBuilder = new StringBuilder("'");
            stringBuilder.append(returnedValue);
            stringBuilder.append("'");
            return stringBuilder.toString();
        }
        return returnedValue;
    }

    private int validateId(Object id) throws NumberFormatException {
        return Integer.parseInt(id.toString());
    }
}