package utils;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

public class Utils {
	public static boolean shouldLog = false;

	public static String caller() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		new Exception("Debug").printStackTrace(ps);
		String output = "";
		try {
			output = os.toString("UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return output;
	}

	public static void log(String message) {
		if (Utils.shouldLog ) {
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter("gamelog.txt", true));
				out.write(message);
				out.newLine();
				out.close();
			} catch (IOException e) {
			}
		}
	}

	public static void log(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		e.printStackTrace(pw);
		pw.flush();
		sw.flush();
		log(sw.toString());
	}
	
}
