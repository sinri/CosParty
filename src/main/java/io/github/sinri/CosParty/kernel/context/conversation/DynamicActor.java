package io.github.sinri.CosParty.kernel.context.conversation;

import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;

/**
 * 动态角色类。
 * <p>
 * 在AI对话场景中被赋予身份的角色，具有名称和行为指令。
 */
public class DynamicActor implements Actor {
    /**
     * 角色的唯一标识名称。
     */
    private String actorName;

    /**
     * 角色的行为指令或描述。
     */
    private String actorInstruction;

    /**
     * 获取角色名称。
     *
     * @return 角色的唯一标识名称
     */
    @Nonnull
    @Override
    public String getActorName() {
        return this.actorName;
    }

    /**
     * 设置角色名称。
     *
     * @param actorName 角色的唯一标识名称
     * @return 当前Actor实例
     */
    public DynamicActor setActorName(String actorName) {
        this.actorName = actorName;
        return this;
    }

    /**
     * 获取角色指令。
     *
     * @return 角色的行为指令或描述
     */
    @Nonnull
    @Override
    public String getActorInstruction() {
        return this.actorInstruction;
    }

    /**
     * 设置角色指令。
     *
     * @param actorInstruction 角色的行为指令或描述
     * @return 当前Actor实例
     */
    public DynamicActor setActorInstruction(String actorInstruction) {
        this.actorInstruction = actorInstruction;
        return this;
    }

    /**
     * 从JSON对象反序列化重载Actor实例。
     *
     * @param jsonObject 包含Actor信息的JSON对象
     */
    @Override
    public void reloadData(@Nonnull JsonObject jsonObject) {
        this.setActorName(jsonObject.getString("actor_name"))
            .setActorInstruction(jsonObject.getString("actor_instruction"));
    }

    /**
     * 返回Actor的字符串表示。
     *
     * @return Actor的JSON格式字符串表示
     */
    @Override
    public String toString() {
        return toJsonExpression();
    }
}
