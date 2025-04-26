package io.github.vdaburon.jmeter.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 
 * @author Vincent Daburon
 */
public class HtmlGraphVisualizationGenerator {
	// CRLF ou LF ou CR
	public static final String LINE_SEP = System.getProperty("line.separator");

	public static final String K_TABLE_CONTENTS = "@@TABLE_OF_CONTENTS@@";
	
	public static void main(String[] args) {
		
		if (args.length != 2) {
			System.err.println("Usage HtmlGraphVisualizationGenerator dirWithFiles fileOutHtml");
			System.err.println("E.g. java -Dimage_width=1024-Dadd_toc=true -jar create-html-for-files-in-directory-<version>.jar c:/jmeter/dir_results index.html");
			System.exit(1);
		}
		String dirWithFiles = args[0];
		String fileOutHtml =args[1];

		String sImageWidth = System.getProperty("image_width");
		int imageWidth = 1024;
		if (sImageWidth != null) {
			try {
				imageWidth = Integer.parseInt(sImageWidth);
				System.out.println("image_width : " + imageWidth);
			}catch (NumberFormatException ex) {
				System.err.println("Can't parse this integer : <" + sImageWidth + ">, default image_width value = 1024");
			}
		}

		String sIsAddToc = System.getProperty("add_toc");
		boolean isAddToc = true;
		if (sIsAddToc != null) {
			isAddToc = Boolean.parseBoolean(sIsAddToc);
		}

		File fDirWithFiles = new File(dirWithFiles);
		File fileIndex = new File(dirWithFiles + "/" + fileOutHtml);
		if (fileIndex.exists()) {
			System.out.println("Delete previous file : " + fileIndex.getName());
			fileIndex.delete();
		}
		
		Object[] tabFiles = listFileOrderByName(fDirWithFiles);
		ArrayList listTitles = new ArrayList<>();
		
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(new File(dirWithFiles + "/" + fileOutHtml)));

			System.out.println("Generating " + fileOutHtml + " ...");

			out.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
			out.write(LINE_SEP);
			out.write("<html>");
			out.write(LINE_SEP);
			out.write("<head>");
			out.write(LINE_SEP);
			out.write(LINE_SEP);
			out.write("<title>html page generated</title>");
			out.write(LINE_SEP);
			out.write("</head>");
			out.write(LINE_SEP);
			out.write("<body><br/><br/>");
			out.write(LINE_SEP);

			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH'h'mm'm'ss's'", Locale.getDefault());
			String sDate = dateformat.format(new Date());
			
			out.write("<h1> Generated date " + sDate + "</h1><br/>");
			out.write(LINE_SEP);

			if (isAddToc) {
				out.write(K_TABLE_CONTENTS + "<br/>");
				out.write(LINE_SEP);
				System.out.println("With a Table Of Contents");
			}

			for (int i = 0; i < tabFiles.length; i++) {
				File f = (File) tabFiles[i];
				
				String name = f.getName().toLowerCase();
				if (name.endsWith("csv") || name.endsWith("jtl") || name.endsWith("xml") ||name.endsWith("gz")  || name.endsWith("zip") || name.endsWith("log")
						|| name.endsWith("xlsx") || name.endsWith("xls")
						|| name.endsWith("gif") || name.endsWith("png") || name.endsWith("bmp") || name.endsWith("jpg") || name.endsWith("jpeg")
						|| name.endsWith("html")) {
					
					// folderRead = c:\dir1\dir2\dirIn, f =  c:\dir1\dir2\dirIn\logo.gif => nameRelative = logo.gif (remove the folderRead path)
					String nameRelative = f.getCanonicalPath().substring(fDirWithFiles.getCanonicalPath().length() + 1);
					out.write("<p><a name=\"" + i + "\"></a></p>");
					out.write(LINE_SEP);
					out.write("<h2>" + nameRelative + "</h2><br/>");
					out.write(LINE_SEP);
					listTitles.add(nameRelative);

					if (name.endsWith("csv") || name.endsWith("jtl") || name.endsWith("xml") ||name.endsWith("gz") || name.endsWith("zip") || name.endsWith("log") ||
							name.endsWith("xlsx") || name.endsWith("xls")) {
						long lengthBytes = f.length();
						DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
						symbols.setGroupingSeparator(' ');

						DecimalFormat formatter = new DecimalFormat("###,###.##", symbols);
						// link to the file
						out.write("<a href='" + nameRelative + "'>" + nameRelative + "</a>&nbsp;&nbsp;&nbsp;File size=" + formatter.format(lengthBytes) + " Bytes<br/><br/>");
					}
										
					if (name.endsWith("gif") || name.endsWith("png") || name.endsWith("bmp") || name.endsWith("jpg") || name.endsWith("jpeg")) {
						// image
						out.write("<img src='" + nameRelative + "' width=\"" + imageWidth + "\"><br/><br/>");
						out.write(LINE_SEP);
					}
				
					if (name.endsWith("html")) {
						// include the html content directly in the result for the Synthesis Report
						String htmlPage = readAllFileToString(f.getAbsolutePath());
						out.write(htmlPage);
						out.write(LINE_SEP);
					}
				} // end all endsWith if
			} // end for

			out.write(LINE_SEP);
			out.write("</body>");
			out.write(LINE_SEP);
			out.write("</html>");

			System.out.println("Done!");

		} catch (final Exception e1) {
			System.out.println(e1.getMessage());

		} finally {
			if (null != out) {
				try {
					out.close();
				} catch (IOException e2) {
					System.out.println(e2.getMessage());
				}
			}
		}

 		if (isAddToc) {
			try {
				replaceTableOfContentsByTitle(fileIndex, listTitles);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
    }
	
	@SuppressWarnings("unchecked")
	public static Object[] listFileOrderByName(File folderRead) {
		ArrayList<File> aList = new ArrayList<File>();
		
		findAllFilesInDirectory(folderRead, aList);
		Object[] tabFiles = aList.toArray();
		
		Arrays.sort(tabFiles , new FileNameComparator());
		return tabFiles;
	}

	private static String readAllFileToString(String fileName) {
		String content = "";

		try {
			content = new String(Files.readAllBytes(Paths.get(fileName)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return content;
	}

	public static void findAllFilesInDirectory(File directory, List<File> files) {
	    // Get all files from a directory.
	    File[] fList = directory.listFiles();
	    if(fList != null) {
	        for (File file : fList) {      
	            if (file.isFile()) {
	                files.add(file);
	            } else if (file.isDirectory()) {
	            	findAllFilesInDirectory(file, files);
	            }
	        }
	    }
	}

	public static void replaceTableOfContentsByTitle(File fileIndex, ArrayList <String> listTitles) throws IOException {
		String allIndex = readAllFileToString(fileIndex.getCanonicalPath());
		StringBuffer sbTitles = new StringBuffer(2048);
		sbTitles.append("<h2>Table Of Contents</h2>" + LINE_SEP);
		sbTitles.append("<h4>" + LINE_SEP);
		for (int i = 0; i < listTitles.size(); i++) {
			sbTitles.append("<a href=\"#" + i + "\">");
			sbTitles.append(listTitles.get(i));
			sbTitles.append("</a></br>" + LINE_SEP);
		}
		int position = allIndex.indexOf(K_TABLE_CONTENTS);
		StringBuffer sbNewIndex = new StringBuffer(128 * 1024);
		if (position > 1) {
			sbNewIndex.append(allIndex.substring(0,position));
			sbNewIndex.append(sbTitles.toString());
			sbNewIndex.append(allIndex.substring((position + K_TABLE_CONTENTS.length()), allIndex.length()));
		}
		sbTitles.append("</h4>" + LINE_SEP);

		if (fileIndex.exists()) {
			fileIndex.delete();
		}
		BufferedWriter out = new BufferedWriter(new FileWriter(fileIndex));
		out.write(sbNewIndex.toString());
		out.close();
	}
}

