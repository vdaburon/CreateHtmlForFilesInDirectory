package io.github.vdaburon.jmeter.utils;

import java.io.File;
import java.util.Comparator;

public class FileNameComparator implements Comparator {
	/*
	 * Filenames with less directory deep are before file this directory
	 * Example : z.img < dir/x.img < dir/y.img < aa/bb/c.img < aa/bb/d.img
	 *           no dir, dir 1 deep level,      dir 2 deep level
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object obj1, Object obj2) {
		try {
			if (obj1 instanceof File) {
				String name1 = ((File) obj1).getCanonicalPath();
				String name2 = ((File) obj2).getCanonicalPath();
				
				int nbCharAntiSlash1 = countOccurenceOfAChar(name1, '\\');
				int nbCharSlash1 = countOccurenceOfAChar(name1, '/');
				
				int nbCharSeparDir1 = Math.max(nbCharAntiSlash1, nbCharSlash1);

				int nbCharAntiSlash2 = countOccurenceOfAChar(name2, '\\');
				int nbCharSlash2 = countOccurenceOfAChar(name2, '/');
				
				int nbCharSeparDir2 = Math.max(nbCharAntiSlash2, nbCharSlash2);
				
				if (nbCharSeparDir1 < nbCharSeparDir2) {
					return -1;
				}
				if (nbCharSeparDir1 > nbCharSeparDir2) {
					return 1;
				}
				
				return name1.compareTo(name2);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0;
	}
	
	int countOccurenceOfAChar(String someString, char someChar) {
		int count = 0;
		for (int i = 0; i < someString.length(); i++) {
			if (someString.charAt(i) == someChar) {
				count++;
			}
		}
		return count;
	}

}
