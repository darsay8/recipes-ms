package dev.rm.recipes.service;

import dev.rm.recipes.model.Recipe;
import java.util.List;

public interface RecipeService {
  Recipe createRecipe(Recipe recipe);

  Recipe getRecipeById(Long recipeId);

  List<Recipe> getAllRecipes();

  List<Recipe> getRecipesByUserId(Long userId);

  Recipe updateRecipe(Long recipeId, Recipe recipe);

  void deleteRecipe(Long recipeId);
}
