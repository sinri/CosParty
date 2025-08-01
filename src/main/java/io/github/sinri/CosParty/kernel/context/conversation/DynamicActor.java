package io.github.sinri.CosParty.kernel.context.conversation;

import io.github.sinri.keel.core.json.JsonifiableEntity;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;

/**
 * 本类定义了在AI大模型对话场景下被赋予身份的角色。
 * <p>
 * 一个角色具有唯一的名称，并被赋予特定的行为指令用于指导AI模型模拟该角色的行为。
 * 一个角色应在一个{@link ConversationContext}内注册存在，在其下多个{@link Conversation}中参与对话，对话内容由{@link Speech}封装。
 * <p>
 * 本类实现了{@link JsonifiableEntity}接口，支持序列化和反序列化，便于存储和传输角色信息。
 *
 * @see ConversationContext 对话上下文
 * @see Conversation 对话
 * @see Speech 特定角色的一次发言
 * @since 1.0
 */
public class DynamicActor implements Actor {
    /**
     * 角色的唯一标识名称。
     * 用于在对话中区分不同角色，并在{@link Speech}中标识发言者。
     */
    private String actorName;

    /**
     * 角色的行为指令或描述。
     * 包含角色的性格特征、行为模式、专业领域等信息，
     * 用于指导AI模型在对话中模拟该角色的行为。
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
     * @return 当前Actor实例，支持链式调用
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
     * @return 当前Actor实例，支持链式调用
     */
    public DynamicActor setActorInstruction(String actorInstruction) {
        this.actorInstruction = actorInstruction;
        return this;
    }

    /**
     * 从JSON对象反序列化重载Actor实例。
     *
     * @param jsonObject 包含Actor信息的JSON对象，参照{@link DynamicActor#toJsonObject()}的返回结果
     * @throws IllegalArgumentException 如果JSON格式不正确或缺少必要字段
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
