package ibf2022.batch2.paf.server.models;

import java.util.LinkedList;
import java.util.List;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;

// Do not change this file
public class Restaurant {

	private String restaurantId;
	private String name;
	private String address;
	private String cuisine;
	private List<Comment> comments = new LinkedList<>();

	public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }
	public String getRestaurantId() { return this.restaurantId; }

	public void setName(String name) { this.name = name; }
	public String getName() { return this.name; }

	public void setAddress(String address) { this.address = address; }
	public String getAddress() { return this.address; }

	public void setCuisine(String cuisine) { this.cuisine = cuisine; }
	public String getCuisine() { return this.cuisine; }

	public void setComments(List<Comment> comments) { this.comments = comments; }
	public List<Comment> getComments() { return this.comments; }
	public void addComment(Comment comment) { this.comments.add(comment); }

	@Override
	public String toString() {
		return "Restaurant{restaurantId=%s, name=%s, address=%s, cuisine=%s, comments=%s"
				.formatted(restaurantId, name, address, cuisine, comments);
	}

	public static Restaurant create(Document d, boolean nameAndIdOnly) {

		Restaurant restaurant = new Restaurant();
		restaurant.setRestaurantId(d.getString("restaurant_id"));
		restaurant.setName(d.getString("name"));
		
		if (!nameAndIdOnly) {
		restaurant.setCuisine(d.getString("cuisine"));
		restaurant.setAddress(d.getString("address"));

		List<Object> objects = d.get("comments", List.class);
		List<Comment> comments = new LinkedList<>();
		for (Object o: objects) {
			Document doc = (Document) o;
			comments.add(Comment.create(doc));
		}
		restaurant.setComments(comments);

		}
		return restaurant;

	}

	public JsonObjectBuilder toJsonObjectBuilder() {
		return this.toJsonObjectBuilder(false);
	}

	public JsonObjectBuilder toJsonObjectBuilder(boolean nameAndIdOnly) {
		if (nameAndIdOnly) {
			return Json.createObjectBuilder()
			.add("restaurantId", this.getRestaurantId())
			.add("name", this.getName());
		}

		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
		for (Comment c: comments) {
			jsonArrayBuilder.add(c.toJsonObjectBuilder());
		}
		JsonArray jsonArray = jsonArrayBuilder.build();
		return Json.createObjectBuilder()
		.add("restaurantId", this.getRestaurantId())
		.add("name", this.getName())
		.add("cuisine", this.getCuisine())
		.add("address", this.getAddress())
		.add("comments", jsonArray);
	}
}
