package io.github.sinri.CosParty.facade;

import javax.annotation.Nonnull;

/**
 * 角色扮演脚本接口，用于定义AI工作的流程指令集。
 * <p>
 * 该接口定义了角色扮演场景的编排逻辑，通过场景代码来组织和管理整个角色扮演流程。
 * 每个脚本包含多个场景，场景之间通过场景代码进行跳转和连接。
 * <p>
 * 实现类需要提供：
 * <ul>
 *   <li>起始场景的获取方法</li>
 *   <li>根据场景代码获取对应场景的方法</li>
 * </ul>
 * <p>
 * 使用示例：
 * <pre>{@code
 * CosplayScript script = new MyCosplayScript();
 * CosplayScene startScene = script.getStartingScene();
 * CosplayScene nextScene = script.getSceneByCode("scene_001");
 * }</pre>
 */
public interface CosplayScript {
    /**
     * 获取脚本的起始场景。
     * <p>
     * 每个角色扮演脚本都必须有一个起始场景，这是整个流程的入口点。
     * 当开始执行角色扮演时，系统会首先调用此方法获取起始场景。
     * <p>
     * @return 起始场景对象，不能为null
     * @throws RuntimeException 如果起始场景未设置或对应的场景不存在
     * @throws NullPointerException 如果起始场景为null
     */
    @Nonnull
    CosplayScene getStartingScene();

    /**
     * 根据场景代码获取对应的场景对象。
     * <p>
     * 场景代码是场景的唯一标识符，用于在场景之间进行跳转。
     * 实现类需要确保所有可能的场景代码都能返回对应的场景对象。
     * <p>
     * @param sceneCode 场景代码，用于标识特定场景
     * @return 对应的场景对象，不能为null
     * @throws RuntimeException 如果场景代码对应的类不存在、无法实例化或不是CosplayScene类型
     */
    @Nonnull
    CosplayScene getSceneByCode(@Nonnull String sceneCode);
}
