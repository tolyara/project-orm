package demo;


import java.lang.reflect.Field;
import java.sql.*;
import java.util.List;
import java.util.Set;

import javax.sql.rowset.CachedRowSet;

import connections.MyConnection;
import demo.models.*;
import sql.EntityDAO;
import sql.QueryBuilder;
import storages.Entity;
import storages.Table;
import transactions.Transaction;

/**
 * Class demonstrates the job with project main entities
 */
public class MainClass {

	public static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
	private static final String VERSION = "beta version";
	public static final MyConnection connection = new MyConnection(false);


	private static final Client CLIENT = new Client("Ivanov", "Ivan", false);

	public static void main(String[] args) throws Exception {

		 printHeader();
		 doDemo();
		 closeResources();

	}

	/*
	 * Method is needed for closing ORM objects after use
	 */
	private static void closeResources() {
		connection.close();
	}

	private static void printHeader() {
		System.out.println("PROJECT MY-ORM : " + VERSION);
	}

	private static void doDemo() throws Exception {

		Entity entity =  EntityDAO.getInstance().selectEntityById(new Entity(Worker.class), 1);

/*
		Table.createTableFromEntity(entityTeacher1);

		Table.createRecordInTable(entityTeacher1);
		Table.createRecordInTable(entityTeacher2);
		Table.createRecordInTable(entityTeacher3);
		Table.createRecordInTable(entityStudent1);
		Table.createRecordInTable(entityStudent2);
		Table.createRecordInTable(entityStudent3);*/
/*
		entityTeacher1.loadManyToMany(1, 1, 2);
		entityTeacher2.loadManyToMany(2, 1, 2, 3);
		entityTeacher3.loadManyToMany(3, 2, 3);
		entityStudent1.loadManyToMany(1, 2);
		entityStudent3.loadManyToMany(3, 1, 3);*/
	System.out.print("");


	}

	private static void createCustomScript() throws IllegalArgumentException, IllegalAccessException {
		
		QueryBuilder querryBuilder = new QueryBuilder();
		Entity entity = new Entity(Worker.class);
		
		final String QUERRY_2 = querryBuilder
				.select(entity.column("id"), (entity.column("salary").avg()))
				.from(entity)
				.where(entity.column("id").lessThan(4))
				.orderBy(entity.column("id")).submit();
		System.out.println("\n" + QUERRY_2 + "\n\n");		
		
		final String QUERRY_3 = querryBuilder.selectAll()
				.from(entity).where(entity.column("salary").moreThan(601))
				.and(entity.column("hasAddress").eq(false)).submit();	
		System.out.println(QUERRY_3);
		printReceivedObjects(EntityDAO.getInstance().executeCustomRequest(QUERRY_3, entity));		
	}

	private static void printReceivedObjects(List<Entity> entities)
			throws IllegalArgumentException, IllegalAccessException {
		for (Entity entity : entities) {
			Worker worker = (Worker) entity.getEntityObject();
			for (Field field : worker.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				System.out.printf("%14s", field.get(worker));
			}
			System.out.println();
		}
	}

	private static void tryTransaction() throws Exception {
		Transaction tx = new Transaction();
		tx.openConnection();
		Table.createRecordInTable(new Entity(new Worker(12, "test1", false, 1200.5)));
		connection.close();
		EntityDAO.getInstance().updateRecordInTable(new Entity(new Worker(12, "test1", false, 1200.5)));
		try {
			tx.commit();
		} catch (Throwable e) {
			tx.rollback();
			e.printStackTrace();
		}
	}
}