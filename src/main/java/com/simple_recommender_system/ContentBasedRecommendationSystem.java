package com.simple_recommender_system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;

import org.grouplens.lenskit.ItemRecommender;
import org.grouplens.lenskit.ItemScorer;
import org.grouplens.lenskit.Recommender;
import org.grouplens.lenskit.RecommenderBuildException;
import org.grouplens.lenskit.core.LenskitConfiguration;
import org.grouplens.lenskit.core.LenskitRecommender;
import org.grouplens.lenskit.data.dao.EventDAO;
import org.grouplens.lenskit.data.dao.ItemDAO;
import org.grouplens.lenskit.data.dao.UserDAO;
import org.grouplens.lenskit.scored.ScoredId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple_recommender_system.cbf.TFIDFItemScorer;
import com.simple_recommender_system.cbf.dao.CSVItemTagDAO;
import com.simple_recommender_system.cbf.dao.MOOCRatingDAO;
import com.simple_recommender_system.cbf.dao.MOOCUserDAO;
import com.simple_recommender_system.cbf.dao.RatingFile;
import com.simple_recommender_system.cbf.dao.TagFile;
import com.simple_recommender_system.cbf.dao.TitleFile;
import com.simple_recommender_system.cbf.dao.UserFile;

/**
 * Simple hello-world program.
 * 
 * @author <a href="http://www.grouplens.org">GroupLens Research</a>
 */
public class ContentBasedRecommendationSystem {
	private static final Logger logger = LoggerFactory.getLogger(ContentBasedRecommendationSystem.class);

	public static void main(String[] args) throws RecommenderBuildException {
		try {
			LenskitConfiguration config = configureRecommender();

			logger.info("building recommender");
			Recommender rec = LenskitRecommender.build(config);

			/*
			 * if (args.length == 0) { logger.error(
			 * "No users specified; provide user IDs as command line arguments"
			 * ); }
			 */
			String csvFile = "resources/data_sets/movieid-movie.csv";
			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = ",";
			HashMap<Long, String> movieMap = new HashMap<Long, String>();

			br = new BufferedReader(new FileReader(csvFile));

			while ((line = br.readLine()) != null) {
				String[] movieData = line.split(cvsSplitBy);
				movieMap.put(Long.parseLong(movieData[0]), movieData[1]);
			}

			// we automatically get a useful recommender since we have a scorer
			ItemRecommender irec = rec.getItemRecommender();
			assert irec != null;
			
			try {
				// Generate 5 recommendations for each user
				long userId = 13;
				logger.info("searching for recommendations for user {}", userId);
				List<ScoredId> recs = irec.recommend(userId, 5);
				
				if (recs.isEmpty()) {
					logger.warn("no recommendations for user {}, do they exist?", userId);
				}
				
				System.out.format("recommendations for user %d:\n", userId);
				
				for (ScoredId id : recs) {
					System.out.format("  %s: %.4f\n", movieMap.get(id.getId()), id.getScore());
				}
			} catch (UnsupportedOperationException e) {
				if (e.getMessage().equals("stub implementation")) {
					System.out.println("Congratulations, the stub builds and runs!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the LensKit recommender configuration.
	 * 
	 * @return The LensKit recommender configuration.
	 */
	// LensKit configuration API generates some unchecked warnings, turn them
	// off
	@SuppressWarnings("unchecked")
	private static LenskitConfiguration configureRecommender() {
		LenskitConfiguration config = new LenskitConfiguration();
		// configure the rating data source
		config.bind(EventDAO.class).to(MOOCRatingDAO.class);
		config.set(RatingFile.class).to(new File("resources/data_sets/PreprocessedDataForMahout.csv"));

		// use custom item and user DAOs
		// specify item DAO implementation with tags
		config.bind(ItemDAO.class).to(CSVItemTagDAO.class);
		// specify tag file
		config.set(TagFile.class).to(new File("resources/data_sets/movie-tags.csv"));
		// and title file
		config.set(TitleFile.class).to(new File("resources/data_sets/movieid-movie.csv"));

		// our user DAO can look up by user name
		config.bind(UserDAO.class).to(MOOCUserDAO.class);
		config.set(UserFile.class).to(new File("resources/data_sets/users.csv"));

		// use the TF-IDF scorer you will implement to score items
		config.bind(ItemScorer.class).to(TFIDFItemScorer.class);
		return config;
	}
}