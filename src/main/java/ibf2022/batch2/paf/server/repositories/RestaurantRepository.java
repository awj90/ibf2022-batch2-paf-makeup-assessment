package ibf2022.batch2.paf.server.repositories;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.StringOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ibf2022.batch2.paf.server.models.Comment;
import ibf2022.batch2.paf.server.models.Restaurant;

@Repository
public class RestaurantRepository {

	private static final String RESTAURANT_COLLECTION_NAME="restaurants";
	private static final String COMMENTS_COLLECTION_NAME="comments";

	@Autowired
	private MongoTemplate mongoTemplate;

	// TODO: Task 2 
	// Do not change the method's signature
	// Write the MongoDB query for this method in the comments below
	// db.restaurants.distinct('cuisine');
	public List<String> getCuisines() {
		List<String> cuisines = mongoTemplate.findDistinct(new Query(), "cuisine", RESTAURANT_COLLECTION_NAME, String.class);
		return cuisines;
	}

	// TODO: Task 3 
	// Do not change the method's signature
	// Write the MongoDB query for this method in the comments below
	// 	db.restaurants.aggregate([ 
	//     {
	//         $match: {cuisine: "Asian"}
	//     },
	//     {
	//         $project: {
	//             restaurant_id: 1,
	//             name: 1 
	//         }
	//     }
	// ]).sort({
	//     name: 1,
	// });
	public List<Restaurant> getRestaurantsByCuisine(String cuisine) {
		MatchOperation matchOperation = Aggregation.match(Criteria.where("cuisine").is(cuisine));
		ProjectionOperation projectionOperation = Aggregation.project("restaurant_id", "name");
		Aggregation pipeline = Aggregation.newAggregation(matchOperation, projectionOperation);
		AggregationResults<Document> r = mongoTemplate.aggregate(pipeline, RESTAURANT_COLLECTION_NAME, Document.class);
		Iterator<Document> iterator = r.iterator();
		List<Restaurant> restaurants = new LinkedList<>();
		while (iterator.hasNext()) {
			restaurants.add(Restaurant.create(iterator.next(), true));
		}
		return restaurants;
	}
	
	// TODO: Task 4 
	// Do not change the method's signature
	// Write the MongoDB query for this method in the comments below
	// 	db.restaurants.aggregate([
	//     {
	//         $match: {restaurant_id: '40827287'}
	//     },
	//     {
	//         $lookup: {
	//             from: "comments",
	//             localField: "restaurant_id",
	//             foreignField: "restaurantId",
	//             as: "comments_for_this_restaurant",
	//         }
	//     },
	//     {
	//         $project: {
	//             _id: -1,
	//             restaurant_id: 1,
	//             name: 1,
	//             cuisine: 1,
	//             address: {
	//                 $concat: ["$address.building", ", ", "$address.street", ", ", "$address.zipcode", ", ", "$borough"]
	//             },
	//             comments: "$comments_for_this_restaurant",
	//         }
	//     }
	// ]);
	public Optional<Restaurant> getRestaurantById(String id) {
		MatchOperation matchOperation = Aggregation.match(Criteria.where("restaurant_id").is(id));
		LookupOperation lookupOperation = Aggregation.lookup(COMMENTS_COLLECTION_NAME, "restaurant_id", "restaurantId", "comments_for_this_restaurant");
		ProjectionOperation projectionOperation = Aggregation.project("restaurant_id", "name", "cuisine")
															.and(StringOperators.Concat.valueOf("address.building")
																				.concat(", ")
																				.concatValueOf("address.street")
																				.concat(", ")
																				.concatValueOf("address.zipcode")
																				.concat(", ")
																				.concatValueOf("borough")
																				).as("address")
															.and("comments_for_this_restaurant").as("comments")
															.andExclude("_id");
		Aggregation pipeline = Aggregation.newAggregation(matchOperation, lookupOperation, projectionOperation);
		AggregationResults<Document> r = mongoTemplate.aggregate(pipeline, RESTAURANT_COLLECTION_NAME, Document.class);
		Iterator<Document> iterator = r.iterator();
		if (!iterator.hasNext()) {
			return Optional.empty();
		}
		Document document = iterator.next();
		return Optional.of(Restaurant.create(document, false));
	}

	// TODO: Task 5 
	// Do not change the method's signature
	// Write the MongoDB query for this method in the comments below
	// db.comments.insert({
	// 	restaurantId: "40827287",
	// 	name: "Fred",
	// 	date: Long("1682519304"),
	// 	comment: "Great Restaurant!",
	// 	rating: 5,
	// });
	public void insertRestaurantComment(Comment comment) {
		mongoTemplate.insert(comment, "comments");    
	}
}
