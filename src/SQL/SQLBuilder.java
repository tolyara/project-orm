package SQL;

import java.lang.reflect.Field;

import annotations.Column;
import annotations.ForeignKey;
import storages.DataTypes;
import storages.Entity;
import storages.Table;

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


    public static String buildCreateForeignKeyRequest(Entity entity, Field field) {
        ForeignKey annotation = field.getAnnotation(ForeignKey.class);
        StringBuilder SQLRequest = new StringBuilder();
        try {
            String name = "demo.models." + annotation.entity();
            Entity entityRequest = new Entity(Class.forName(name));
            String requestTableName = entityRequest.tableName();

            if (!Table.isTableExist(requestTableName)) {
                Table.createTableFromEntity(entityRequest);
            }

            SQLRequest.append("ALTER TABLE ").append(entity.tableName()).append(" ADD CONSTRAINT ");
            SQLRequest.append("fk_").append(entity.tableName()).append("_").append(field.getName());
            SQLRequest.append(" FOREIGN KEY ");
            SQLRequest.append("(").append(field.getAnnotation(Column.class).fieldName()).append(")");
            SQLRequest.append(" REFERENCES ").append(requestTableName).append(" ");
            SQLRequest.append("(").append(annotation.column()).append(")");
            SQLRequest.append(" ON UPDATE ").append(annotation.onUpdate().toString()).append(" ");
            SQLRequest.append(" ON DELETE ").append(annotation.onDelete().toString()).append(" ");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return SQLRequest.toString();
    }

    public static String buildFieldValuesLine(Entity entity) {
        StringBuilder valuesLine = new StringBuilder();
        String columnName;
        Object fieldValue;

        for (Field parsedField : entity.getEntityClass().getDeclaredFields()) {
        	if (parsedField.isAnnotationPresent(Column.class)) {
            columnName = parsedField.getAnnotation(Column.class).fieldName();

                try {
                    parsedField.setAccessible(true);
                    fieldValue = ((Object) parsedField.get(entity.getEntityObject()));
                    valuesLine.append(columnName + " = '" + fieldValue + "'" + ", ");
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return valuesLine.toString().substring(0, valuesLine.length() - 2);
    }
}
