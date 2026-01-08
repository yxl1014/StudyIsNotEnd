package util;

/**
 * @author Administrator
 * @Package : util
 * @Create on : 2026/1/8 15:45
 **/
import entity.TokenInfoDO;

/**
 * Token 解析器
 */
public class TokenResolver {
    /**
     * 从 token 字符串解析 TokenInfoDO
     * 解析失败返回 null
     */
    public static TokenInfoDO resolve(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        try {
            return JwtUtil.parseTokenInfo(token);
        } catch (Exception e) {
            return null;
        }
    }
}

