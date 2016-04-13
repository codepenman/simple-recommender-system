package pre_processor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MovieGenreMatrix2MahoutInputFormatConverter {

	public static void main(String[] args) {
		String csvFile = "resources/data_sets/Movie_Genre_Data_Set.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		
		try {
			int movieId = 1;
			br = new BufferedReader(new FileReader(csvFile));
			System.out.println(br.readLine());
			
			while ((line = br.readLine()) != null) {
			        // use comma as separator
				String[] movieGenreMatrix = line.split(cvsSplitBy);

				System.out.println(movieGenreMatrix[0]);

				for(int i=1;i<movieGenreMatrix.length;i++){
					if(movieGenreMatrix[i].equals("0")) continue;
					System.out.print((i)+",");
					System.out.print(movieId + ",");
					System.out.print(movieGenreMatrix[i]);
					System.out.println();
				}
				movieId++;
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
