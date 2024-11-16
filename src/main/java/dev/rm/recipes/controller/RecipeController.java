package dev.rm.recipes.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import dev.rm.recipes.model.Difficulty;
import dev.rm.recipes.model.MealType;
import dev.rm.recipes.model.Recipe;
import dev.rm.recipes.model.User;
import dev.rm.recipes.service.RecipeService;
import dev.rm.recipes.service.UserService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

  private final RecipeService recipeService;

  private final UserService userService;

  public RecipeController(RecipeService recipeService, UserService userService) {
    this.recipeService = recipeService;
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<Recipe> createRecipe(@RequestBody Recipe recipe) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUsername = authentication.getName();

    User user = userService.getUserByEmail(currentUsername);

    recipe.setUser(user);

    Recipe createdRecipe = recipeService.createRecipe(recipe);
    return new ResponseEntity<>(createdRecipe, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
    Recipe recipe = recipeService.getRecipeById(id);
    return ResponseEntity.ok(recipe);
  }

  @GetMapping
  public ResponseEntity<List<Recipe>> getAllRecipes() {
    List<Recipe> recipes = recipeService.getAllRecipes();
    return ResponseEntity.ok(recipes);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Recipe> updateRecipe(@PathVariable Long id, @RequestBody Recipe recipe) {
    Recipe updatedRecipe = recipeService.updateRecipe(id, recipe);
    return ResponseEntity.ok(updatedRecipe);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
    recipeService.deleteRecipe(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/search")
  public ResponseEntity<List<Recipe>> searchRecipes(
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "mealType", required = false) MealType mealType,
      @RequestParam(value = "countryOfOrigin", required = false) String countryOfOrigin,
      @RequestParam(value = "difficulty", required = false) Difficulty difficulty) {
    List<Recipe> recipes = recipeService.searchRecipes(name, mealType, countryOfOrigin, difficulty);
    return ResponseEntity.ok(recipes);
  }

}
