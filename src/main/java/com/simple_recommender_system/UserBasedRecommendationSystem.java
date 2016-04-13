package com.simple_recommender_system;

import java.io.File;
import java.util.List;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class UserBasedRecommendationSystem {

	public static void main(String[] args) {		
		try{				

			/* Converting the CSV File data present in the form of (User, Item, Rating) into Data Model */
			DataModel dm = new FileDataModel(new File("resources/data_sets/PreprocessedMCDataForMahout.csv"));
			
			System.out.println("DataModel: " + dm);
			
			/* In this recommendation system we will be using Pearson Correlation Method for calculating the Similarity */
			UserSimilarity similarity = new PearsonCorrelationSimilarity(dm);

			/* Here I define the neighborhood range, from which recommendation system will recommend items based on the items liked by similar users */
			UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, dm);
			
			UserBasedRecommender recommender = new GenericUserBasedRecommender(dm, neighborhood, similarity);

			/* Here I am trying to recommend items for the user with User Id to 2 and number of items as 3*/
			List<RecommendedItem> recommendations = recommender.recommend(13, 5);
			
			long[] similarUserIds = recommender.mostSimilarUserIDs(13, 5);
			
			/* Display all the items recommended to the user with User Id 2 */
			for (RecommendedItem recommendation : recommendations) {
			  System.out.println(recommendation);
			}
			
			for(long userId : similarUserIds)	{
				System.out.println(userId);
			}
			
		}catch (Exception e) {
			System.out.println("There was an error.");
			e.printStackTrace();
		}				
	}
}
