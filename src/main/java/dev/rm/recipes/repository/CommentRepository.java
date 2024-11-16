package dev.rm.recipes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.rm.recipes.model.Comment;
import dev.rm.recipes.model.Recipe;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findByRecipe(Recipe recipe);
}