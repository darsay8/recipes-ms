package dev.rm.recipes.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({ "user", "hibernateLazyInitializer", "handler" })
@Table(name = "recipes")
public class Recipe {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long recipeId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_user")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User user;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "image", nullable = false)
  private String image;

  @Column(name = "video_url")
  private String videoUrl;

  @Enumerated(EnumType.STRING)
  @Column(name = "meal_type", nullable = false)
  private MealType mealType;

  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference
  // @OrderColumn(name = "ingredient_order")
  private List<Ingredient> ingredients;

  @Column(name = "country_of_origin")
  private String countryOfOrigin;

  @Enumerated(EnumType.STRING)
  @Column(name = "difficulty")
  private Difficulty difficulty;

  @Column(name = "instructions", nullable = false)
  private String instructions;

  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<Like> likes;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  public Long getUserId() {
    return user != null ? user.getUserId() : null;
  }

  public void setIngredientsWithRecipe(List<Ingredient> ingredients) {
    this.ingredients = ingredients;
    for (Ingredient ingredient : ingredients) {
      ingredient.setRecipe(this);
    }
  }

}
