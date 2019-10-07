package demo;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.List;

import connections.MyConnection;
import demo.models.Client;
import demo.models.ForTest;
import demo.models.ImmutableWorker;
import demo.models.TestModel;
import demo.models.Worker;
import sql.EntityDAO;
import sql.QuerryBuilder;
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

	private static final Client CLIENT = new Client("Ivanov", "Ivan", "false");

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
//		 Table.createTableFromEntity(new Entity(ForTest.class));

//		printReceivedObjects(EntityDAO.getInstance().readAllRecordsOrderedByPK(new Entity(Worker.class)));

		// Entity en = EntityDAO.getInstance().selectEntityById(new
		// Entity(Worker.class), 40);
		// System.out.println(en.getEntityObject());

//		 Table.createRecordInTable(new Entity(new ImmutableWorker(12, "tes65", true,
//		 600.5)));
		// Table.createRecordInTable(new Entity(new Worker(12, "test9", false, 999,
		// 9)));
		// Table.createTableFromEntity(new Entity(Client.class));
		// EntityDAO.getInstance().updateRecordInTable(new Entity(new Worker(10,
		// "super_test4",
		// false, 1000, 23)));
//		System.out.println(EntityDAO.getInstance().deleteRecordInTableByPK(new Entity(new Worker(10))));
		// entityDAO.deleteAllRecordsInTable(new Entity(Worker.class));
		
//		createCustomScript();
		
		
	}

	private static void createCustomScript() {
		
		QuerryBuilder querryBuilder = new QuerryBuilder();
		Entity entity = new Entity(Worker.class);
		
		final String QUERRY_1 = querryBuilder.selectAll().from(entity).submit();
		System.out.println(QUERRY_1);
		
		final String QUERRY_2 = querryBuilder
				.select(entity.column("id"), (entity.column("surname").avg()))
				.from(entity)
				.where(entity.column("hasAddress").lessThan(true))
				.orderBy(entity.column("id")).submit();
		System.out.println(QUERRY_2);		
		
		final String QUERRY_3 = querryBuilder.selectAll().from(entity).where(entity.column("salary").moreThan(100))
				.and(entity.column("hasAddress").eq(false)).submit();				
		System.out.println(QUERRY_3);
		
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
		// myORM.close();
		EntityDAO.getInstance().updateRecordInTable(new Entity(new Worker(12, "test1", false, 1200.5)));
		try {
			tx.commit();
		} catch (Throwable e) {
			tx.rollback();
			e.printStackTrace();
		}
	}

}