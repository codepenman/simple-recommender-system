package com.simple_recommender_system;

import java.io.File;
import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class UserBasedRecommenderEvaluator {

	public static void main(String args[]) throws TasteException, IOException {
		DataModel dm = new FileDataModel(new File("resources/data_sets/PreprocessedMCDataForMahout.csv"));
		RecommenderBuilder builder = new RecommenderBuilder() {
			public Recommender buildRecommender(DataModel dm) throws TasteException {
				
				//Step 1 :- Create UserSimilarity Matrix
				UserSimilarity similarity = new PearsonCorrelationSimilarity(dm);

				//Step 2 :- Build the user neighbourhood
				UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, dm);
				
				//Step 3 :- Build the recommender
				UserBasedRecommender recommender = new GenericUserBasedRecommender(dm, neighborhood, similarity);
				
				return recommender;
				//Step 4 :- build and return the Recommender to evaluate here
			}
		};
		//Step 5 :- build the evaluator and use Average absolute error metric
		RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
		double evaluation = evaluator.evaluate(builder,null, dm, 0.65, 0.35);
		System.out.println("error rate:"+evaluation);

	}

}
