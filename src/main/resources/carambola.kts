/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

import enveeed.carambola.dsl.*

import enveeed.carambola.flogger.dsl.useFlogger
import enveeed.carambola.slf4j.dsl.useSLF4J
import enveeed.carambola.jul.dsl.useJUL

import enveeed.carambola.handlers.StandardHandler

import java.util.logging.Level

// carambola configuration script
carambola {

    adapters {
        useFlogger()
        useSLF4J()
        useJUL()
    }

    handlers {
        handler(StandardHandler())
    }

    level(Level.FINE)

}