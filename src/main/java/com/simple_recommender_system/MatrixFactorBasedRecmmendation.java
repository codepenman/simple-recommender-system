package com.simple_recommender_system;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import scala.Tuple2;

import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import org.apache.spark.SparkConf;

public class MatrixFactorBasedRecmmendation {
	public static void main(String[] args) {
	    // $example on$
	    SparkConf conf = new SparkConf().setAppName("Java Collaborative Filtering Example").setMaster("local");
	    JavaSparkContext jsc = new JavaSparkContext(conf);

	    // Load and parse the data
	    String path = "resources/data_sets/PreprocessedDataForMahout.csv";
	    JavaRDD<String> productData = jsc.textFile("resources/data_sets/movieid-movie.csv");
	    JavaRDD<String> data = jsc.textFile(path);
	    JavaRDD<Tuple2<Integer, Rating>> ratings = data.map(
	      new Function<String, Tuple2<Integer, Rating>>() {
	        public Tuple2<Integer, Rating> call(String s) {
	          String[] sarray = s.split(",");
              Integer cacheStamp = (int) ((Math.random()*10) % 10);
              Rating rating=new Rating(Integer.parseInt(sarray[0]), Integer.parseInt(sarray[1]),
      	            Double.parseDouble(sarray[2]));
	          return new Tuple2<Integer, Rating>(cacheStamp, rating);
	        }
	      }
	    );
	    
	  //Movies file should be csv file in a (MovieID,Title) format
	    //Keep this block as it is
	    Map<Integer, String> products = productData.mapToPair(
	            new PairFunction<String, Integer, String>() {
	                public Tuple2<Integer, String> call(String s) throws Exception {
	                    String[] sarray = s.split(",");
	                    return new Tuple2<Integer, String>(Integer.parseInt(sarray[0]), sarray[1]);
	                }
	            }
	    ).collectAsMap();
	    
	    
	  //training data set
	    // below function generate training data from input data 
	    // keep other things as it is
	    JavaRDD<Rating> training = ratings.filter(
	            new Function<Tuple2<Integer, Rating>, Boolean>() {
	                public Boolean call(Tuple2<Integer, Rating> tuple) throws Exception {
	                    return tuple._1() < 6;
	                    // write your logic to create training data set based on timestamp from input dataset
	                }
	            }
	    ).map(
	            new Function<Tuple2<Integer, Rating>, Rating>() {
	                public Rating call(Tuple2<Integer, Rating> tuple) throws Exception {
	                    return tuple._2();
	                }
	            }
	    ).cache();


	    //validation data set
	    // below function generate validation data from input data 
	    // keep other things as it is
	    JavaRDD<Rating> validation = ratings.filter(
	            new Function<Tuple2<Integer, Rating>, Boolean>() {
	                public Boolean call(Tuple2<Integer, Rating> tuple) throws Exception {
	                    return tuple._1() >= 6 && tuple._1() < 8;
	                    
	                }
	            }
	    ).map(
	            new Function<Tuple2<Integer, Rating>, Rating>() {
	                public Rating call(Tuple2<Integer, Rating> tuple) throws Exception {
	                    return tuple._2();
	                }
	            }
	    );

	    //test data set
	    // below function generate validation data from input data 
	    // keep other things as it is
	    JavaRDD<Rating> test = ratings.filter(
	            new Function<Tuple2<Integer, Rating>, Boolean>() {
	                public Boolean call(Tuple2<Integer, Rating> tuple) throws Exception {
	                    return tuple._1() >= 8;
	                   
	                }
	            }
	    ).map(
	            new Function<Tuple2<Integer, Rating>, Rating>() {
	                public Rating call(Tuple2<Integer, Rating> tuple) throws Exception {
	                    return tuple._2();
	                }
	            }
	    );

	    
	    
	    

	    // Build the recommendation model using ALS
	    int rank = 8;
	    int numIterations = 10;
	    MatrixFactorizationModel model = ALS.train(JavaRDD.toRDD(training), rank, numIterations, 0.01);

	    // Evaluate the model on rating data
	    JavaRDD<Tuple2<Object, Object>> userProducts = validation.map(
	      new Function<Rating, Tuple2<Object, Object>>() {
	        public Tuple2<Object, Object> call(Rating r) {
	          return new Tuple2<Object, Object>(r.user(), r.product());
	        }
	      }
	    );
	    JavaPairRDD<Tuple2<Integer, Integer>, Double> predictions = JavaPairRDD.fromJavaRDD(
	      model.predict(JavaRDD.toRDD(userProducts)).toJavaRDD().map(
	        new Function<Rating, Tuple2<Tuple2<Integer, Integer>, Double>>() {
	          public Tuple2<Tuple2<Integer, Integer>, Double> call(Rating r){
	            return new Tuple2<>(new Tuple2<>(r.user(), r.product()), r.rating());
	          }
	        }
	      ));
	    JavaRDD<Tuple2<Double, Double>> ratesAndPreds =
	      JavaPairRDD.fromJavaRDD(validation.map(
	        new Function<Rating, Tuple2<Tuple2<Integer, Integer>, Double>>() {
	          public Tuple2<Tuple2<Integer, Integer>, Double> call(Rating r){
	            return new Tuple2<>(new Tuple2<>(r.user(), r.product()), r.rating());
	          }
	        }
	      )).join(predictions).values();
	    double MSE = JavaDoubleRDD.fromRDD(ratesAndPreds.map(
	      new Function<Tuple2<Double, Double>, Object>() {
	        public Object call(Tuple2<Double, Double> pair) {
	          Double err = pair._1() - pair._2();
	          return err * err;
	        }
	      }
	    ).rdd()).mean();
	    System.out.println("Mean Squared Error = " + MSE);
	    
	    // Save and load model
/*	    model.save(jsc.sc(), "E:\\SJSU Stuff\\CMPE 239 - Erinaki\\Project 1\\mf");
	    MatrixFactorizationModel sameModel = MatrixFactorizationModel.load(jsc.sc(),
	      "E:\\SJSU Stuff\\CMPE 239 - Erinaki\\Project 1\\mf");*/
	    // $example off$
	    /*Rating r[]=sameModel.recommendProducts(292, 5);
	    //sameModel.
	    for (Rating movie: r){
	    	System.out.println(movie.toString());
	    }*/
	    
	  //###########   Implement below part    #################//

        // your logic for generating movie recommendation for particular user
        // Use methods provided by spark to generate recommendation for particular user

	    final int userId=292;
        //Getting the users ratings
        JavaRDD<Rating> userRatings = ratings.filter(
                new Function<Tuple2<Integer, Rating>, Boolean>() {
                    public Boolean call(Tuple2<Integer, Rating> tuple) throws Exception {
                        return tuple._2().user() == userId;
                    }
                }
        ).map(
                new Function<Tuple2<Integer, Rating>, Rating>() {
                    public Rating call(Tuple2<Integer, Rating> tuple) throws Exception {
                        return tuple._2();
                    }
                }
        );

        //Getting the product ID's of the products that user rated
        JavaRDD<Tuple2<Object, Object>> newuserProducts = userRatings.map(
                new Function<Rating, Tuple2<Object, Object>>() {
                    public Tuple2<Object, Object> call(Rating r) {
                        return new Tuple2<Object, Object>(r.user(), r.product());
                    }
                }
        );

        List<Integer> productSet = new ArrayList<Integer>();
        productSet.addAll(products.keySet());

        Iterator<Tuple2<Object, Object>> productIterator = newuserProducts.toLocalIterator();

        //Removing the user watched (rated) set from the all product set
        while(productIterator.hasNext()) {
            Integer movieId = (Integer)productIterator.next()._2();
            if(productSet.contains(movieId)){
                productSet.remove(movieId);
            }
        }

        JavaRDD<Integer> candidates = jsc.parallelize(productSet);

        JavaRDD<Tuple2<Integer, Integer>> userCandidates = candidates.map(
                new Function<Integer, Tuple2<Integer, Integer>>() {
                    public Tuple2<Integer, Integer> call(Integer integer) throws Exception {
                        return new Tuple2<Integer, Integer>(userId, integer);
                    }
                }
        );
        List<Rating> recommendations;
        //Predict recommendations for the given user
        recommendations = model.predict(JavaPairRDD.fromJavaRDD(userCandidates)).collect();

        //Sorting the recommended products and sort them according to the rating
        Collections.sort(recommendations, new Comparator<Rating>() {
            public int compare(Rating r1, Rating r2) {
                return r1.rating() < r2.rating() ? -1 : r1.rating() > r2.rating() ? 1 : 0;
            }
        });

        //get top 5 from the recommended products.
        recommendations = recommendations.subList(0, 5);
        for (Rating movie: recommendations){
	    	System.out.println(movie.toString());
	    }
	    
	    
	    
	    jsc.stop();
	  }

}
