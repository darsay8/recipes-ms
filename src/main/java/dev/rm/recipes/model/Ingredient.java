package dev.rm.recipes.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ingredients")
public class Ingredient {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long ingredientId;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String quantity;

  @Column(name = "recipe_id", nullable = false)
  private Long recipeId;
}
