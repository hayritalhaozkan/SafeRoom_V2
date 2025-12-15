package com.saferoom.log;

import com.saferoom.db.*;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collections;

public class Logger {

	private final String className;

	// ThreadLocal context for tracking metadata (Call ID, User ID) across threads
	private static final ThreadLocal<Map<String, String>> context = ThreadLocal.withInitial(ConcurrentHashMap::new);

	private Logger(Class<?> cls) {
		this.className = cls.getSimpleName();
	}

	private Logger(String className) {
		this.className = className;
	}

	public static Logger getLogger(Class<?> cls) {
		return new Logger(cls);
	}

	public static Logger getLogger(String className) {
		return new Logger(className);
	}

	public enum Level {
		INFO, WARN, ERROR, DEBUG, TRACE
	}

	// Context Management
	public static void setContext(String key, String value) {
		context.get().put(key, value);
	}

	public static void clearContext() {
		context.get().clear();
	}

	public static String getContext(String key) {
		return context.get().get(key);
	}

	/* [Timestamp] [LEVEL] [Thread] [Class] [Context] - Message */

	private String formatLogMessage(Level level, String message) {
		String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
		String threadName = Thread.currentThread().getName();

		// Build context string
		Map<String, String> ctx = context.get();
		String contextStr = ctx.isEmpty() ? "" : " " + ctx.toString();

		return String.format("[%s] [%-5s] [%s] [%s]%s - %s",
				timestamp, level, threadName, className, contextStr, message);
	}

	private static final String LOG_FILE = "logs/saferoom.log";

	static {
		File logDir = new File("logs");
		if (!logDir.exists()) {
			logDir.mkdirs(); // log dizini yoksa oluştur
		}
	}

	private synchronized void writeToFile(String message) {
		try (FileWriter fw = new FileWriter(LOG_FILE, true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.println(message);
		} catch (IOException e) {
			System.err.println("Log dosyasına yazılamadı: " + e.getMessage());
		}
	}

	private synchronized void writeToDatabase(Level level, String formattedMessage) {
		// Database logging implementation omitted for safety/performance in this
		// refactor
		// Can be re-enabled if DBManager is thread-safe and non-blocking
		/*
		 * String query = "INSERT INTO logs(timestamp, level, classname, message) "
		 * + "VALUES (CURRENT_TIMESTAMP, ?, ?, ?); ";
		 * try(Connection conn = DBManager.getConnection() ;
		 * PreparedStatement prpst = conn.prepareStatement(query)){
		 * 
		 * prpst.setString(1, level.name());
		 * prpst.setString(2, className);
		 * prpst.setString(3, formattedMessage);
		 * 
		 * prpst.executeUpdate();
		 * 
		 * } catch (Exception e) {
		 * System.err.println("DB Log failed: " + e.getMessage());
		 * }
		 */
	}

	private void log(Level level, String message, Throwable t) {
		String formattedMessage = formatLogMessage(level, message);

		// Console output
		if (level == Level.ERROR) {
			System.err.println(formattedMessage);
			if (t != null)
				t.printStackTrace();
		} else {
			System.out.println(formattedMessage);
		}

		// File output
		StringBuilder fileMsg = new StringBuilder(formattedMessage);
		if (t != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			t.printStackTrace(pw);
			fileMsg.append(System.lineSeparator()).append(sw.toString());
		}
		writeToFile(fileMsg.toString());

		// Database output (optional)
		// writeToDatabase(level, formattedMessage);
	}

	public void info(String message) {
		log(Level.INFO, message, null);
	}

	public void warn(String message) {
		log(Level.WARN, message, null);
	}

	public void error(String message) {
		log(Level.ERROR, message, null);
	}

	public void error(String message, Throwable t) {
		log(Level.ERROR, message, t);
	}

	public void debug(String message) {
		log(Level.DEBUG, message, null);
	}

	public void trace(String message) {
		log(Level.TRACE, message, null);
	}
}