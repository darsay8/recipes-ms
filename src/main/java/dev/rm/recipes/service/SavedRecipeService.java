package dev.rm.recipes.service;

import dev.rm.recipes.model.SavedRecipe;
import java.util.List;

public interface SavedRecipeService {
  SavedRecipe saveRecipe(SavedRecipe savedRecipe);

  List<SavedRecipe> getSavedRecipesByUserId(Long userId);

  void removeSavedRecipe(Long savedRecipeId);
}
