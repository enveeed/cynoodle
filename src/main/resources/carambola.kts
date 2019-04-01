/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

import enveeed.carambola.dsl.*
import enveeed.carambola.flogger.dsl.*
import enveeed.carambola.handlers.StandardHandler

// carambola configuration script
carambola {

    adapters {
        useFlogger()
    }

    handlers {
        handler(StandardHandler())
    }

}