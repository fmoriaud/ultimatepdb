package database;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Fabrice on 29/09/16.
 */
public class UpdateSequenceDatabase {

    private Connection connexion;
    private int maxCharInVarchar = 30000;


    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public UpdateSequenceDatabase() {

        this.connexion = DatabaseTools.getConnection();
    }


    // -------------------------------------------------------------------
    // Public & Interface Methods
    // -------------------------------------------------------------------
    public void buildDatabase(){

    }


    private void create() {

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
}