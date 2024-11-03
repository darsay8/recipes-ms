package dev.rm.recipes.service;

import dev.rm.recipes.model.Ingredient;
import java.util.List;

public interface IngredientService {
  Ingredient addIngredient(Ingredient ingredient);

  List<Ingredient> getIngredientsByRecipeId(Long recipeId);

  void deleteIngredient(Long ingredientId);
}
