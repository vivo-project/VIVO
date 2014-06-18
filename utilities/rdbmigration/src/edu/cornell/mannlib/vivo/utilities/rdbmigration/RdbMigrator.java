/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vivo.utilities.rdbmigration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.GraphRDB;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.tdb.TDBFactory;

/**
 * A free-standing application that walks the user through the process of
 * copying VIVO configuration data from RDB to TDB.
 */
public class RdbMigrator {
	private static final String TABLE_RDB = "jena_graph";
	private static final String TABLE_MIGRATED = "vivo_rdb_migrated";
	private static final List<String> EXPECTED_MODELS = Arrays
			.asList(new String[] {
					"http://vitro.mannlib.cornell.edu/default/vitro-kb-displayMetadata",
					"http://vitro.mannlib.cornell.edu/default/vitro-kb-displayMetadata-displayModel",
					"http://vitro.mannlib.cornell.edu/default/vitro-kb-displayMetadataTBOX",
					"http://vitro.mannlib.cornell.edu/default/vitro-kb-userAccounts" });

	private final String vivoHomeDir;
	private final String jdbcUrl;
	private final String username;
	private final String password;

	private File targetDir;
	private boolean alreadyMigrated;
	private List<String> modelsToCopy;

	/**
	 * Confirm all of the parameters. Ask the user for approval. Do the
	 * migration.
	 * 
	 * @throws UserDeclinedException
	 *             If the user decides not to continue.
	 */
	public RdbMigrator(String vivoHomeDir, String jdbcUrl, String username,
			String password) throws UserDeclinedException, IOException,
			SQLException {
		this.vivoHomeDir = vivoHomeDir;
		this.jdbcUrl = jdbcUrl;
		this.username = username;
		this.password = password;

		testDbConnection();

		checkThatRdbExists();

		confirmTargetDirectory();

		if (doesTdbExist()) {
			askContinueOverTdb();
		}

		if (isAlreadyMigrated()) {
			askMigrateAgain();
		}

		getListOfRdbModels();

		if (isUnexpectedModels()) {
			askMigrateAllModels();
		}

		askApprovalForMigrationPlan();

		migrate();
	}

	private void testDbConnection() {
		try (Connection conn = getSqlConnection()) {
			// Just open and close it.
		} catch (SQLException e) {
			quit("Can't log in to database: '" + jdbcUrl + "', '" + username
					+ "', '" + password + "'\n" + e.getMessage());
		}
	}

	private void checkThatRdbExists() throws SQLException {
		try (Connection conn = getSqlConnection()) {
			DatabaseMetaData md = conn.getMetaData();
			try (ResultSet rs = md.getTables(null, null, TABLE_RDB, null);) {
				if (!rs.next()) {
					quit("The database at '" + jdbcUrl
							+ "' contains no RDB tables.");
				}
			}
		}
	}

	private void confirmTargetDirectory() {
		File vivoHome = new File(vivoHomeDir);
		if (!vivoHome.isDirectory()) {
			quit("'" + vivoHome + "' is not a directory.");
		}
		if (!vivoHome.canWrite()) {
			quit("Can't write to '" + vivoHome + "'.");
		}
		targetDir = new File(vivoHome, "tdbmodels");
	}

	private boolean doesTdbExist() {
		if (!targetDir.exists()) {
			return false;
		}

		if (!targetDir.isDirectory()) {
			quit("'" + targetDir + "' is not a directory.");
		}
		if (!targetDir.canWrite()) {
			quit("Can't write to '" + targetDir + "'.");
		}

		String[] filenames = targetDir.list();
		if (filenames == null || filenames.length == 0) {
			return false;
		}

		return true;
	}

	private void askContinueOverTdb() throws UserDeclinedException, IOException {
		ask("A directory of TDB files exists at '" + targetDir + "'.\n"
				+ "   Migration will replace the existing triples.\n"
				+ "Continue? (y/n)");
	}

	private boolean isAlreadyMigrated() throws SQLException {
		try (Connection conn = getSqlConnection()) {
			DatabaseMetaData md = conn.getMetaData();
			try (ResultSet rs = md.getTables(null, null, TABLE_MIGRATED, null);) {
				if (rs.next()) {
					alreadyMigrated = true;
					announceMigrationDate(conn);
					return true;
				} else {
					return false;
				}
			}
		}
	}

	private void announceMigrationDate(Connection conn) {
		String migrationDate = "UNKNOWN DATE";
		String query = String.format("SELECT date FROM %s LIMIT 1",
				TABLE_MIGRATED);

		try (Statement stmt = conn.createStatement();
				java.sql.ResultSet rs = stmt.executeQuery(query)) {
			if (rs.next()) {
				migrationDate = rs.getString("DATE");
			}
		} catch (SQLException e) {
			// go with default answer.
		}

		System.out.println("It looks like this RDB data has already been "
				+ "migrated to TDB, on " + migrationDate
				+ "\n   (found a table named '" + TABLE_MIGRATED + "')");
	}

	private void askMigrateAgain() throws UserDeclinedException, IOException {
		ask("Migrate again? (y/n)");
	}

	private void getListOfRdbModels() throws SQLException {
		try (Connection conn = getSqlConnection();
				ClosingDBConnection rdb = new ClosingDBConnection(conn)) {
			modelsToCopy = rdb.getAllModelNames().toList();
		}
	}

	private boolean isUnexpectedModels() {
		List<String> unexpectedModels = new ArrayList<>(modelsToCopy);
		unexpectedModels.removeAll(EXPECTED_MODELS);
		if (unexpectedModels.isEmpty()) {
			return false;
		}

		System.out
				.println("VIVO requires only these models from RDB:\n   "
						+ StringUtils.join(EXPECTED_MODELS, "\n   ")
						+ "\nYour RDB triple-store contains these additional models:\n   "
						+ StringUtils.join(unexpectedModels, "\n   "));
		return true;
	}

	private void askMigrateAllModels() throws IOException, UserDeclinedException {
		System.out.println("Migrate all models, or only the required models?\n"
				+ "   Enter 'all' or 'only', or anything else to quit.");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = br.readLine();
		if (s != null) {
			if (s.trim().toLowerCase().equals("all")) {
				return;
			} else if (s.trim().toLowerCase().equals("only")) {
				modelsToCopy = EXPECTED_MODELS;
				return;
			}
		}
		throw new UserDeclinedException("Enter 'all' or 'only'.");
	}

	private void askApprovalForMigrationPlan() throws SQLException,
			UserDeclinedException, IOException {
		int modelCount = 0;
		int tripleCount = 0;
		try (Connection conn = getSqlConnection();
				ClosingDBConnection rdb = new ClosingDBConnection(conn)) {
			for (String modelName : modelsToCopy) {
				modelCount++;
				try (ClosingGraphRDB graph = new ClosingGraphRDB(rdb, modelName)) {
					tripleCount += graph.size();
				}
			}
		}
		String warning = alreadyMigrated ? "   Existing triples will be over-written.\n"
				: "";
		String question = String.format("Migrating %d triples in %d models "
				+ "to TDB files in '%s'\n%sContinue? (y/n)", tripleCount,
				modelCount, targetDir, warning);
		ask(question);
	}

	private void quit(String message) {
		throw new IllegalArgumentException(
				"--------------------------------------------------------------\n"
						+ message
						+ "\n--------------------------------------------------------------");
	}

	private void ask(String string) throws UserDeclinedException, IOException {
		System.out.println(string);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = br.readLine();
		if ((s == null) || (!s.trim().toLowerCase().equals("y"))) {
			throw new UserDeclinedException("OK.");
		}
	}

	private static class UserDeclinedException extends Exception {
		public UserDeclinedException(String message) {
			super(message);
		}
	}

	public void migrate() throws SQLException {
		copyData();
		writeMigrationRecord();
	}

	private void copyData() throws SQLException {
		try (Connection conn = getSqlConnection();
				ClosingDBConnection rdb = new ClosingDBConnection(conn)) {
			Dataset tdbDataset = TDBFactory.createDataset(targetDir
					.getAbsolutePath());
			copyGraphs(rdb, tdbDataset);
		}
	}

	@SuppressWarnings("deprecation")
	private void copyGraphs(ClosingDBConnection rdb, Dataset tdbDataset) {
		DatasetGraph tdbDsGraph = tdbDataset.asDatasetGraph();
		for (String modelName : modelsToCopy) {
			try (ClosingGraphRDB graph = new ClosingGraphRDB(rdb, modelName)) {
				tdbDsGraph.addGraph(Node.createURI(modelName), graph);
				System.out
						.println(String.format("  copied %4d triples from %s",
								graph.size(), modelName));
			}
		}
	}

	private void writeMigrationRecord() throws SQLException {
		String createTable = String.format("CREATE TABLE %s (date DATE)",
				TABLE_MIGRATED);
		String deleteOldDates = String.format("DELETE FROM %s", TABLE_MIGRATED);
		String insertDate = String.format("INSERT INTO %s (date) VALUES (?)",
				TABLE_MIGRATED);
		try (Connection conn = getSqlConnection();
				Statement stmt = conn.createStatement();
				PreparedStatement pstmt = conn.prepareStatement(insertDate)) {
			if (alreadyMigrated) {
				stmt.executeUpdate(deleteOldDates);
			} else {
				stmt.executeUpdate(createTable);
			}
			pstmt.setDate(1, new Date(System.currentTimeMillis()));
			pstmt.executeUpdate();
		}
	}

	private Connection getSqlConnection() throws SQLException {
		Properties connectionProps;
		connectionProps = new Properties();
		connectionProps.put("user", username);
		connectionProps.put("password", password);
		return DriverManager.getConnection(jdbcUrl, connectionProps);
	}

	private static class ClosingDBConnection extends DBConnection implements
			AutoCloseable {
		ClosingDBConnection(Connection sqlConnection) {
			super(sqlConnection, "MySQL");
		}

	}

	private static class ClosingGraphRDB extends GraphRDB implements
			AutoCloseable {
		ClosingGraphRDB(DBConnection rdb, String modelName) {
			super(rdb, modelName, null,
					GraphRDB.OPTIMIZE_ALL_REIFICATIONS_AND_HIDE_NOTHING, false);
		}
	}

	// ----------------------------------------------------------------------
	// Main routine
	// ----------------------------------------------------------------------

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		if (args.length != 4) {
			System.out.println("Usage: RdbMigrator vivoHomeDir, jdbcUrl, "
					+ "username, password");
		}

		try {
			new RdbMigrator(args[0], args[1], args[2], args[3]);
		} catch (IllegalArgumentException | UserDeclinedException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
