package database;


public class ControllerDataBaseBuilding {

	// -------------------------------------------------------------------
	// Control on DerbyDb
	// -------------------------------------------------------------------
	public static String DERBY_DATABASE_NAME = "";
	public static final int MAX_SIZE_IN_BYTES_FOR_A_DB_ENTRY = 262144 ; // 1024000; //262144;
	public static final int STOP_DATABASE_CLEANLY_WHEN_THIS_COUNT_ENTRIES_WRITTEN_IS_REACHED = 1000;
	public static boolean STOP_DATABASE_BUILDING = false;

}
