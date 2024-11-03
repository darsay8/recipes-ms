package dev.rm.recipes.service;

import org.springframework.stereotype.Service;

import dev.rm.recipes.model.Recipe;
import dev.rm.recipes.repository.RecipeRepository;

import java.util.List;

@Service
public class RecipeServiceImpl implements RecipeService {

  private final RecipeRepository recipeRepository;

  public RecipeServiceImpl(RecipeRepository recipeRepository) {
    this.recipeRepository = recipeRepository;
  }

  @Override
  public Recipe createRecipe(Recipe recipe) {
    return recipeRepository.save(recipe);
  }

  @Override
  public Recipe getRecipeById(Long id) {
    return recipeRepository.findById(id).orElseThrow(() -> new RuntimeException("Recipe not found"));
  }

  @Override
  public List<Recipe> getAllRecipes() {
    return recipeRepository.findAll();
  }

  @Override
  public Recipe updateRecipe(Long id, Recipe recipe) {
    Recipe existingRecipe = getRecipeById(id);
    recipe.setId(existingRecipe.getId());
    return recipeRepository.save(recipe);
  }

  @Override
  public void deleteRecipe(Long id) {
    recipeRepository.deleteById(id);
  }
}
