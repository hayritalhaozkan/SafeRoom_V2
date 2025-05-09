package com.saferoom.log;

import com.saferoom.db.*;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	
	private final String className;
	
	private Logger(Class<?> cls) {
		
		this.className = cls.getSimpleName();	
	}
	
	private Logger(String className) {
		this.className = className;
	}
	
	public static Logger getLogger(Class<?> cls ) {
		
		return new Logger(cls);
	}
	
	public enum Level{
		INFO, WARN, ERROR, DEBUG, TRACE
	}
	
	/*     [Tarih-Zaman] [LEVEL] [SINIF ADI] ~ Mesaj          */
	
	private String formatLogMessage(Level level, String message)
	{
		String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		
		return "[" + timestamp + "] [" + level + "] [" + className + "] - " + message; 
	}
	
	private static final String LOG_FILE = "logs/saferoom.log";
	
	static {
	    File logDir = new File("logs");
	    if (!logDir.exists()) {
	        logDir.mkdirs();  // log dizini yoksa oluştur
	    }
	}
	
	private synchronized void writeToFile(String message) {
		try(FileWriter fw = new FileWriter(LOG_FILE, true);
			BufferedWriter bw = new BufferedWriter(fw);	
			PrintWriter out = new PrintWriter(bw)){
			out.println(message);
		} catch(IOException e) {
			System.err.println("Log dosyasına yazılamadı: " + e.getMessage());
		}
	}
	//ExecuteQuery sadece SELECT Sorguları için kullanılır ve ResultSet döner.
	
	private synchronized void writeToDatabase(Level level, String formattedMessage)throws Exception {
		String query = "INSERT INTO logs(timestamp, level, classname, message) "
						+ "VALUES (CURRENT_TIMESTAMP, ?, ?, ?); ";
		try(Connection conn = DBManager.getConnection()	;	
			PreparedStatement prpst = conn.prepareStatement(query)){
			
				prpst.setString(1, level.name());
				prpst.setString(2, className);
				prpst.setString(3, formattedMessage);
				
				prpst.executeUpdate();
				
		}
	}
	
	
	
	private synchronized void log(String formattedMessage) throws Exception {
		System.out.println(formattedMessage); //Write to the Console
		writeToFile(formattedMessage); //Write to the file
	}
	private synchronized void log(Level level, String formattedMessage) throws Exception {
	    System.out.println(formattedMessage);
	    writeToFile(formattedMessage);
	    writeToDatabase(level, formattedMessage);
	}

	public void info(String message) throws Exception {
		log(Level.INFO, formatLogMessage(Level.INFO, message));
	}

	public void warn(String message) throws Exception {
		log(Level.WARN, formatLogMessage(Level.WARN, message));
	}

	public void error(String message) throws Exception{
		log(Level.ERROR, formatLogMessage(Level.ERROR, message));
	}

	public void debug(String message) throws Exception{
		log(Level.DEBUG, formatLogMessage(Level.DEBUG, message));
	}

	public void trace(String message) throws Exception{
		log(Level.TRACE, formatLogMessage(Level.TRACE, message));
	}


}