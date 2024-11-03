package dev.rm.recipes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.rm.recipes.model.Like;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
  List<Like> findByUserId(Long userId);

  List<Like> findByRecipeId(Long recipeId);
}
