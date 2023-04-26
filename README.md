## Task 2: Get list of cuisines
db.restaurants.distinct('cuisine');

## Task 3: Get list of restaurants of a particular cuisine
db.restaurants.aggregate([ 
    {
        $match: {cuisine: "Asian"}
    },
    {
        $project: {
            restaurant_id: 1,
            name: 1 
        }
    }
]).sort({
    name: 1,
});

## Task 4: Get restaurant details by restaurant_id
db.restaurants.aggregate([
    {
        $match: {restaurant_id: '40827287'}
    },
    {
        $lookup: {
            from: "comments",
            localField: "restaurant_id",
            foreignField: "restaurantId",
            as: "comments_for_this_restaurant",
        }
    },
    {
        $project: {
            _id: -1,
            restaurant_id: 1,
            name: 1,
            cuisine: 1,
            address: {
                $concat: ["$address.building", ", ", "$address.street", ", ", "$address.zipcode", ", ", "$borough"]
            },
            comments: "$comments_for_this_restaurant",
        }
    }
]);

## Task 5: Insert comment document into comments collection
db.comments.insert({
    restaurantId: "40827287",
    name: "Fred",
    date: Long("1682519304000"),
    comment: "Great Restaurant!",
    rating: 5,
});
db.comments.insert({
    restaurantId: "40827287",
    name: "Wilma",
    date: Long("1682519999000"),
    comment: "Love it!",
    rating: 4,
});