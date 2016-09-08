package database;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import multithread.StoreSequenceInDatabaseFromPDBFileCallable;
import parameters.AlgoParameters;
import mystructure.EnumMyReaderBiojava;

public class UpdateSequenceDatabaseMultithreaded {
	//-------------------------------------------------------------
	// Class variables
	//-------------------------------------------------------------
	private AlgoParameters algoParameters;
	private EnumMyReaderBiojava enumMyReaderBiojava;
	private Connection connexion;



	//-------------------------------------------------------------
	// Constructor
	//-------------------------------------------------------------
	public UpdateSequenceDatabaseMultithreaded(AlgoParameters algoParameters, EnumMyReaderBiojava enumMyReaderBiojava){
		this.algoParameters = algoParameters;
		this.enumMyReaderBiojava = enumMyReaderBiojava;
		this.connexion = DatabaseTools.getConnection();
	}




	// -------------------------------------------------------------------
	// Public & Interface Methods
	// -------------------------------------------------------------------
	public void updateDatabase() throws FileNotFoundException, IOException{

		int maxCharInVarchar = 30000; 

		boolean deleteAll = true;
		if (deleteAll == true){
			try {
				Statement stmt = connexion.createStatement();
				String sql = "DROP TABLE sequence";
				stmt.execute(sql);
				System.out.println("Drop all tables from myDB !");
				stmt.close();
			} catch (SQLException e1) {
				System.out.println("Table sequence is not existing so cannot be droped");
			}

			// check if table exists if not create it
			try {
				Statement stmt = connexion.createStatement();
				//String createTableSql = "CREATE TABLE " + "sequence" + " (fourLettercode varchar(4), chainId varchar(1), sequenceString varchar(" + maxCharInVarchar + "), lastmodificationtime timestamp )";
				String createTableSql = "CREATE TABLE " + "sequence" + " (fourLettercode varchar(4), chainId varchar(1), "
						+ "sequenceString varchar(" + maxCharInVarchar + "), PRIMARY KEY (fourLettercode, chainId) ) ";
				//System.out.println(createTableSql);
				stmt.executeUpdate(createTableSql);
				System.out.println("created table sequence in myDB !");
				stmt.close();
			} catch (SQLException e1) {
				System.out.println("Table sequence already exists in myDB !");
			}
		}

		// TODO FMM create a section in algoParameters for Release
		// ************************************************************
		String pathToFile = algoParameters.getPATH_TO_TARGET_LIST_FILE_TO_BE_PROCESSED_BUILD_SEQ_DB();
		// ************************************************************

		int consumersCount = 6;
		final ExecutorService executorService = getExecutorServiceForComparisons(consumersCount);
		int timeSecondsToWaitIfQueueIsFullBeforeAddingMore = 60;

		// Read txt file without storing all
		try(BufferedReader br = new BufferedReader(new FileReader(pathToFile))) {
			String line = br.readLine();

			while (line != null) {

				StringTokenizer tok = new StringTokenizer(line, ","); 
				List<String> splittedLine = new ArrayList<>();
				while ( tok.hasMoreElements() )  
				{  
					String next = (String) tok.nextElement();
					splittedLine.add(next);
				}
				String fourLetterCode = null;
				String chainName = null;
				int chainLengthFromFile = 0;
				try {
					fourLetterCode = splittedLine.get(0);
					chainName = splittedLine.get(1);
					chainLengthFromFile = Integer.valueOf(splittedLine.get(2));
				}catch(Exception e){
					System.out.println("Problem in parsing line : " + line);
					line = br.readLine();
					continue;
				}

				// fourLetterCode
				// chainName
				StoreSequenceInDatabaseFromPDBFileCallable generateSequenceFromPDBFileCallable = new StoreSequenceInDatabaseFromPDBFileCallable(fourLetterCode, chainName, connexion, algoParameters, enumMyReaderBiojava, maxCharInVarchar);

				try{
					executorService.execute(generateSequenceFromPDBFileCallable);

				}catch (RejectedExecutionException e){

					try {
						Thread.sleep(timeSecondsToWaitIfQueueIsFullBeforeAddingMore * 1000);
						continue;
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}

				line = br.readLine();
			}
		}

		System.out.println("Sequence database is updated");
		DatabaseTools.shutdown();
	}



	private ExecutorService getExecutorServiceForComparisons(int consumersCount){
		int  corePoolSize  =    0; // no need to keep idle ones
		long keepAliveTime = 500000000; // no need to terminate if thread gets no job, that
		// could happen when searching database for a potetial hit, that could last as long
		// as the time to search the whole system
		int maxCountRunnableInBoundQueue = 10000; // 10000;

		ExecutorService threadPoolExecutor =
				new ThreadPoolExecutor(
						corePoolSize,
						consumersCount,
						keepAliveTime,
						TimeUnit.MILLISECONDS,
						new LinkedBlockingQueue<Runnable>(maxCountRunnableInBoundQueue)
						);

		return threadPoolExecutor;
	}
}
