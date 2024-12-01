package dev.rm.recipes.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import dev.rm.recipes.model.Comment;
import dev.rm.recipes.model.Difficulty;
import dev.rm.recipes.model.Ingredient;
import dev.rm.recipes.model.Like;
import dev.rm.recipes.model.MealType;
import dev.rm.recipes.model.Recipe;
import dev.rm.recipes.model.Role;
import dev.rm.recipes.model.User;
import dev.rm.recipes.repository.CommentRepository;
import dev.rm.recipes.repository.LikeRepository;
import dev.rm.recipes.repository.RecipeRepository;
import dev.rm.recipes.repository.UserRepository;

import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final RecipeRepository recipeRepository;
  private final CommentRepository commentRepository;
  private final LikeRepository likeRepository;
  private final PasswordEncoder passwordEncoder;

  public DataInitializer(PasswordEncoder passwordEncoder, UserRepository userRepository,
      RecipeRepository recipeRepository, CommentRepository commentRepository, LikeRepository likeRepository) {
    this.passwordEncoder = passwordEncoder;
    this.userRepository = userRepository;
    this.recipeRepository = recipeRepository;
    this.commentRepository = commentRepository;
    this.likeRepository = likeRepository;
  }

  public static final String PASSWORD = "123ABC";

  @Override
  public void run(String... args) {
    User user1 = User.builder()
        .username("james")
        .email("james@mail.com")
        .password(passwordEncoder.encode(PASSWORD))
        .role(Role.ADMIN)
        .build();

    User user2 = User.builder()
        .username("hank")
        .email("hank@mail.com")
        .password(passwordEncoder.encode(PASSWORD))
        .role(Role.USER)
        .build();

    User user3 = User.builder()
        .username("sarah")
        .email("sarah@mail.com ")
        .password(passwordEncoder.encode(PASSWORD))
        .role(Role.USER)
        .build();

    userRepository.saveAll(Arrays.asList(user1, user2, user3));

    Ingredient pancakesIngredient1 = Ingredient.builder().name("Flour").quantity("2 cups").build();
    Ingredient pancakesIngredient2 = Ingredient.builder().name("Milk").quantity("1.5 cups").build();
    Ingredient pancakesIngredient3 = Ingredient.builder().name("Eggs").quantity("2").build();
    Ingredient pancakesIngredient4 = Ingredient.builder().name("Baking Powder").quantity("1 tbsp").build();
    Ingredient pancakesIngredient5 = Ingredient.builder().name("Salt").quantity("1 tsp").build();

    Recipe recipe1 = Recipe.builder()
        .user(user1)
        .name("Pancakes")
        .image("https://joyfoodsunshine.com/wp-content/uploads/2022/08/fluffy-pancake-recipe-from-scratch-2.jpg")
        .videoUrl("https://www.youtube.com/watch?v=9zCVCL4V8JQ")
        .mealType(MealType.BREAKFAST)
        .countryOfOrigin("USA")
        .difficulty(Difficulty.EASY)
        .instructions("Mix ingredients, pour onto skillet, flip when bubbles form.")
        .ingredients(Arrays.asList(pancakesIngredient1, pancakesIngredient2,
            pancakesIngredient3, pancakesIngredient4,
            pancakesIngredient5))
        .build();

    recipe1.setIngredientsWithRecipe(Arrays.asList(pancakesIngredient1, pancakesIngredient2, pancakesIngredient3,
        pancakesIngredient4, pancakesIngredient5));

    Ingredient saladIngredient1 = Ingredient.builder().name("Romaine Lettuce").quantity("1 head").build();
    Ingredient saladIngredient2 = Ingredient.builder().name("Croutons").quantity("1 cup").build();
    Ingredient saladIngredient3 = Ingredient.builder().name("Caesar Dressing").quantity("1/2 cup").build();
    Ingredient saladIngredient4 = Ingredient.builder().name("Parmesan Cheese").quantity("1/4 cup").build();

    Recipe recipe2 = Recipe.builder()
        .user(user1)
        .name("Caesar Salad")
        .image("https://www.jessicagavin.com/wp-content/uploads/2019/07/caesar-salad-10-1200.jpg")
        .videoUrl("https://www.youtube.com/watch?v=a4Z2x0sPq3A")
        .mealType(MealType.LUNCH)
        .countryOfOrigin("Italy")
        .difficulty(Difficulty.EASY)
        .instructions("Combine lettuce, croutons, and dressing. Toss to combine.")
        .ingredients(Arrays.asList(saladIngredient1, saladIngredient2, saladIngredient3, saladIngredient4))
        .build();

    recipe2.setIngredientsWithRecipe(
        Arrays.asList(saladIngredient1, saladIngredient2, saladIngredient3, saladIngredient4));

    Ingredient spaghettiIngredient1 = Ingredient.builder().name("Spaghetti").quantity("400g").build();
    Ingredient spaghettiIngredient2 = Ingredient.builder().name("Pancetta").quantity("150g").build();
    Ingredient spaghettiIngredient3 = Ingredient.builder().name("Eggs").quantity("3").build();
    Ingredient spaghettiIngredient4 = Ingredient.builder().name("Parmesan Cheese").quantity("100g").build();
    Ingredient spaghettiIngredient5 = Ingredient.builder().name("Black Pepper").quantity("to taste").build();

    Recipe recipe3 = Recipe.builder()
        .user(user1)
        .name("Spaghetti Carbonara")
        .image(
            "https://hips.hearstapps.com/del.h-cdn.co/assets/16/04/1453933735-carbonara-delish-1.jpg?crop=0.995xw:0.664xh;0,0.0349xh&resize=1200:*")
        .videoUrl("https://www.youtube.com/watch?v=NqFi90p38N8")
        .mealType(MealType.DINNER)
        .countryOfOrigin("Italy")
        .difficulty(Difficulty.MEDIUM)
        .instructions("Cook spaghetti, fry pancetta, mix with egg andcheese,combine.")
        .ingredients(Arrays.asList(spaghettiIngredient1, spaghettiIngredient2, spaghettiIngredient3,
            spaghettiIngredient4, spaghettiIngredient5))
        .build();

    recipe3.setIngredientsWithRecipe(Arrays.asList(spaghettiIngredient1, spaghettiIngredient2, spaghettiIngredient3,
        spaghettiIngredient4, spaghettiIngredient5));

    Ingredient cookieIngredient1 = Ingredient.builder().name("Butter").quantity("1 cup").build();
    Ingredient cookieIngredient2 = Ingredient.builder().name("Brown Sugar").quantity("1 cup").build();
    Ingredient cookieIngredient3 = Ingredient.builder().name("Eggs").quantity("2").build();
    Ingredient cookieIngredient4 = Ingredient.builder().name("Flour").quantity("2.5 cups").build();
    Ingredient cookieIngredient5 = Ingredient.builder().name("Chocolate Chips").quantity("2 cups").build();

    Recipe recipe4 = Recipe.builder()
        .user(user2)
        .name("Chocolate Chip Cookies")
        .image("https://olivesnthyme.com/wp-content/uploads/2022/02/Mini-Chocolate-Chip-Cookies-15.jpg")
        .videoUrl("https://www.youtube.com/watch?v=loqCY9b7aec")
        .mealType(MealType.SNACK)
        .countryOfOrigin("USA")
        .difficulty(Difficulty.EASY)
        .instructions("Cream butter and sugar, add eggs, mix in flour and chips.")
        .ingredients(Arrays.asList(cookieIngredient1, cookieIngredient2, cookieIngredient3, cookieIngredient4,
            cookieIngredient5))
        .build();

    recipe4.setIngredientsWithRecipe(Arrays.asList(cookieIngredient1, cookieIngredient2, cookieIngredient3,
        cookieIngredient4, cookieIngredient5));

    Ingredient tacosIngredient1 = Ingredient.builder().name("Ground Beef").quantity("500g").build();
    Ingredient tacosIngredient2 = Ingredient.builder().name("Taco Shells").quantity("10").build();
    Ingredient tacosIngredient3 = Ingredient.builder().name("Lettuce").quantity("1 cup").build();
    Ingredient tacosIngredient4 = Ingredient.builder().name("Tomato").quantity("1 cup").build();
    Ingredient tacosIngredient5 = Ingredient.builder().name("Cheese").quantity("1 cup").build();

    Recipe recipe5 = Recipe.builder()
        .user(user3)
        .name("Beef Tacos")
        .image("https://loveandgoodstuff.com/wp-content/uploads/2020/08/classic-ground-beef-tacos-1200x1200.jpg")
        .videoUrl("https://www.youtube.com/watch?v=qL6ml7x56p4")
        .mealType(MealType.DINNER)
        .countryOfOrigin("Mexico")
        .difficulty(Difficulty.MEDIUM)
        .instructions("Cook beef, add spices, serve in tortillas with toppings.")
        .ingredients(
            Arrays.asList(tacosIngredient1, tacosIngredient2, tacosIngredient3, tacosIngredient4, tacosIngredient5))
        .build();

    recipe5
        .setIngredientsWithRecipe(Arrays.asList(tacosIngredient1, tacosIngredient2, tacosIngredient3, tacosIngredient4,
            tacosIngredient5));

    recipeRepository.saveAll(Arrays.asList(recipe1, recipe2, recipe3, recipe4,
        recipe5));

    Comment comment1 = Comment.builder()
        .user(user1)
        .recipe(recipe5)
        .content("I love this recipe!")
        .build();

    commentRepository.save(comment1);

    Like like1 = Like.builder().user(user1).recipe(recipe5).build();
    Like like2 = Like.builder().user(user2).recipe(recipe5).build();
    Like like3 = Like.builder().user(user3).recipe(recipe5).build();

    likeRepository.saveAll(Arrays.asList(like1, like2, like3));
  }
}
