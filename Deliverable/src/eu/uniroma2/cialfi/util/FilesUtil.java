package eu.uniroma2.cialfi.util;

import java.io.File;

public class FilesUtil{

	public static void ListFiles(File dir) {
		for (File file : dir.listFiles()) {
			if (!file.isDirectory()) {
				System.out.println(file.getName());
			} else {
				ListFiles(file);
			}
		}
		return;

	}
}
