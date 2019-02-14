/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.build


import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Plugin for cynoodle build configuration.
 */
// TODO This is reserved for automatically setting module classes in the build progress in the future
class CyNoodlePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {


        def extension = project.extensions.create('cynoodle', CyNoodlePluginExtension, project)

        project.tasks.create group: 'cynoodle', name: 'generateModuleProvider', type: GenerateModuleProviderTask,
                action: {
                    GenerateModuleProviderTask task ->
                        task.modules.addAll(extension.modules)
                }

    }
}
