package io.github.sinri.CosParty.facade.context.conversation;

import io.github.sinri.keel.core.json.JsonifiableEntity;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;

/**
 * 在一个{@link ConversationContext}下通用的在AI大模型下运作的角色。
 * 
 * <p>本类定义了对话系统中参与对话的角色，每个角色具有唯一的名称和特定的行为指令。
 * 角色可以在多个{@link Conversation}中参与对话，通过{@link Speech}发表内容。
 * 
 * <p>角色信息包括：
 * <ul>
 *   <li><strong>角色名称</strong>：用于标识角色的唯一标识符，在对话中用于区分不同角色的发言</li>
 *   <li><strong>角色指令</strong>：描述角色的行为模式、性格特征或特定任务的指令，用于指导AI模型模拟该角色的行为</li>
 * </ul>
 * 
 * <p>本类实现了{@link JsonifiableEntity}接口，支持序列化和反序列化，便于存储和传输角色信息。
 * 
 * @see ConversationContext 对话上下文，管理角色和对话
 * @see Conversation 对话，包含多个发言
 * @see Speech 发言，记录角色的具体发言内容
 */
public class Actor implements JsonifiableEntity<Actor> {
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
    public String getActorName() {
        return this.actorName;
    }

    /**
     * 设置角色名称。
     * 
     * @param actorName 角色的唯一标识名称
     * @return 当前Actor实例，支持链式调用
     */
    public Actor setActorName(String actorName) {
        this.actorName = actorName;
        return this;
    }

    /**
     * 获取角色指令。
     * 
     * @return 角色的行为指令或描述
     */
    public String getActorInstruction() {
        return this.actorInstruction;
    }

    /**
     * 设置角色指令。
     * 
     * @param actorInstruction 角色的行为指令或描述
     * @return 当前Actor实例，支持链式调用
     */
    public Actor setActorInstruction(String actorInstruction) {
        this.actorInstruction = actorInstruction;
        return this;
    }

    /**
     * 将Actor对象序列化为JSON格式。
     * 
     * <p>序列化后的JSON包含以下字段：
     * <ul>
     *   <li><code>actor_name</code>：角色名称</li>
     *   <li><code>actor_instruction</code>：角色指令</li>
     * </ul>
     * 
     * @return 包含Actor信息的JSON对象
     */
    @Nonnull
    @Override
    public JsonObject toJsonObject() {
        return new JsonObject()
                .put("actor_name", actorName)
                .put("actor_instruction", actorInstruction)
                ;
    }

    /**
     * 从JSON对象反序列化创建Actor实例。
     * 
     * <p>期望的JSON格式应包含以下字段：
     * <ul>
     *   <li><code>actor_name</code>：角色名称</li>
     *   <li><code>actor_instruction</code>：角色指令</li>
     * </ul>
     * 
     * @param jsonObject 包含Actor信息的JSON对象
     * @return 新创建的Actor实例
     * @throws IllegalArgumentException 如果JSON格式不正确或缺少必要字段
     */
    @Nonnull
    @Override
    public Actor reloadDataFromJsonObject(@Nonnull JsonObject jsonObject) {
        return new Actor()
                .setActorName(jsonObject.getString("actor_name"))
                .setActorInstruction(jsonObject.getString("actor_instruction"))
                ;
    }

    /**
     * 获取当前Actor实例。
     * 
     * @return 当前Actor实例
     */
    @Nonnull
    @Override
    public Actor getImplementation() {
        return this;
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
