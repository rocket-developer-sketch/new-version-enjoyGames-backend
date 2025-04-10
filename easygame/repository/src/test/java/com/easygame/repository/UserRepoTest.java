package com.easygame.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@BaseTestConfig
public class UserRepoTest {
    @Autowired
    private UserRepository repository;

    @Autowired
    private ApplicationContext context;

    @Test
    void printBeans() {
        Arrays.stream(context.getBeanDefinitionNames())
                .filter(name -> name.contains("userRepository"))
                .forEach(System.out::println);
    }

    @Test
    @DisplayName("Confirms successful user save")
    void saveUser_success() {
        User user = User.builder()
                .nickName("test_nick")
                .build();

        User savedUser = repository.save(user);

        assertThat(savedUser.getUserId()).isNotNull();
        assertThat(savedUser.getNickName()).isEqualTo("test_nick");
        assertThat(savedUser.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Throws an exception when trying to save a duplicate nickname")
    void saveUser_duplicatedNickname_shouldFail() {
        User user1 = User.builder().nickName("dupUser").build();
        User user2 = User.builder().nickName("dupUser").build();

        repository.save(user1);

        assertThatThrownBy(() -> repository.saveAndFlush(user2))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Finds a user by nickname")
    void findByNickname_success() {
        User user = User.builder().nickName("hanna").build();
        repository.save(user);

        Optional<User> result = repository.findByNickName("hanna");

        assertThat(result).isPresent();
        assertThat(result.get().getNickName()).isEqualTo("hanna");
    }
}
