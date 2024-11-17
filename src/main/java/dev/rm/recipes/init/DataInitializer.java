package dev.rm.recipes.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import dev.rm.recipes.model.Comment;
import dev.rm.recipes.model.Difficulty;
import dev.rm.recipes.model.Ingredient;
import dev.rm.recipes.model.MealType;
import dev.rm.recipes.model.Recipe;
import dev.rm.recipes.model.Role;
import dev.rm.recipes.model.User;
import dev.rm.recipes.repository.CommentRepository;
import dev.rm.recipes.repository.RecipeRepository;
import dev.rm.recipes.repository.UserRepository;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;

@Component
public class DataInitializer implements CommandLineRunner {

  @Autowired
  private final UserRepository userRepository;

  @Autowired
  private final RecipeRepository recipeRepository;

  @Autowired
  private final CommentRepository commentRepository;

  @Autowired
  private final PasswordEncoder passwordEncoder;

  public DataInitializer(PasswordEncoder passwordEncoder, UserRepository userRepository,
      RecipeRepository recipeRepository, CommentRepository commentRepository) {
    this.passwordEncoder = passwordEncoder;
    this.userRepository = userRepository;
    this.recipeRepository = recipeRepository;
    this.commentRepository = commentRepository;
  }

  @Override
  public void run(String... args) {
    User user1 = User.builder()
        .username("james")
        .email("james@mail.com")
        .password(passwordEncoder.encode("123ABC"))
        .role(Role.ADMIN)
        .build();

    User user2 = User.builder()
        .username("hank")
        .email("hank@mail.com")
        .password(passwordEncoder.encode("123ABC"))
        .role(Role.USER)
        .build();

    User user3 = User.builder()
        .username("sarah")
        .email("sarah@mail.com ")
        .password(passwordEncoder.encode("123ABC"))
        .role(Role.USER)
        .build();

    userRepository.saveAll(Arrays.asList(user1, user2, user3));

    Recipe recipe1 = Recipe.builder()
        .user(user1)
        .name("Pancakes")
        .image("https://joyfoodsunshine.com/wp-content/uploads/2022/08/fluffy-pancake-recipe-from-scratch-2.jpg")
        .videoUrl("https://www.youtube.com/watch?v=9zCVCL4V8JQ")
        .mealType(MealType.BREAKFAST)
        .countryOfOrigin("USA")
        .difficulty(Difficulty.EASY)
        .instructions("Mix ingredients, pour onto skillet, flip when bubbles form.")
        .ingredients(Arrays.asList(
            Ingredient.builder().name("Flour").quantity("2 cups").build(),
            Ingredient.builder().name("Milk").quantity("1.5 cups").build(),
            Ingredient.builder().name("Eggs").quantity("2").build(),
            Ingredient.builder().name("Baking Powder").quantity("1 tbsp").build(),
            Ingredient.builder().name("Salt").quantity("1 tsp").build()))
        .build();

    Recipe recipe2 = Recipe.builder()
        .user(user1)
        .name("Caesar Salad")
        .image("https://www.jessicagavin.com/wp-content/uploads/2019/07/caesar-salad-10-1200.jpg")
        .videoUrl("https://www.youtube.com/watch?v=a4Z2x0sPq3A")
        .mealType(MealType.LUNCH)
        .countryOfOrigin("Italy")
        .difficulty(Difficulty.EASY)
        .instructions("Combine lettuce, croutons, and dressing. Toss to combine.")
        .ingredients(Arrays.asList(
            Ingredient.builder().name("Romaine Lettuce").quantity("1 head").build(),
            Ingredient.builder().name("Croutons").quantity("1 cup").build(),
            Ingredient.builder().name("Caesar Dressing").quantity("1/2 cup").build(),
            Ingredient.builder().name("Parmesan Cheese").quantity("1/4 cup").build()))
        .build();

    Recipe recipe3 = Recipe.builder()
        .user(user1)
        .name("Spaghetti Carbonara")
        .image(
            "https://hips.hearstapps.com/del.h-cdn.co/assets/16/04/1453933735-carbonara-delish-1.jpg?crop=0.995xw:0.664xh;0,0.0349xh&resize=1200:*")
        .videoUrl("https://www.youtube.com/watch?v=NqFi90p38N8")
        .mealType(MealType.DINNER)
        .countryOfOrigin("Italy")
        .difficulty(Difficulty.MEDIUM)
        .instructions("Cook spaghetti, fry pancetta, mix with egg and cheese,combine.")
        .ingredients(Arrays.asList(
            Ingredient.builder().name("Spaghetti").quantity("400g").build(),
            Ingredient.builder().name("Pancetta").quantity("150g").build(),
            Ingredient.builder().name("Eggs").quantity("3").build(),
            Ingredient.builder().name("Parmesan Cheese").quantity("100g").build(),
            Ingredient.builder().name("Black Pepper").quantity("to taste").build()))
        .build();

    Recipe recipe4 = Recipe.builder()
        .user(user2)
        .name("Chocolate Chip Cookies")
        .image("https://olivesnthyme.com/wp-content/uploads/2022/02/Mini-Chocolate-Chip-Cookies-15.jpg")
        .videoUrl("https://www.youtube.com/watch?v=loqCY9b7aec")
        .mealType(MealType.SNACK)
        .countryOfOrigin("USA")
        .difficulty(Difficulty.EASY)
        .instructions("Cream butter and sugar, add eggs, mix in flour and chips.")
        .ingredients(Arrays.asList(
            Ingredient.builder().name("Butter").quantity("1 cup").build(),
            Ingredient.builder().name("Brown Sugar").quantity("1 cup").build(),
            Ingredient.builder().name("Eggs").quantity("2").build(),
            Ingredient.builder().name("Flour").quantity("2.5 cups").build(),
            Ingredient.builder().name("Chocolate Chips").quantity("2 cups").build()))
        .build();

    Recipe recipe5 = Recipe.builder()
        .user(user3)
        .name("Beef Tacos")
        .image("https://loveandgoodstuff.com/wp-content/uploads/2020/08/classic-ground-beef-tacos-1200x1200.jpg")
        .videoUrl("https://www.youtube.com/watch?v=qL6ml7x56p4")
        .mealType(MealType.DINNER)
        .countryOfOrigin("Mexico")
        .difficulty(Difficulty.MEDIUM)
        .instructions("Cook beef, add spices, serve in tortillas with toppings.")
        .ingredients(Arrays.asList(
            Ingredient.builder().name("Ground Beef").quantity("500g").build(),
            Ingredient.builder().name("Taco Shells").quantity("10").build(),
            Ingredient.builder().name("Lettuce").quantity("1 cup").build(),
            Ingredient.builder().name("Tomato").quantity("1 cup").build(),
            Ingredient.builder().name("Cheese").quantity("1 cup").build()))
        .build();

    recipeRepository.saveAll(Arrays.asList(recipe1, recipe2, recipe3, recipe4,
        recipe5));

    Comment comment1 = Comment.builder()
        .user(user1)
        .recipe(recipe5)
        .content("I love this recipe!")
        .build();

    commentRepository.save(comment1);
  }

}
