package dev.rm.recipes;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

import dev.rm.recipes.controller.UserController;

@SpringBootTest
class RecipesApplicationTests {

  @Autowired
  private UserController controller;

  @Test
  void contextLoads() {
    assertThat(controller).isNotNull();
  }

}
