package io.github.xuankaicat.napkat.core.event

/**
 * NapCat 事件监听器接口
 *
 * 实现此接口以接收并处理 NapCat 事件。
 */
interface NapCatEventListener {
    /**
     * 处理事件
     *
     * @param event 接收到的 NapCat 事件
     */
    suspend fun onEvent(event: NapCatEvent)
}
