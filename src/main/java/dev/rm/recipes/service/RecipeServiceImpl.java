package dev.rm.recipes.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import dev.rm.recipes.model.Difficulty;
import dev.rm.recipes.model.MealType;
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

  @Override
  public List<Recipe> searchRecipes(String name, MealType mealType, String countryOfOrigin, Difficulty difficulty) {

    Specification<Recipe> spec = Specification.where(null);

    if (name != null) {
      spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
    }

    if (mealType != null) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("mealType"), mealType));
    }

    if (countryOfOrigin != null) {
      spec = spec
          .and((root, query, cb) -> cb.equal(cb.lower(root.get("countryOfOrigin")), countryOfOrigin.toLowerCase()));
    }

    if (difficulty != null) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("difficulty"), difficulty));
    }

    return recipeRepository.findAll(spec);
  }
}
