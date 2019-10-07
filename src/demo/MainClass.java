package demo;

import SQL.EntityDAO;
import SQL.QuerryBuilder;
import connections.MyConnection;
import demo.fk_models.Student;
import demo.fk_models.Teacher;
import demo.models.Client;
import demo.models.TestModel;
import demo.models.Worker;
import storages.Entity;
import storages.PGConnectionPool;
import storages.Table;
import transactions.Transaction;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


/**
 * Class demonstrates the job with project main entities
 */
public class MainClass {

	public static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
	private static final String VERSION = "beta version";
	public static final MyConnection connection = new MyConnection(false);

	private static final Client CLIENT = new Client("Ivanov", "Ivan", "false");

	public static void main(String[] args) throws Exception {

		//fillTables();
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

		// Table.createTableFromEntity(new Entity(Worker.class));

		try (final PreparedStatement statement = PGConnectionPool.getInstance().getConnection().prepareStatement("DROP TABLE student,teacher")) {
			//statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
        Entity student = new Entity(Student.class);
        Entity teacher = new Entity(Teacher.class);
		student = EntityDAO.getInstance().selectEntityById(student, 1);
        teacher = EntityDAO.getInstance().selectEntityById(teacher, 1);

        Teacher t = (Teacher) teacher.getEntityObject();
        Entity e = new Entity(t.getStudent(1));

        //List<Entity> entities = EntityDAO.getInstance().readAllRecordsOrderedByPK(new Entity(Worker.class));
		//entities.get(0).loadOneToOne();
		//EntityDAO.getInstance().createRecordInTable(new Entity(new Worker("test", false, 1)));
		//Entity entity = EntityDAO.getInstance().selectEntityById(new Entity(Worker.class), 3);
		System.out.println("");
		//printReceivedObjects(EntityDAO.getInstance().readAllRecordsOrderedByPK(new Entity(Worker.class)));
//		 Table.createTableFromEntity(new Entity(Worker.class));
		
//		printReceivedObjects(EntityDAO.getInstance().readAllRecordsOrderedByPK((Worker.class)));


		// Entity en = EntityDAO.getInstance().selectEntityById(new
		// Entity(Worker.class), 40);
		// System.out.println(en.getEntityObject());

//		 Table.createRecordInTable(new Entity(new ImmutableWorker(12, "tes65", true, 600.5)));
//		Table.createRecordInTable(new Entity(new Worker(12, "test9", false, 999, 9)));
//		 Table.createTableFromEntity(new Entity(Client.class));
		// Table.deleteEntityTable("worker");
//		 EntityDAO.getInstance().updateRecordInTable(new Entity(new Worker(10, "super_test4",
//		 false, 1000, 23)));
//		 EntityDAO.getInstance().deleteRecordInTableByPK(new Entity(new Worker(10)));
	}

	private static void fillTables()
	{
		Client client;
		Worker worker;

		for(int i = 0; i < 20; i++){
			TestModel testModel = new TestModel("field " + i, new Date(System.currentTimeMillis()));
			EntityDAO.getInstance().createRecordInTable(new Entity(testModel));
			client = new Client("surname " + i, "name" + i,"false", i);
			EntityDAO.getInstance().createRecordInTable(new Entity(client));
			worker = new Worker("surname " + i, false, 1);
			EntityDAO.getInstance().createRecordInTable(new Entity(worker));
		}

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