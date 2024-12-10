package dev.rm.recipes.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.rm.recipes.model.Ingredient;
import dev.rm.recipes.repository.IngredientRepository;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class IngredientServiceImplTest {

  @Mock
  private IngredientRepository ingredientRepository;

  @InjectMocks
  private IngredientServiceImpl ingredientService;

  private Ingredient ingredient;
  private Long ingredientId;

  @BeforeEach
  void setUp() {
    ingredientId = 1L;
    ingredient = new Ingredient(ingredientId, "Tomato", "2", null);
  }

  @Test
  void testCreateIngredient() {

    when(ingredientRepository.save(ingredient)).thenReturn(ingredient);

    Ingredient result = ingredientService.createIngredient(ingredient);

    assertNotNull(result, "Created ingredient should not be null");
    assertEquals(ingredient, result, "Created ingredient should be the same as input ingredient");
    verify(ingredientRepository, times(1)).save(ingredient);
  }

  @Test
  void testGetIngredientById_Success() {

    when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(ingredient));

    Ingredient result = ingredientService.getIngredientById(ingredientId);

    assertNotNull(result, "Ingredient should be found");
    assertEquals(ingredient, result, "Returned ingredient should match the expected one");
    verify(ingredientRepository, times(1)).findById(ingredientId);
  }

  @Test
  void testGetIngredientById_NotFound() {

    when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> ingredientService.getIngredientById(ingredientId));
    assertEquals("Ingredient not found", exception.getMessage(), "Exception message should match");
    verify(ingredientRepository, times(1)).findById(ingredientId);
  }

  @Test
  void testGetAllIngredients() {

    Ingredient ingredient2 = new Ingredient(2L, "Pasta", "200g", null);
    List<Ingredient> ingredients = List.of(ingredient, ingredient2);
    when(ingredientRepository.findAll()).thenReturn(ingredients);

    List<Ingredient> result = ingredientService.getAllIngredients();

    assertNotNull(result, "Result should not be null");
    assertEquals(2, result.size(), "Result list should contain two ingredients");
    assertTrue(result.contains(ingredient), "Result should contain the first ingredient");
    assertTrue(result.contains(ingredient2), "Result should contain the second ingredient");
    verify(ingredientRepository, times(1)).findAll();
  }

  @Test
  void testUpdateIngredient() {

    Ingredient updatedIngredient = new Ingredient(ingredientId, "Updated Tomato", "3", null);
    when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(ingredient));
    when(ingredientRepository.save(updatedIngredient)).thenReturn(updatedIngredient);

    Ingredient result = ingredientService.updateIngredient(ingredientId, updatedIngredient);

    assertNotNull(result, "Updated ingredient should not be null");
    assertEquals(updatedIngredient, result, "Returned ingredient should match the updated one");
    verify(ingredientRepository, times(1)).findById(ingredientId);
    verify(ingredientRepository, times(1)).save(updatedIngredient);
  }

  @Test
  void testDeleteIngredient_Success() {

    doNothing().when(ingredientRepository).deleteById(ingredientId);

    ingredientService.deleteIngredient(ingredientId);

    verify(ingredientRepository, times(1)).deleteById(ingredientId);
  }

  @Test
  void testDeleteIngredient_NotFound() {

    doThrow(new RuntimeException("Ingredient not found")).when(ingredientRepository).deleteById(ingredientId);

    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> ingredientService.deleteIngredient(ingredientId));
    assertEquals("Ingredient not found", exception.getMessage(), "Exception message should match");
    verify(ingredientRepository, times(1)).deleteById(ingredientId);
  }
}
