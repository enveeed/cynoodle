/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.commands;

/**
 * Policy for when a command is issued that is not known ("no such command").
 */
public enum CommandNSCPolicy {

    /**
     * Ignore and don't do anything.
     */
    IGNORE,
    /**
     * Report that the command is not known.
     */
    REPORT,
    /**
     * Report that the command is not known and provide examples
     * for similar commands.
     */
    REPORT_DETAILED

}
