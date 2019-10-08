package sql;

import storages.Entity;

public class QueryBuilder {
	
	private StringBuilder querry;

	private static final String COMMA = ", ";
	private static final String SEMICOLON = ";";
	private static final String SELECT_ALL = "SELECT *";
	private static final String FROM = " FROM ";
	private static final String WHERE = " WHERE ";
	private static final String SELECT = "SELECT ";
	private static final String EQUALS = " = ";
	private static final String MORE_THAN = " > ";
	private static final String LESS_THAN = " < ";
	private static final String ORDER_BY = " ORDER BY ";
	private static final String AND = " AND ";
	private static final String AVG = " AVG";
	
	public QueryBuilder() {
		querry = new StringBuilder();
	}
	
	public QueryBuilder(String string) {
		querry = new StringBuilder(string);
	}

	public String submit() {
		String resultQuerry = querry.append(SEMICOLON).toString();
		querry = new StringBuilder(); // clear builder for next queries
		return resultQuerry;
	}
	
	public QueryBuilder selectAll() {
		querry.append(SELECT_ALL);
		return this;
	}
	
	public QueryBuilder from(Entity entity) {
		querry.append(FROM);
		querry.append(entity.tableName());
		return this;
	}
	
	public QueryBuilder where(String condition) {
		querry.append(WHERE);
		querry.append(condition);
		return this;
	}
	
	public String eq(Object object) {
		querry.append(EQUALS);
		querry.append(object.toString());
		return this.toString();
	}
	
	public String moreThan(Object object) {
		querry.append(MORE_THAN);
		querry.append(object.toString());
		return this.toString();
	}
	
	public String lessThan(Object object) {
		querry.append(LESS_THAN);
		querry.append(object.toString());
		return this.toString();
	}
	
	public QueryBuilder orderBy(QueryBuilder column) {
		querry.append(ORDER_BY);
		querry.append(column.toString());
		return this;
	}
	
	public QueryBuilder and(String condition) {
		querry.append(AND);
		querry.append(condition);
		return this;
	}
	
	public QueryBuilder avg() {
		String column = this.toString();
		querry = new StringBuilder();
		querry.append(AVG);
		querry.append("(" + column + ")");
		return this;
	}

	@Override
	public String toString() {
		return querry.toString();
	}

	public QueryBuilder select(QueryBuilder ... columns) {
		querry.append(SELECT);
		StringBuilder localBuilder = new StringBuilder();
		for (QueryBuilder column : columns) {
			localBuilder.append(column.toString() + COMMA);
		}
		localBuilder = new StringBuilder(deleteLastComma(localBuilder.toString()));
		querry.append(localBuilder);
		return this;
	}

	private static String deleteLastComma(String string) {
		return string.toString().trim().substring(0, string.toString().length() - 2);		
	}	

}
