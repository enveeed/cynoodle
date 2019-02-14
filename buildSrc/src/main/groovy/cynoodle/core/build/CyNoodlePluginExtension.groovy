/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.build

import org.gradle.api.Project
import org.gradle.api.provider.ListProperty

class CyNoodlePluginExtension {

    final ListProperty<String> modules

    //

    CyNoodlePluginExtension(Project project) {
        modules = project.objects.listProperty(String)
    }
}
