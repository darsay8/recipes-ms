package dev.rm.recipes.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "recipes")
public class Recipe {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long recipeId;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MealType mealType;

  @ElementCollection
  @CollectionTable(name = "ingredients", joinColumns = @JoinColumn(name = "recipe_id"))
  private List<Ingredient> ingredients;

  @Column(name = "country_of_origin")
  private String countryOfOrigin;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Difficulty difficulty;

  @Column(name = "like_count", nullable = false)
  private int likeCount;

  @Column(name = "save_count", nullable = false)
  private int saveCount;

  @Column(name = "share_count", nullable = false)
  private int shareCount;

  @Column(nullable = false)
  private String instructions;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public enum MealType {
    BREAKFAST, LUNCH, DINNER, SNACK
  }

  public enum Difficulty {
    EASY, MEDIUM, HARD
  }
}
