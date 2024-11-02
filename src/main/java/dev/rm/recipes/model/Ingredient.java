package dev.rm.recipes.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ingredients")
public class Ingredient {

  @Id
  @GeneratedValue
  private UUID ingredientId;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String quantity;

  @Column(name = "recipe_id", nullable = false)
  private UUID recipeId;
}
