package com.simple_recommender_system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public class ItemBasedRecommendationSystem {

	public static void main(String[] args) {		
		try{				
			//Step 1:- Input CSV file (CSV file should be in userID, itemID, preference) format
			DataModel model = new FileDataModel(new File("resources/data_sets/PreprocessedDataForMahout.csv"));

			String csvFile = "resources/data_sets/movieid-movie.csv";
			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = ",";
			HashMap<Integer, String> movieMap= new HashMap<Integer, String>();
			
			br = new BufferedReader(new FileReader(csvFile));
			
			while ((line = br.readLine()) != null) {
				String[] movieData = line.split(cvsSplitBy);
				movieMap.put(Integer.parseInt(movieData[0]), movieData[1]);				
			}

			//Step 2:- Create ItemSimilarity Matrix
			ItemSimilarity similarity = new PearsonCorrelationSimilarity(model);
						
			//Step 3:- Create object of ItemBasedRecommender
			GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(model,similarity);
			
			//Step 4:- Call the Generated Recommender in previous step to getting recommendation for particular user or Item			
			int userID=292;
			
			List<RecommendedItem> recommendations = recommender.recommend(userID, 3);

			System.out.println("Movies Being recommended for User:"+userID);
			
			for (RecommendedItem recommendation : recommendations) {
				System.out.println(movieMap.get((int)recommendation.getItemID()));
			}			
		}
		catch (Exception e) {
			System.out.println("There was an error.");
			e.printStackTrace();
		}		
		
		}
}