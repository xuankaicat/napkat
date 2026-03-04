package io.github.xuankaicat.napkat.spring.boot.starter

import io.github.xuankaicat.napkat.spring.boot.starter.test.mock.MockBot
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan
open class TestConfig {
    @TestConfiguration
    open class MockBotConfig {
        @Bean
        open fun mockBot(): MockBot {
            return MockBot()
        }
    }
}