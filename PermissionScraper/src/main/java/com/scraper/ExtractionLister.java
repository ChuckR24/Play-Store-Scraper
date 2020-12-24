package com.scraper;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
public class ExtractionLister {
	
	public static void main(String[] args) throws Exception {
		//BEFORE EACH RUN
		//Change directory it's searching
		//update input_ids
		//change numbers 01,12,02
		
		//turn input_ids into a list of apps
		LinkedHashMap<String, ArrayList<String>> appMap = new LinkedHashMap<String, ArrayList<String>>();
		
		try {
			Scanner scan = new Scanner(new File("input_ids"));
			
		    while(scan.hasNextLine()) {
		    	String id = scan.nextLine();
		      	appMap.put(id.trim()+".txt", new ArrayList<String>());
		    }
	    }catch(FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		
		
		//for each file Calendar, Contacts, etc.
		//store the name of the file, then iterate through all of the files in it
		//then search the list of apps for Apps with that line's id and add the folder name to their list of extracted permissions if it's not there already
		
		//should be 0,1 for just desc
		//0,2 for both
		//1,2 for just priv
		int from = 0;
		int to = 2;
		
		//this should be a folder with Descriptions, Privacy policy inside
		File extractedDataFolder = new File("R:\\DocumentsHDD\\CWRU Junior Year\\Semester 1 - Fall 2020\\Xiao Research\\week10\\Extracted Data");
		File[] DescAndPrivPol = extractedDataFolder.listFiles();
		for(int i = from; i < to; i++) {
			File[] permissionFolders = DescAndPrivPol[i].listFiles();
			//inside should be Calendar, Contacts, Location, ...
			for(File specificPermissionFolder : permissionFolders) {
				if(specificPermissionFolder.isDirectory()) {
					//inside Calendar folder
					File[] extractedPermissions = specificPermissionFolder.listFiles();
					for(File extractedPermission : extractedPermissions) {
						String appId = extractedPermission.getName();
						String permissionCategory = specificPermissionFolder.getName();
						
						ArrayList<String> appExtractedPermissions = appMap.get(appId);
						if (!appExtractedPermissions.contains(permissionCategory)) {
							appExtractedPermissions.add(permissionCategory);
							appMap.put(appId, appExtractedPermissions);
						}
					}
				}
			}
		}
		
		int permCount = 0;
		//print the appMap
		for(Entry<String, ArrayList<String>> entry : appMap.entrySet()) {
			
			//comment this out to get just the values
			//System.out.print(entry.getKey() + "\t\t\t");
			
			if(entry.getValue().size() > 0) {
				for(String perm: entry.getValue()){
					permCount++;
					System.out.print(perm + ",");
				}
			}else {
				System.out.print("None");
			}
			
			System.out.println("");
		}
		System.out.println("Permissions in this output: "+ permCount);
	}
}
