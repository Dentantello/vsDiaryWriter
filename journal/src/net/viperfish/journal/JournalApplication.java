package net.viperfish.journal;

import java.io.File;
import java.io.IOException;
import java.security.Security;

import net.viperfish.journal.auth.AuthenticationManagerFactory;
import net.viperfish.journal.authentications.HashAuthFactory;
import net.viperfish.journal.framework.OperationExecutor;
import net.viperfish.journal.framework.UserInterface;
import net.viperfish.journal.persistent.DataSourceFactory;
import net.viperfish.journal.persistent.IndexerFactory;
import net.viperfish.journal.secure.SecureEntryDatabaseWrapper;
import net.viperfish.journal.secure.SecureFactoryWrapper;
import net.viperfish.journal.ui.CommandLineUserInterface;
import net.viperfish.journal.ui.SingleThreadedOperationExecutor;
import net.viperfish.utils.config.Configuration;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import test.java.StubDataSourceFactory;

public class JournalApplication {
	private static DataSourceFactory df;
	private static IndexerFactory indexerFactory;
	private static UserInterface ui;
	private static OperationExecutor worker;
	private static File configFile;
	private static boolean firstRun;
	private static File dataDir;
	private static AuthenticationManagerFactory authFactory;
	private static final boolean unitTest = false;
	private static String password;
	private static SystemConfig sysConf;

	static {
		Security.addProvider(new BouncyCastleProvider());
		initFileStructure();
		sysConf = new SystemConfig();
		Configuration.put(SecureEntryDatabaseWrapper.config().getUnitName(),
				SecureEntryDatabaseWrapper.config());
		Configuration.put(sysConf.getUnitName(), sysConf);
	}

	public JournalApplication() {
	}

	private static void cleanUp() {
		getDataSourceFactory().cleanUp();
		getIndexerFactory().cleanUp();
		getWorker().terminate();
	}

	private static void initFileStructure() {
		configFile = new File("config.xml");
		if (!configFile.exists()) {
		}
		dataDir = new File("data");
		if (!dataDir.exists()) {
			firstRun = true;
			dataDir.mkdir();
		}
	}

	public static OperationExecutor getWorker() {
		if (worker == null) {
			worker = new SingleThreadedOperationExecutor();
		}
		return worker;
	}

	public static DataSourceFactory getDataSourceFactory() {
		if (df == null) {
			if (unitTest) {
				df = new StubDataSourceFactory();
			} else {
				try {
					Class<?> selected = Class.forName(sysConf
							.getProperty("DataSourceFactory"));
					DataSourceFactory tmp = (DataSourceFactory) selected
							.newInstance();
					tmp.setDataDirectory(dataDir);
					df = new SecureFactoryWrapper(tmp, password);
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return df;
	}

	public static IndexerFactory getIndexerFactory() {
		if (indexerFactory == null) {
			try {
				indexerFactory = (IndexerFactory) Class.forName(
						sysConf.getProperty("IndexerFactory")).newInstance();
				indexerFactory.setDataDir(dataDir);
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		return indexerFactory;
	}

	public static AuthenticationManagerFactory getAuthFactory() {
		if (authFactory == null) {
			authFactory = new HashAuthFactory();
			authFactory.setDataDir(dataDir);
		}
		return authFactory;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		JournalApplication.password = password;
	}

	public static void main(String[] arg) {
		ui = new CommandLineUserInterface();
		try {
			Configuration.loadAll();
		} catch (IOException e1) {
			e1.printStackTrace();
			System.err.println(e1);
			System.exit(1);
		}
		ui.setAuthManager(getAuthFactory().getAuthenticator());
		if (firstRun) {
			ui.setup();
		}
		password = ui.promptPassword();
		ui.run();
		cleanUp();
		try {
			Configuration.persistAll();
		} catch (IOException e) {
			System.err
					.println("critical error incountered while saving configuration, quitting");
		}

	}

}
