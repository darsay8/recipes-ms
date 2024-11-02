package dev.rm.recipes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.rm.recipes.model.Like;

import java.util.List;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {
  List<Like> findByUserId(UUID userId);

  List<Like> findByRecipeId(UUID recipeId);
}
