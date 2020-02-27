package sql;

import java.lang.reflect.Field;

import annotations.Column;
import annotations.ForeignKey;
import annotations.ManyToMany;
import annotations.ManyToOne;
import storages.DataTypes;
import storages.Entity;
import storages.Table;

import java.lang.reflect.Field;

/*
 * Class for building SQL requests
 */

public final class SQLBuilder {
    private final static String CREATETABLE = "CREATE TABLE ";
    private final static String PRIMARYKEY = "PRIMARY KEY";
    private final static String _ID = "_id";
    private final static String ID = " (id)";
    private final static String ALTERTABLE = "ALTER TABLE ";
    private final static String ADDCONSTRAINT = " ADD CONSTRAINT ";
    private final static String FOREIGNKEY = " FOREIGN KEY";
    private final static String INTEGER = " INTEGER, ";
    private final static String LEFTBRACKET = " (";
    private final static String RIGHTBRACKET = ")";
    private final static String SERIAL = " serial, ";
    private final static String INSERTINTO = "INSERT INTO ";


    /*
     * Method for building SQL request to create table from Entity class
     * @param Entity class object.
     * @return create-table SQL request
     */
    public static String buildCreateTableRequest(Entity entity) {

        String tableName = entity.tableName();
        String primaryKey = entity.primaryKey();

        StringBuilder SQLRequest = new StringBuilder(CREATETABLE + tableName
                + LEFTBRACKET + primaryKey + SERIAL);
        SQLRequest.append(buildEntityFieldLine(entity));
        SQLRequest.append(PRIMARYKEY).append(LEFTBRACKET).append(primaryKey).append(RIGHTBRACKET);
        SQLRequest.append(RIGHTBRACKET);
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

    public static String buildForeignKeyRequest(Entity entity, Field field, String joinTableName) {
        ManyToMany annotation = field.getAnnotation(ManyToMany.class);
        String columnName = entity.tableName() + _ID;
        StringBuilder SQLRequest = new StringBuilder();
        SQLRequest.append(ALTERTABLE).append(joinTableName).append(ADDCONSTRAINT);
        SQLRequest.append("fk_").append(columnName).append(joinTableName);
        SQLRequest.append(FOREIGNKEY).append(LEFTBRACKET).append(columnName).append(RIGHTBRACKET);
        SQLRequest.append(" REFERENCES ").append(entity.tableName()).append(ID);
        SQLRequest.append(" ON UPDATE ").append(annotation.onUpdate().toString()).append(" ");
        SQLRequest.append(" ON DELETE ").append(annotation.onDelete().toString()).append(" ");
        return SQLRequest.toString();
    }

    public static String alterIntFieldLine(String tableName, String idName)
    {
        return "ALTER TABLE " + tableName + " add " + idName + " INT;" ;
    }

    public static String buildCreateForeignKeyRequest(Entity entity, Field field) {
        ForeignKey annotation = field.getAnnotation(ForeignKey.class);
        StringBuilder SQLRequest = new StringBuilder();
        try {
            String name = "demo.models." + annotation.entity();            //todo fix this line
            Entity entityRequest = new Entity(Class.forName(name));
            String requestTableName = entityRequest.getModelAnnotation().tableName();

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

    public static String buildJoinTableRequest(Entity first, Entity second, String joinTableName) {
        String firstColumnName = first.tableName() + _ID;
        String secondColumnName = second.tableName() + _ID;
        String createTable = CREATETABLE + joinTableName +
                "(id" + SERIAL + firstColumnName + INTEGER + secondColumnName + INTEGER +  PRIMARYKEY  + ID + RIGHTBRACKET;
        return createTable;
    }

    public static String buildCreateRecordInJoinTableRequest(Entity parent, Entity child, int parentId, int childId) {
        String joinTableName = Table.getJoinTableName(parent, child);
        String columnParent = parent.tableName() + _ID;
        String columnChild = child.tableName() + _ID;
        String request = INSERTINTO + joinTableName
                + LEFTBRACKET + columnParent + ", " + columnChild + RIGHTBRACKET
                + " VALUES" + LEFTBRACKET + parentId + ", " + childId + RIGHTBRACKET;
        return request;
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

    public static String buildFkForManyToOne(Entity entity, Entity entityRequest, Field field){

        StringBuilder SQLRequest = new StringBuilder();
        SQLRequest.append("ALTER TABLE ").append(entity.tableName()).append(" ADD CONSTRAINT ");
        SQLRequest.append("fk_").append(entity.tableName()).append("_").append(field.getName());
        SQLRequest.append(" FOREIGN KEY ");
        SQLRequest.append("(").append(field.getAnnotation(ManyToOne.class).joinColumn()).append(")");
        SQLRequest.append(" REFERENCES ").append(entityRequest.getModelAnnotation().tableName()).append(" ");
        SQLRequest.append("(").append(entityRequest.getModelAnnotation().primaryKey()).append(")");
        SQLRequest.append(" ON UPDATE ").append(field.getAnnotation(ManyToOne.class).onUpdate().toString()).append(" ");
        SQLRequest.append(" ON DELETE ").append(field.getAnnotation(ManyToOne.class).onDelete().toString()).append(" ");

        return SQLRequest.toString();
    }
}
