package io.github.xuankaicat.napkat.core.api

/**
 * 密钥接口
 *
 * 密钥获取、Token、Cookies相关接口
 */
interface INapCatKey {
    /**
     * 获取 Cookies
     * @param domain 域名
     * @see <a href="https://napcat.apifox.cn/411631171e0">https://napcat.apifox.cn/411631171e0</a>
     */
    suspend fun getCookies(domain: String): NapCatResponse<Cookies>

    /**
     * 获取 CSRF Token
     * @see <a href="https://napcat.apifox.cn/411631172e0">https://napcat.apifox.cn/411631172e0</a>
     */
    suspend fun getCsrfToken(): NapCatResponse<CsrfToken>

    /**
     * 获取登录凭证
     * @see <a href="https://napcat.apifox.cn/411631173e0">https://napcat.apifox.cn/411631173e0</a>
     */
    suspend fun getCredentials(): NapCatResponse<Credentials>

    /**
     * 获取扩展 RKey
     * @see <a href="https://napcat.apifox.cn/411631175e0">https://napcat.apifox.cn/411631175e0</a>
     */
    suspend fun getRkey(): NapCatResponse<List<RKeyInfo>>

    /**
     * 获取 RKey 服务器
     * @see <a href="https://napcat.apifox.cn/411631177e0">https://napcat.apifox.cn/411631177e0</a>
     */
    suspend fun getRkeyServer(): NapCatResponse<RKeyServerInfo>

    /**
     * 获取 RKey
     * @see <a href="https://napcat.apifox.cn/411631174e0">https://napcat.apifox.cn/411631174e0</a>
     */
    suspend fun ncGetRkey(): NapCatResponse<List<NcRKeyInfo>>

    /**
     * 获取 ClientKey
     * @see <a href="https://napcat.apifox.cn/411631176e0">https://napcat.apifox.cn/411631176e0</a>
     */
    suspend fun getClientKey(): NapCatResponse<ClientKeyInfo>
}
