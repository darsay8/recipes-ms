package dev.rm.recipes.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.rm.recipes.model.Ingredient;
import dev.rm.recipes.service.IngredientService;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

  private final IngredientService ingredientService;

  public IngredientController(IngredientService ingredientService) {
    this.ingredientService = ingredientService;
  }

  @PostMapping
  public ResponseEntity<Ingredient> createIngredient(@RequestBody Ingredient ingredient) {
    Ingredient createdIngredient = ingredientService.createIngredient(ingredient);
    return new ResponseEntity<>(createdIngredient, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Ingredient> getIngredientById(@PathVariable Long id) {
    Ingredient ingredient = ingredientService.getIngredientById(id);
    return ResponseEntity.ok(ingredient);
  }

  @GetMapping
  public ResponseEntity<List<Ingredient>> getAllIngredients() {
    List<Ingredient> ingredients = ingredientService.getAllIngredients();
    return ResponseEntity.ok(ingredients);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Ingredient> updateIngredient(@PathVariable Long id, @RequestBody Ingredient ingredient) {
    Ingredient updatedIngredient = ingredientService.updateIngredient(id, ingredient);
    return ResponseEntity.ok(updatedIngredient);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
    ingredientService.deleteIngredient(id);
    return ResponseEntity.noContent().build();
  }
}
