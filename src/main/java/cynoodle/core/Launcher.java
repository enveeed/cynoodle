/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core;

import com.mongodb.ConnectionString;

import javax.annotation.Nonnull;

// TODO everything
public final class Launcher {

    public static void main(@Nonnull String[] args) {

        CyNoodle.launch(StartParameters.newBuilder()
                .setMongoConnection(new ConnectionString("mongodb://localhost"))
                .build());

    }

}
