package serviceEntity;

/**
 * @author Administrator
 * @Package : serviceEntity
 * @Create on : 2026/1/8 15:42
 **/

import entity.TokenInfoDO;
import po.TUserFlagType;
import po.TUserPower;

/**
 * 当前请求用户上下文（ThreadLocal）
 */
public final class UserContext {

    private UserContext() {}

    private static final ThreadLocal<TokenInfoDO> CONTEXT = new ThreadLocal<>();

    /** 设置当前用户 */
    public static void set(TokenInfoDO tokenInfo) {
        CONTEXT.set(tokenInfo);
    }

    /** 获取当前用户（可能为 null） */
    public static TokenInfoDO get() {
        return CONTEXT.get();
    }

    /** 强制获取（不存在直接抛异常） */
    public static TokenInfoDO getRequired() {
        TokenInfoDO info = CONTEXT.get();
        if (info == null) {
            throw new IllegalStateException("UserContext is empty");
        }
        return info;
    }

    /** 清理，必须调用 */
    public static void clear() {
        CONTEXT.remove();
    }

    /* ===== 便捷方法 ===== */

    public static long getUserTel() {
        return getRequired().getUserTel();
    }

    public static TUserPower getUserPower(){
        return getRequired().getUserPower();
    }

    public static TUserFlagType getUserFlagType(){
        return getRequired().getUserFlagType();
    }
}

