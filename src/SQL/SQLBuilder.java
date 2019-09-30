package SQL;

import annotations.Field;
import annotations.ForeignKey;
import annotations.Model;
import storages.DataTypes;
import storages.Entity;

/*
 * Class for building SQL requests
 */

public final class SQLBuilder {


    /*
     * Method for building SQL request to create table from Entity class
     * @param Entity class object.
     * @return create-table SQL request
     */
    public static String buildCreateTableRequest(Entity entity) {

        String tableName = entity.tableName();
        String primaryKey = entity.primaryKey();

        StringBuilder SQLRequest = new StringBuilder("CREATE TABLE " + tableName
                + " (" + primaryKey + " serial, ");
        SQLRequest.append(buildEntityFieldLine(entity));
        SQLRequest.append("PRIMARY KEY (").append(primaryKey).append(")");
        SQLRequest.append(buildEntityForeignKeyFieldLine(entity));
        SQLRequest.append(")");
        return SQLRequest.toString();
    }


    private static String buildEntityFieldLine(Entity entity) {
        StringBuilder fieldLine = new StringBuilder();

        for (int i = 0; i < entity.getFieldsNames().size(); i++) {
            fieldLine.append(entity.getFieldsNames().get(i));
            fieldLine.append(" ").append(DataTypes.getInstance().getDataTypes().get(entity.getFieldTypes().get(i))).append(", ");
        }
        return fieldLine.toString();
    }

    private static String buildEntityForeignKeyFieldLine(Entity entity) {
        StringBuilder foreignKeyFieldLine = new StringBuilder();

        if (entity.getForeignKeyFields().size() > 0) {
            for (java.lang.reflect.Field field : entity.getForeignKeyFields()) {
                foreignKeyFieldLine.append(", FOREIGN KEY ");
                ForeignKey annotation = field.getAnnotation(ForeignKey.class);
                foreignKeyFieldLine.append("(").append(field.getAnnotation(Field.class).fieldName()).append(")");
                foreignKeyFieldLine.append(" REFERENCES ").append(annotation.table()).append(" ");
                foreignKeyFieldLine.append("(").append(annotation.column()).append(")");
                foreignKeyFieldLine.append(" ON UPDATE ").append(annotation.onUpdate().toString()).append(" ");
                foreignKeyFieldLine.append(" ON DELETE ").append(annotation.onDelete().toString()).append(" ");
            }
        }
        return foreignKeyFieldLine.toString();
    }

    public static String buildFieldValuesLine(Entity entity){
        StringBuilder valuesLine = new StringBuilder();
        String columnName, fieldValue;

        for (java.lang.reflect.Field parsedField : entity.getEntityClass().getDeclaredFields()) {
            columnName = parsedField.getName();
            //todo refactor
            if (!columnName.toLowerCase().equals(entity.primaryKey())) { /* skip field that is PK */
                try {
                    parsedField.setAccessible(true);
                    fieldValue = ((String) parsedField.get(entity.getEntityObject()));
                    valuesLine.append(columnName + " = '" + fieldValue + "'" + ", ");
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return valuesLine.toString().substring(0, valuesLine.length() - 2);
    }



}
