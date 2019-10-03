package storages;

import annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	
}
