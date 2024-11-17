package dev.rm.recipes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.rm.recipes.model.Like;
import dev.rm.recipes.model.Recipe;
import dev.rm.recipes.model.User;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
  List<Like> findByUser(User user);

  List<Like> findByRecipe(Recipe recipe);
}
