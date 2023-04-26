package ibf2022.batch2.paf.server.controllers;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import ibf2022.batch2.paf.server.models.Comment;
import ibf2022.batch2.paf.server.models.Restaurant;
import ibf2022.batch2.paf.server.services.RestaurantService;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.servlet.http.HttpSession;

@RestController
public class RestaurantController {

	@Autowired
	private RestaurantService restaurantService;

	// TODO: Task 2 - request handler
	@GetMapping(path={"/", "/api/cuisines"}, produces="application/json")
	public ResponseEntity<String> getCuisines() {
		List<String> cuisines = restaurantService.getCuisines();
		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
		for (String c : cuisines) {
			jsonArrayBuilder.add(c);
		}
		JsonArray jsonArray = jsonArrayBuilder.build();
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(jsonArray.toString());
	}

	// TODO: Task 3 - request handler
	@GetMapping(path="/api/restaurants/{cuisine}", produces="application/json")
	public ResponseEntity<String> getRestaurantsByCuisine(@PathVariable String cuisine) {
		List<Restaurant> restaurants = restaurantService.getRestaurantsByCuisine(cuisine);
		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
		for (Restaurant r: restaurants) {
			jsonArrayBuilder.add(r.toJsonObjectBuilder(true));
		}
		JsonArray jsonArray = jsonArrayBuilder.build();
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(jsonArray.toString());
	}


	// TODO: Task 4 - request handler
	@GetMapping(path="/api/restaurant/{restaurantId}", produces="application/json")
	public ResponseEntity<String> getRestaurantById(@PathVariable String restaurantId, HttpSession session) {
		session.setAttribute("restaurant_id", restaurantId);
		Optional<Restaurant> result = restaurantService.getRestaurantById(restaurantId);
		if (result.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
								.contentType(MediaType.APPLICATION_JSON)
								.body(Json.createObjectBuilder()
											.add("error", "Missing %s".formatted(restaurantId))
											.build()
											.toString());
		}
		Restaurant restaurant = result.get();
		return ResponseEntity.status(HttpStatus.OK)
							.contentType(MediaType.APPLICATION_JSON)
							.body(restaurant.toJsonObjectBuilder().build().toString());
	}

	// TODO: Task 5 - request handler
	@PostMapping(path="/api/restaurant/comment", produces="application/json")
	public ResponseEntity<String> postRestaurantComment(@ModelAttribute Comment comment, HttpSession session) {
		String restaurantId = (String) session.getAttribute("restaurant_id");
		comment.setRestaurantId(restaurantId);
		comment.setDate(new Date().getTime());
		restaurantService.postRestaurantComment(comment);
		return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(Json.createObjectBuilder().build().toString());
	}
}
