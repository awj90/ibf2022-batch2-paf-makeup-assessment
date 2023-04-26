package ibf2022.batch2.paf.server.controllers;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import ibf2022.batch2.paf.server.models.Comment;
import ibf2022.batch2.paf.server.models.Restaurant;
import ibf2022.batch2.paf.server.services.RestaurantService;
import jakarta.servlet.http.HttpSession;

@Controller
public class ViewController {
    
    @Autowired
    private RestaurantService restaurantService;

    @GetMapping(path="/view/cuisines", produces="text/html")
    public String renderViewOne(Model model) {
        List<String> cuisines = restaurantService.getCuisines();
        model.addAttribute("cuisines", cuisines);
        return "view1";
    }

    @GetMapping(path="/view/restaurants/{cuisine}", produces="text/html")
    public String renderViewTwo(@PathVariable String cuisine, Model model) {
        List<Restaurant> restaurants = restaurantService.getRestaurantsByCuisine(cuisine);
        model.addAttribute("restaurants", restaurants);
        model.addAttribute("cuisine", cuisine);
        return "view2";
    }

    @GetMapping(path="/view/restaurant/{restaurantId}", produces="text/html")
    public String renderViewThree(@PathVariable String restaurantId, HttpSession session, Model model) {
        session.setAttribute("restaurant_id", restaurantId);
        Optional<Restaurant> result = restaurantService.getRestaurantById(restaurantId);
        model.addAttribute("restaurant", result.get());
        Comment comment = new Comment();
        comment.setRestaurantId(restaurantId);
        model.addAttribute("comment", comment);
        return "view3";
    }

    @PostMapping(path="/view/restaurant/comment", produces="text/html")
    public String postComment(@ModelAttribute Comment comment, HttpSession session, Model model) {
        String restaurantId = (String) session.getAttribute("restaurant_id");
		comment.setRestaurantId(restaurantId);
		comment.setDate(new Date().getTime());
        restaurantService.postRestaurantComment(comment);

        List<String> cuisines = restaurantService.getCuisines();
        model.addAttribute("cuisines", cuisines);
        return "view1";
    }
}
