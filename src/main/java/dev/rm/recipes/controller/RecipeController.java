package dev.rm.recipes.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.rm.recipes.model.Recipe;
import dev.rm.recipes.service.RecipeService;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

  private final RecipeService recipeService;

  public RecipeController(RecipeService recipeService) {
    this.recipeService = recipeService;
  }

  @PostMapping
  public ResponseEntity<Recipe> createRecipe(@RequestBody Recipe recipe) {
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
}
