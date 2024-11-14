package dev.rm.recipes.service;

import dev.rm.recipes.model.Difficulty;
import dev.rm.recipes.model.MealType;
import dev.rm.recipes.model.Recipe;
import java.util.List;

public interface RecipeService {
  Recipe createRecipe(Recipe recipe);

  Recipe getRecipeById(Long id);

  List<Recipe> getAllRecipes();

  Recipe updateRecipe(Long id, Recipe recipe);

  void deleteRecipe(Long id);

  List<Recipe> searchRecipes(String name, MealType mealType, String countryOfOrigin, Difficulty difficulty);
}
