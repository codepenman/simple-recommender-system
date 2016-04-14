package com.simple_recommender_system;

import java.io.*;
import java.util.*;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.*;
import org.apache.mahout.cf.taste.impl.neighborhood.*;
import org.apache.mahout.cf.taste.impl.recommender.*;
import org.apache.mahout.cf.taste.impl.similarity.*;
import org.apache.mahout.cf.taste.model.*;
import org.apache.mahout.cf.taste.neighborhood.*;
import org.apache.mahout.cf.taste.recommender.*;
import org.apache.mahout.cf.taste.similarity.*;

public class ItemBasedRecommendationSystem {

	public static void main(String[] args) {		
		try{	
			System.out.println("Hello world");
			
			//Step 1:- Input CSV file (CSV file should be in userID, itemID, preference) format

			DataModel model = new FileDataModel(new File("resources/data_sets/PreprocessedDataForMahout.csv"));
			
			/*Akshay
			 * Parsing Movied Id vs Movie Name file and creating a Hash Map so the we can output
			 * movie names instead of the movie Ids
			 * */
			String csvFile = "resources/data_sets/movieid-movie.csv";
			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = ",";
			HashMap<Integer, String> movieMap= new HashMap<Integer, String>();
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] movieData = line.split(cvsSplitBy);
				//System.out.println(movieData[0]+"---"+movieData[1]);
				movieMap.put(Integer.parseInt(movieData[0]), movieData[1]);
				
			}
			//Step 2:- Create ItemSimilarity Matrix
			ItemSimilarity similarity = new PearsonCorrelationSimilarity(model);
			
			
			//Step 3:- Create object of ItemBasedRecommender
			GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(model,similarity);
			
			//Step 4:- Call the Generated Recommender in previous step to getting 
			//recommendation for particular user or Item
			int userID=292;
			List<RecommendedItem> recommendations = recommender.recommend(userID, 3);
			System.out.println("Movies Being recommended for User:"+userID);
			for (RecommendedItem recommendation : recommendations) {
				//System.out.println((int)recommendation.getItemID());
				System.out.println(movieMap.get((int)recommendation.getItemID()));
			}
			/* Commenting this as it was for our own understanding
			List<RecommendedItem> similarItems = recommender.mostSimilarItems(1, 3);
			
			for (RecommendedItem recommendation : similarItems) {
				  System.out.println(recommendation);
			}
			*/
			
		}
		catch (Exception e) {
			System.out.println("There was an error.");
			e.printStackTrace();
		}		
		
		}
}