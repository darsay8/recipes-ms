package dev.rm.recipes.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "saved_recipes")
public class SavedRecipe {

  @Id
  @GeneratedValue
  private UUID savedRecipeId;

  @Column(nullable = false)
  private UUID userId;

  @Column(nullable = false)
  private UUID recipeId;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }
}
