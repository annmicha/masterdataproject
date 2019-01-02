package masterwebdataproject.clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;

import masterwebdataproject.model.*;

public class DogfoodMultipleClustering {
	public static HashSet<DogfoodMulti> dogList;
	public static HashSet<DMulti_Cluster> clusterList;
	public static HashSet<String> clusterCheck;
	public static HashSet<DMulti_Cluster> valuesToAdd;
	public static HashSet<DMulti_Cluster> valuesToRemove;

	public static void clearSets() {
		dogList = new HashSet<DogfoodMulti>();
		clusterList = new HashSet<DMulti_Cluster>();
		clusterCheck = new HashSet<String>();
		valuesToAdd = new HashSet<DMulti_Cluster>();
		valuesToRemove = new HashSet<DMulti_Cluster>();
	}

	// CSV file with all dogfood offers is read and parsed into a HashSet<Dogfood>
	public static void readDataLineByLine(String file) {
		BufferedReader br = null;
		String line = "";

		// prevents from reading headers
		int count = 0;

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));

			while ((line = br.readLine()) != null) {
				if (count > 0) {
					String[] array = line.split(",");

					if (array.length == 10) {

						// 0 > offerID
						// 1 > name
						// 2 > brand
						// 3 > description
						// 4-6 > idValues
						// 7-8 > price and currency
						// 9 > file name

						HashSet<String> temp = new HashSet<String>();

						for (int counter = 4; counter <= 6; counter++) {
							if (!array[counter].isEmpty()) {
								temp.add(array[counter].replaceAll("\\s", ""));
							}

						}

						// DogfoodMulti (String identifier, String name, HashSet<String> set){

						dogList.add(new DogfoodMulti(array[0], array[1], temp));

					}
				}

				count++;
			}
//			}

			System.out.println("[INFO] File has been processed.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	// prints given HashSet
	public static void printHashSet(HashSet<?> h) {
		Iterator<?> it = h.iterator();
		while (it.hasNext()) {
			System.out.println(it.next());
		}
	}

	// Intatiate first round of clusters by looking at each dogfood in the list and
	// create a cluster for it
	public static void createCluster(HashSet<DogfoodMulti> dogList) {
		int count = 0;
		Iterator<DogfoodMulti> it = dogList.iterator();
		while (it.hasNext()) {
			DMulti_Cluster c = new DMulti_Cluster(it.next());
			if ((c.getIDGenerator() == 0) & (count == 0)) {
				c.restartInteger();
				count++;
			}
			clusterList.add(c);
		}
	}

	// merge both clusters into one
	public static DMulti_Cluster merge(DMulti_Cluster c1, DMulti_Cluster c2) {
		DMulti_Cluster c = new DMulti_Cluster();

		Iterator<DogfoodMulti> it1 = c1.getList().iterator();
		while (it1.hasNext()) {
			c.addItem(it1.next());
			c.updateIDValues();
		}
		Iterator<DogfoodMulti> it2 = c2.getList().iterator();
		while (it2.hasNext()) {
			c.addItem(it2.next());
			c.updateIDValues();
		}

		return c;
	}

	public static boolean hasBeenTested(DMulti_Cluster c1, DMulti_Cluster c2) {
		String s1 = c1.getClusterId() + "" + c2.getClusterId();
		String s2 = c2.getClusterId() + "" + c1.getClusterId();
		boolean found = false;

		// only continue if not identical
		if (c1.getClusterId() != c2.getClusterId()) {

			// check if c1 and c2 has been checked yet
			if (clusterCheck.contains(s1)) {
				found = true;
			} else if (clusterCheck.contains(s2)) {
				found = true;
			} else {
				found = false;
			}
		} else {
			found = true;
		}
		return found;
	}

	public static boolean findOverlap(DMulti_Cluster c1, DMulti_Cluster c2) {

		// eventuell auskommentieren
		clusterCheck.add(c1.getClusterId() + "" + c2.getClusterId());
		clusterCheck.add(c2.getClusterId() + "" + c1.getClusterId());

		boolean overlap = false;

		// get the corresponding idValues for both clusters
		HashSet<String> h1 = c1.getIDValues();
		HashSet<String> h2 = c2.getIDValues();

		for (String id : h1) {
			if ((id != "") && h2.contains(id)) {
				overlap = true;
				System.out.println(id + " has been found.");
			}
		}
		return overlap;
	}

	// Compares clustersfrom clusterList, only if their are distinct and have not
	// been compared in the same round before
	public static void findClusters() {
		clusterCheck = new HashSet<String>();
		valuesToAdd = new HashSet<DMulti_Cluster>();
		valuesToRemove = new HashSet<DMulti_Cluster>();

		Iterator<DMulti_Cluster> it1 = clusterList.iterator();
		int count = 0;

		while (it1.hasNext()) {
			DMulti_Cluster c1 = it1.next();

			Iterator<DMulti_Cluster> it2 = clusterList.iterator();

			count++;

			if (!c1.getHasBeenMatched()) {
				while (it2.hasNext()) {
					DMulti_Cluster c2 = it2.next();

					Iterator<String> iValue1 = c1.getIDValues().iterator();
					Iterator<String> iValue2 = c2.getIDValues().iterator();

					if ((iValue2.hasNext()) && (iValue1.hasNext())) {
						String s1 = iValue1.next();
						String s2 = iValue2.next();

						if ((s1.length() > 2) && (s2.length() > 2)) {
							char char_1_1 = s1.charAt(0);
							char char_2_1 = s2.charAt(0);
							char char_1_2 = s1.charAt(1);
							char char_2_2 = s2.charAt(1);

							// compares only if different clusters
							// and number starts with the same digit

							if ((char_1_1 == char_2_1) && (char_1_2 == char_2_2) && (c1.getID() != c2.getID())) {
//							
								if ((!c2.getHasBeenMatched())) {
									boolean test = findOverlap(c1, c2);

									if (test) {
										DMulti_Cluster new_cluster = merge(c1, c2);
										valuesToAdd.add(new_cluster);
										valuesToRemove.add(c1);
										valuesToRemove.add(c2);
										c1.setHasBeenMatched(true);
										c2.setHasBeenMatched(true);
									}
								}
							}
						}
					}
				}
			}
		}
		clusterList.removeAll(valuesToRemove);
		clusterList.addAll(valuesToAdd);

	}

	public static void clustering() {
		int size_before = clusterList.size();
		int size_after = 0;
		int count = 1;

		while (size_before != size_after) {

			System.out.println("\n***** Find Clusters Round " + count);

			size_before = clusterList.size();

			findClusters();
			for (DMulti_Cluster c : clusterList) {
				c.setHasBeenMatched(false);
			}

			size_after = clusterList.size();
			System.out.println("Clustersize now: " + size_before + " / " + size_after);
			count++;

		}
	}
	
	public static void printFile(String filepath) {
		
		PrintWriter pw;
		try {
			pw = new PrintWriter(new File(filepath));
			StringBuilder sb = new StringBuilder();
			sb.append("clusterID");
			sb.append(',');
			sb.append("Values");
			sb.append(',');
			sb.append("Size");
			sb.append(',');
			sb.append("DogIDs");
			sb.append('\n');
			for (DMulti_Cluster c : clusterList) {
				sb.append(c.toString());
				sb.append("\n");
			}

			pw.write(sb.toString());
			pw.close();
			System.out.println("done!");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public static void main(String[] args) {
		// resets all HashSets
		clearSets();

		// reads file into Dogfood HashSet
		System.out.println("\n***** New File Read *********\n");
		readDataLineByLine("data/input/dogfood_Dec5.csv");

		// turns dogfood items into single clusters
		System.out.println("\n***** Initiate Clusters *********\n");
		createCluster(dogList);

		// iterates over cluster set until stop criteria is met (number of clusters does
		// not change)
		System.out.println("\n***** Start Clustering *********\n");
		clustering();

		System.out.println("\n***** PrintFiles *********\n");
		String outputpath = "data/output/clusters_dogfood.csv";
		printFile(outputpath);

	}
}
