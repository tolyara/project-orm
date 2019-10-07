package demo;


import java.lang.reflect.Field;
import java.sql.*;
import java.util.List;
import java.util.Set;

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

		 Table.createTableFromEntity(new Entity(Worker.class));
/*
		List<Entity> entities = EntityDAO.getInstance().readAllRecordsOrderedByPK(new Entity(Worker.class));
		Entity entity = EntityDAO.getInstance().selectEntityById(new Entity(Worker.class), 3);
		System.out.println("");*/
		//printReceivedObjects(EntityDAO.getInstance().readAllRecordsOrderedByPK(new Entity(Worker.class)));

//		 Object en = EntityDAO.getInstance().selectEntityById(Worker.class, 7);
//		 System.out.println(en);

//		 Table.createRecordInTable(new Entity(new ImmutableWorker(12, "tes65", true, 600.5)));

//		 Table.createTableFromEntity(new Entity(Client.class));
/*
		Worker objWorker1 = new Worker("work1", true);
		Worker objWorker2 = new Worker("work2", true);
		Worker objWorker3 = new Worker("work3", true);
		Client objClient1 = new Client("sur1", "name1", false);
		Client objClient2 = new Client("sur2", "name2", false);

		 Entity worker1 = new Entity(objWorker1);
		 Entity worker2 = new Entity(objWorker2);
		 Entity worker3 = new Entity(objWorker3);
		 Entity client1 = new Entity(objClient1);
		 Entity client2 = new Entity(objClient2);
		Table.createRecordInTable(worker1);
		Table.createRecordInTable(worker2);
		Table.createRecordInTable(worker3);
		Table.createRecordInTable(client1);
		Table.createRecordInTable(client2);

		worker1.loadManyToMany(1, 1, 2);
		worker2.loadManyToMany(2, 1);
		client1.loadManyToMany(1, 2, 3);

		System.out.println(objWorker1.getClients());
		System.out.println(objWorker2.getClients());
		System.out.println(objClient1.getWorkers());
*/

//		Table.loadManyToMany(worker1,1, 1 , 2);
//		Table.loadManyToMany(client1,1, 1 , 2);

		// Table.deleteEntityTable("worker");
//		 EntityDAO.getInstance().updateRecordInTable(new Entity(new Worker(10, "super_test4",
//		 false, 1000, 23)));
//		 EntityDAO.getInstance().deleteRecordInTableByPK(new Entity(new Worker(10)));
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