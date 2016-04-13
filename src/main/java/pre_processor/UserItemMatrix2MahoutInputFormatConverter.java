package pre_processor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class UserItemMatrix2MahoutInputFormatConverter {

	public static void main(String[] args) {
		String csvFile = "resources/data_sets/PreProcessing-MeanCentering.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {

			        // use comma as separator
				String[] userData = line.split(cvsSplitBy);

				for(int i=1;i<userData.length;i++){
					if(userData[i].equals("0")) continue;
					System.out.print(userData[0]+",");
					System.out.print((i)+",");
					System.out.print(userData[i]);
					System.out.println();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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

		System.out.println("Done");
	}
}
