package storages;

import annotations.*;
import connections.MyConnection;
import sql.EntityDAO;
import sql.QuerryBuilder;
import sql.SQLBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

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
				if (!Table.isTableExist(this.getModelAnnotation().tableName()))
					Table.createTableFromEntity(this);
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
		} else {
			// TODO exception
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

	public List<Field> getManyToManyFields() {
		List<Field> fieldsWithAnnotation = new ArrayList<>();
		for (Field f : entityClass.getDeclaredFields()){
			if (f.isAnnotationPresent(ManyToMany.class)){
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

	public void loadManyToMany(int parentId, int... childIds) {
		createManyToManyDependency();

		List<Field> fields = this.getManyToManyFields();
		for (Field field: fields){
			try {
				Entity child = getEntityFromFieldWithCollection(field);

				field.setAccessible(true);
				Collection<Object> childs = new HashSet<>();


				try (final Statement statement = new MyConnection(false).getConnection().createStatement()) {
					for (int childId : childIds) {
						Entity entity = EntityDAO.getInstance().selectEntityById(child, childId);
						childs.add(entity.getEntityObject());
						statement.executeUpdate(SQLBuilder.buildCreateRecordInJoinTableRequest(this, child, parentId, childId));
					}
				}
				field.set(getEntityObject(), childs);
			} catch (SQLException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	private void createManyToManyDependency() {
		List<Field> manyToManyFields = this.getManyToManyFields();
		if (manyToManyFields.size() > 0) {
			for (Field desiredField : manyToManyFields) {
				Entity child = this.getEntityFromFieldWithCollection(desiredField);
				if (!Table.isTableExist(child.tableName())) {
					Table.createTableFromEntity(child);
				}
				String joinTableName = Table.getJoinTableName(this, child);
				if (!Table.isTableExist(joinTableName)) {
					executeManyToManyRequest(this, child, desiredField, joinTableName);
				}
			}
		}
	}

	private static void executeManyToManyRequest(Entity parent, Entity child, Field field, String tableName) {
		try (final Statement statement = new MyConnection(false).getConnection().createStatement()) {
			statement.executeUpdate(SQLBuilder.buildJoinTableRequest(parent, child, tableName));
			statement.executeUpdate(SQLBuilder.buildForeignKeyRequest(parent, field, tableName));
			statement.executeUpdate(SQLBuilder.buildForeignKeyRequest(child, field, tableName));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Entity getEntityFromFieldWithCollection(Field field) {
		Class dependentClassName = null;
		try {
			String fullDesiredFieldName = field.getGenericType().toString();
			String genericClassNameFormList = fullDesiredFieldName.substring(fullDesiredFieldName.indexOf("<") + 1, fullDesiredFieldName.indexOf(">"));
			dependentClassName = Class.forName(genericClassNameFormList);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return new Entity(dependentClassName);
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
