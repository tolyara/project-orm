package storages;


import SQL.EntityDAO;
import SQL.QuerryBuilder;
import annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/*
 * Class for reflection methods
 */

public class Entity {

    private Class<?> entityClass;
    private Object entityObject;


    public Entity(Class entityClass) {
        if (entityClass.getAnnotation(Model.class) != null) {
            this.entityClass = entityClass;

            try {
                this.entityObject = entityClass.newInstance();
                if (!Table.isTableExist(this.getModelAnnotation().tableName().toLowerCase()))
                    Table.createTableFromEntity(this);
                //loadForeignKeys();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        } else {
            // TODO exception
        }
    }

    public Entity(Object object) {
        if (object.getClass().getAnnotation(Model.class) != null) {
            this.entityClass = object.getClass();
            entityObject = object;
            if (!Table.isTableExist(this.getModelAnnotation().tableName()))
                Table.createTableFromEntity(this);
			//loadForeignKeys();

		} else {
            // TODO exception
        }
    }

    public void loadForeignKeys() {
        try {
            loadOneToOne();
            loadManyToOne();
            loadOneToMany();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<String> getFieldsNames() {
        List<String> nameFields = new ArrayList<>();
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                nameFields.add(field.getAnnotation(Column.class).fieldName());
            }
        }
        return nameFields;
    }

    public List<String> getFieldTypes() {
        List<String> typesFields = new ArrayList<>();
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                typesFields.add(field.getType().getSimpleName());
            }
        }
        return typesFields;
    }

    public List<Field> getForeignKeyFields() {
        List<Field> foreignKeys = new ArrayList<>();
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(ForeignKey.class)) {
                foreignKeys.add(field);
            }
        }
        return foreignKeys;
    }

    public List<Field> getManyToManyFields() {
        List<Field> fieldsWithAnnotation = new ArrayList<>();
        for (Field f : entityClass.getDeclaredFields()) {
            if (f.isAnnotationPresent(ManyToMany.class)) {
                fieldsWithAnnotation.add(f);
            }
        }
        return fieldsWithAnnotation;
    }

    public Integer getPrimaryKeyValue() {
        Integer value = 0;
        for (Field column : getEntityClass().getDeclaredFields()) {
            if (column.isAnnotationPresent(PrimaryKey.class)) {
                // final String COLUMN_NAME = column.getAnnotation(Column.class).fieldName();

                // final String COLUMN_NAME = primaryKey();

                try {

                    // if (COLUMN_NAME.toLowerCase().equals(primaryKey())) {
                    // try {
                    column.setAccessible(true);
                    value = ((Integer) column.get(getEntityObject()));
                } catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    /*
     * Return line with entity fields toLowerCase comma separated without PK
     */
    public String getParsedFieldsLine() {
        StringBuilder parsedFields = new StringBuilder();

        for (Field parsedField : entityClass.getDeclaredFields()) {
            if (parsedField.isAnnotationPresent(Column.class)) {
                final String COLUMN_NAME = parsedField.getAnnotation(Column.class).fieldName();
                // if (!COLUMN_NAME.toLowerCase().equals(primaryKey())) { /* skip field that is
                // PK */
                try {
                    parsedFields.append(COLUMN_NAME.toLowerCase() + ", ");
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
        return parsedFields.toString().trim().substring(0, parsedFields.toString().length() - 2); // return with delete
        // last comma
    }

    /*
     * Return line with entity values of fields toLowerCase comma separated without
     * PK
     */
    public String getParsedValuesLine() {

        StringBuilder preparedValues = new StringBuilder();

        for (Field parsedField : entityClass.getDeclaredFields()) {
            if (parsedField.isAnnotationPresent(Column.class)) {
                final String COLUMN_NAME = parsedField.getAnnotation(Column.class).fieldName();
                // if (!COLUMN_NAME.toLowerCase().equals(primaryKey())) { /* skip field that is
                // PK */
                try {
                    parsedField.setAccessible(true);
                    /* getting value that we need to push */
                    Object fieldValue = (Object) parsedField.get(entityObject);
                    preparedValues.append("'" + fieldValue + "'" + ", ");
                } catch (IllegalArgumentException | IllegalAccessException | ClassCastException e) {
                    e.printStackTrace();
                }
            }
        }

        return preparedValues.toString().trim().substring(0, preparedValues.toString().length() - 2); // return with
        // delete last
        // comma
    }

    private void loadOneToOne() throws IllegalAccessException {

        for (Field field : entityClass.getDeclaredFields()) {
            if (field.getAnnotation(OneToOne.class) != null) {

                if (!Table.isTableExist(new Entity(field.getType()).tableName())) {
                    Table.createTableFromEntity(new Entity(field.getType()));
                }
                field.setAccessible(true);
                Entity localEntity = EntityDAO.getInstance().selectEntityById(new Entity(field.getType()), this.getPrimaryKeyValue());
                field.set(entityObject, localEntity.entityObject);
            }

        }

    }

    private void loadManyToOne() throws IllegalAccessException {

        for (Field field : entityClass.getDeclaredFields()) {
            if (field.getAnnotation(ManyToOne.class) != null) {

                if (!Table.isTableExist(new Entity(field.getType()).tableName())) {
                    Table.createTableFromEntity(new Entity(field.getType()));
                }
                field.setAccessible(true);
                //todo refactor

                try (final Statement statement = PGConnectionPool.getInstance().getConnection().createStatement();
                     final ResultSet resultSet = statement.executeQuery("SELECT * FROM " + getModelAnnotation().tableName() + " WHERE " + primaryKey() + " = " + getPrimaryKeyValue())) {
                     if(resultSet.next()){
                         Entity localEntity = EntityDAO.getInstance().selectEntityById(new Entity(field.getType()), resultSet.getInt(field.getAnnotation(ManyToOne.class).joinColumn()));
                         field.set(entityObject, localEntity.entityObject);
                     }

                } catch (SQLException e) {
                    e.printStackTrace();
                }


            }

        }
    }


    private void loadOneToMany() throws ClassNotFoundException, NoSuchFieldException {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.getAnnotation(OneToMany.class) != null) {
                ParameterizedType type = (ParameterizedType) field.getGenericType();
                String typeName = type.getActualTypeArguments()[0].getTypeName();
                Entity mappedEntity = new Entity(Class.forName(typeName));
                Field mappedField = mappedEntity.getEntityObject().getClass().getDeclaredField(field.getAnnotation(OneToMany.class).mappedBy());

                Set<Entity> entities = new HashSet<>();

                if(mappedField.getAnnotation(ManyToOne.class) != null) {


                    //todo refactor

                    try (final Statement statement = PGConnectionPool.getInstance().getConnection().createStatement();
                         final ResultSet resultSet = statement.executeQuery("SELECT * FROM " + mappedEntity.getModelAnnotation().tableName() + " WHERE " + mappedField.getAnnotation(ManyToOne.class).joinColumn() +" = " + getPrimaryKeyValue())) {
                        while (resultSet.next()) {
                            Entity e = EntityDAO.getInstance().setFieldsValue(mappedEntity, resultSet, mappedEntity.getModelAnnotation().primaryKey());
                            entities.add(e);
                        }
                        field.setAccessible(true);
                        field.set(this.entityObject,  entities);
                    } catch (SQLException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }


    public Model getModelAnnotation() {
        return (Model) entityClass.getAnnotation(Model.class);
    }

    public Class getEntityClass() {
        return entityClass;
    }

    public Object getEntityObject() {
        return entityObject;
    }

    public String tableName() {
        return getModelAnnotation().tableName();
    }

    public String primaryKey() {
        return getModelAnnotation().primaryKey();
    }

    public Class<? extends Annotation> annotationType() {
        return Model.class;
    }
    public QuerryBuilder column(String fieldName) {
        String columnName = "";
        for (Field parsedField : entityClass.getDeclaredFields()) {
            if (parsedField.getName().equals(fieldName)) {
                columnName = getAnnotationAttrubite(parsedField);
            } else {

            }
        }
        return new QuerryBuilder(columnName);
    }

    private String getAnnotationAttrubite(Field parsedField) {
        String columnName = "";
        if (parsedField.isAnnotationPresent(PrimaryKey.class)) {
            columnName = this.primaryKey();
        }
        else {
            columnName = parsedField.getAnnotation(Column.class).fieldName();
        }
        return columnName;
    }

}
