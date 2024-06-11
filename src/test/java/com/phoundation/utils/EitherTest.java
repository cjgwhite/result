package com.phoundation.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EitherTest {

  @Nested
  @DisplayName("When Left")
  class WhenLeft {

    private final Either<String, String> left = Either.left("LEFT");

    @Test
    @DisplayName("If left then execute left function")
    void match() {
      var value = left.match(
          str -> str,
          itg -> fail("Executed ifRight")
      );

      assertEquals("LEFT", value);
    }
    @Test
    @DisplayName("ifLeft does execute Consumer")
    void ifLeft() {

      StringBuilder result = new StringBuilder();
      left.ifLeft(result::append)
              .ifRight(Assertions::fail);


      assertEquals("LEFT", result.toString());
    }
  }

  @Nested
  @DisplayName("When Right")
  class WhenRight {

    Either<String, String> right = Either.right("RIGHT");
    @Test
    @DisplayName("If match then execute right function")
    void match() {
      var value = right.match(
          str -> fail("Executed ifLeft"),
          str -> str
                             );

      assertEquals("RIGHT", value);
    }

    @Test
    @DisplayName("If chaining then ifRight executed")
    void ifRight() {
      StringBuilder result = new StringBuilder();

      right.ifRight(result::append)
          .ifLeft(Assertions::fail);

      assertEquals("RIGHT", result.toString());
    }
  }

}
