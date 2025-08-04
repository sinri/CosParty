package io.github.sinri.CosParty.kernel.context.conversation;

import io.github.sinri.keel.core.json.JsonifiableDataUnit;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;

/**
 * 对话中的角色接口。
 * <p>
 * 角色是对话的参与者，具有名称和特定的行为指令。
 */
public interface Actor extends JsonifiableDataUnit {
    /**
     * 获取角色名称。
     *
     * @return 角色名称
     */
    @Nonnull
    String getActorName();

    /**
     * 获取角色指令。
     *
     * @return 角色的行为指令
     */
    @Nonnull
    String getActorInstruction();

    /**
     * 转换为JSON对象。
     *
     * @return JSON对象表示
     */
    @Nonnull
    default JsonObject toJsonObject() {
        return new JsonObject()
                .put("actor_name", getActorName())
                .put("actor_instruction", getActorInstruction())
                ;
    }
}
