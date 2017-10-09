/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2015 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package playground.ikaddoura.analysis.tables;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.matsim.core.utils.io.IOUtils;

/**
 * 
 * Merges all csv files of the same name and format
 * 
 * file 1:
 * key ; value1
 * 
 * file 2:
 * key ; value2
 * 
 * to one file of the format
 * 
 * key ; value1 ; value2
 * 
 * 
 * 
* @author ikaddoura
*/

public class MergeCSVFiles {
	
	private static final Logger log = Logger.getLogger(MergeCSVFiles.class);
	
	private static final String directory = "/Users/ihab/Desktop/ils4i/kaddoura/optAV/output/";
	private static final String fileName = "aggregated_info_car";
	private static final String separator = ";";
	
	private static TreeMap<String, LinkedHashMap<String, String>> path2key2Value = new TreeMap<>();
	
	public static void main(String[] args) {
		
		List<File> fileList = new ArrayList<>();
		collectAllFilesInDirectory(new File(directory), fileList);

		for (File f : fileList) {
			
			if (f.getName().endsWith(".csv") && f.getName().startsWith(fileName)) {
				
				log.info("*** file: " + f.getPath());
				
				LinkedHashMap<String, String> key2Value = new LinkedHashMap<>();
				
				BufferedReader br = IOUtils.getBufferedReader(f.getPath());
				String line;

				try {
					while( (line = br.readLine()) != null){

						String[] columns = line.split(separator);
						String key = null;
						String value = null;

						for(int i = 0; i < columns.length; i++){
							switch(i){
							case 0: key = columns[i];
							break;
							case 1: value = columns[i];
							break;
							default: throw new RuntimeException("More than two columns. Aborting...");
							}
						}
						
						if (!key.isEmpty()) key2Value.put(key, value);
					}
					
				} catch (NumberFormatException | IOException e) {
					e.printStackTrace();
				}
				
				if (path2key2Value.containsKey(f.getPath())) throw new RuntimeException("Multiple files in same directory path. Aborting...");
				
				path2key2Value.put(f.getPath(), key2Value);
			}
		}
			
		if (path2key2Value.size() > 0) {
			
			String outputFile = directory + "merged_" + fileName + ".csv";

			try ( BufferedWriter bw = IOUtils.getBufferedWriter(outputFile) ) {

				log.info(" Writing merged file to " + outputFile + "...") ;

				bw.write("path");
				for (String path : path2key2Value.keySet()) {
					bw.write(separator + path);						
				}
				bw.newLine();
				
				for (String key : path2key2Value.firstEntry().getValue().keySet()) {

					bw.write(key);					
					for (String path : path2key2Value.keySet()) {						
						bw.write(separator + path2key2Value.get(path).get(key));						
					}
					bw.newLine();
				}
				
				bw.close();

			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		} else {
			throw new RuntimeException("Nothing to write out. File pattern " + fileName + ".csv not found in directory " + directory + ".");
		}
		
		log.info("Done.");
		
	}
	
	private static List<File> collectAllFilesInDirectory(File file, List<File> fileList) {
		log.info("File: " + file);
		File[] files = file.listFiles();
		
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					collectAllFilesInDirectory(f, fileList);
				} else {
					fileList.add(f);
				}
			}
		}
		return fileList;
	}

}

