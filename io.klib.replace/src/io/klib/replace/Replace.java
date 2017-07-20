package io.klib.replace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Scanner;

public class Replace {

	private static final String FILE_TO_TOUCH = ".project";
	public final static String ERROR_START = "[java] ### files with lower versions [ERROR]";
	public final static String ERROR_END = "[java] done.";

	public final static String buildlog = "C:/_workbench/ide/cec-171-neon/ws/dev_171/io.klib.replace/data/build-1478088458795.log";
	public final static String workspace = "C:/_workbench/ide/cec-171-neon/ws/dev_171";

	public static void main(String[] args) {

		FileInputStream inputStream = null;
		Scanner sc = null;
		boolean gather = false;
		HashSet<Path> files2touch = new HashSet<Path>();

		try {
			inputStream = new FileInputStream(new File(buildlog));
			sc = new Scanner(inputStream, "UTF-8");
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (line.contains(ERROR_END)) {
					// stop collection problems
					gather = false;
				}
				if (gather) {
					String directoryName = line.replaceAll(".*?\\[java\\] file ", "").split("_")[0];
					File dir = Paths.get(workspace, directoryName).toFile();
					if (dir.exists() && dir.isDirectory()) {
						files2touch.add(Paths.get(workspace, directoryName, FILE_TO_TOUCH));
					} else {
						String bndProjectName = directoryName.substring(0, directoryName.lastIndexOf("."));
						dir = Paths.get(workspace, bndProjectName).toFile();
						if (dir.exists() && dir.isDirectory()) {
							files2touch.add(Paths.get(workspace, bndProjectName, FILE_TO_TOUCH));
						}
					}
				}
				if (line.contains(ERROR_START)) {
					// start collection problems
					gather = true;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		files2touch.forEach(filePath -> {
			System.out.println("modifying " + filePath);
			try {
				FileWriter fw = new FileWriter(filePath.toFile(), true);
				fw.write("\n");
				fw.close();
			} catch (IOException ioe) {
				System.err.println("IOException: " + ioe.getMessage());
			}
		});

	}

}
