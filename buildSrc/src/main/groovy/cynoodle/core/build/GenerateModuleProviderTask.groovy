/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.build

import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.TaskAction

class GenerateModuleProviderTask extends DefaultTask {

    final ListProperty<String> modules = project.objects.listProperty(String)

    //

    @TaskAction
    void generate() {

        getLogger().info("Used Modules: "+modules.get().toString())

    }

}
