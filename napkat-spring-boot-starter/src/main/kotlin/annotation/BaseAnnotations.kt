package io.github.xuankaicat.napkat.spring.boot.starter.annotation

import org.springframework.stereotype.Component
import io.github.xuankaicat.napkat.spring.boot.starter.dispatcher.NapCatEventDispatcher

/**
 * NapCat 控制器注解
 *
 * 标识一个类为 NapCat Bot 的事件控制器。
 * 被标注的类会被 Spring 容器管理，且会被 [NapCatEventDispatcher] 扫描以注册事件处理函数。
 *
 * 类似于 Spring MVC 的 @Controller。
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Component
annotation class NapCatController(
    val prefix: Array<String> = []
)
