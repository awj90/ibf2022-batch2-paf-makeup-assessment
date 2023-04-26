package ibf2022.batch2.paf.server.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ibf2022.batch2.paf.server.models.Comment;
import ibf2022.batch2.paf.server.models.Restaurant;
import ibf2022.batch2.paf.server.repositories.RestaurantRepository;

@Service
public class RestaurantService {

	@Autowired
	private RestaurantRepository restaurantRepository;

	// TODO: Task 2
	// Do not change the method's signature
	public List<String> getCuisines() {
		List<String> cuisines = restaurantRepository.getCuisines()
													.stream()
													.map(s -> s.replaceAll("/", "_"))
													.sorted()
													.collect(Collectors.toList());

		return cuisines;
	}

	// TODO: Task 3 
	// Do not change the method's signature
	public List<Restaurant> getRestaurantsByCuisine(String cuisine) {
		List<Restaurant> restaurants = restaurantRepository.getRestaurantsByCuisine(cuisine);
		restaurants.sort(Comparator.comparing(restaurant -> restaurant.getName()));
		return restaurants;
	}

	// TODO: Task 4 
	// Do not change the method's signature
	public Optional<Restaurant> getRestaurantById(String id) {
		return restaurantRepository.getRestaurantById(id);
	}

	// TODO: Task 5 
	// Do not change the method's signature
	public void postRestaurantComment(Comment comment) {
		restaurantRepository.insertRestaurantComment(comment);
	}
}
