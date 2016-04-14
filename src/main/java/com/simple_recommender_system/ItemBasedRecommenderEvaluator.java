package com.simple_recommender_system;

import java.io.File;
import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class ItemBasedRecommenderEvaluator {

	public static void main(String args[]) throws TasteException, IOException {
		DataModel dm = new FileDataModel(new File("resources/data_sets/PreprocessedMCDataForMahout.csv"));
		RecommenderBuilder builder = new RecommenderBuilder() {
			public Recommender buildRecommender(DataModel model) throws TasteException {
				
				//Step 1 :- Create ItemSimilarity Matrix
				ItemSimilarity similarity = new PearsonCorrelationSimilarity(model);
				
				
				//Step 2 :- Create object of ItemBasedRecommender
				GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(model,similarity);
				
				return recommender;
				//Step 3:- build and return the Recommender to evaluate here
			}
		};
		 //Step 4:- Use average absolute difference error metric
		RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
		double evaluation = evaluator.evaluate(builder,null, dm, 0.6, 0.4);
		System.out.println("error rate:"+evaluation);

	}

}
