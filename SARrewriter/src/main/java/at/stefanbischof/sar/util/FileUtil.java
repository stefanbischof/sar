package at.stefanbischof.sar.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Convenience methods for handling files
 * 
 * @author z003354t
 *
 */
public class FileUtil {

	/**
	 * Write a string to a file
	 * 
	 * @param filename
	 * @param contents
	 * @throws IOException
	 */
	public static void writeFile(String filename, String contents)
			throws IOException {
		Files.write(Paths.get(filename),
				contents.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Read a file to a string
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static String readFile(String filename) throws IOException {
		StringBuilder sb = new StringBuilder();

		for (String line : Files.readAllLines(Paths.get(filename),
				StandardCharsets.UTF_8)) {
			sb.append(line);
			sb.append(System.lineSeparator());
		}

		return sb.toString();
	}

	/**
	 * Remove file name extension of a file name
	 * 
	 * @param filename
	 * @return
	 */
	public static String removeFileExtension(final String queryFileName) {
		return queryFileName.contains(".") 
				? queryFileName.substring(0,queryFileName.lastIndexOf('.')) 
				: queryFileName;
	}

}
