package dev.rm.recipes.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.rm.recipes.model.Like;
import dev.rm.recipes.repository.LikeRepository;

@Service
public class LikeServiceImpl implements LikeService {

  private final LikeRepository likeRepository;

  @Autowired
  public LikeServiceImpl(LikeRepository likeRepository) {
    this.likeRepository = likeRepository;
  }

  @Override
  public Like addLike(Like like) {
    return likeRepository.save(like);
  }

  @Override
  public List<Like> getLikesByUserId(Long userId) {
    return likeRepository.findByUser_UserId(userId);
  }

  @Override
  public List<Like> getLikesByRecipeId(Long recipeId) {
    return likeRepository.findByRecipe_RecipeId(recipeId);
  }

  @Override
  public List<Like> getLikesByUserIdAndRecipeId(Long userId, Long recipeId) {
    return likeRepository.findByUser_UserIdAndRecipe_RecipeId(userId, recipeId);
  }

  @Override
  public Long getLikeCount(Long recipeId) {
    return (long) likeRepository.findByRecipe_RecipeId(recipeId).size();
  }

  @Override
  public void removeLike(Long likeId) {
    Optional<Like> likeOptional = likeRepository.findById(likeId);
    if (likeOptional.isPresent()) {
      likeRepository.delete(likeOptional.get());
    } else {
      throw new RuntimeException("Like with id " + likeId + " not found.");
    }
  }
}