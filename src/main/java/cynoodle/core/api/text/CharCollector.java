/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.text;

import javax.annotation.Nonnull;

// TODO optimize, maybe dont use StringBuilder for this
public final class CharCollector {

    private StringBuilder content;

    //

    public CharCollector() {
        this.content = new StringBuilder();
    }

    public CharCollector(int initialCapacity) {
        this.content = new StringBuilder(initialCapacity);
    }


    //

    public void collect(char character) {
        this.content.append(character);
    }

    public void collect(char[] characters) {
        this.content.append(characters);
    }

    //

    @Nonnull
    public String drain() {
        String result = this.view();
        this.content.setLength(0);
        return result;
    }

    @Nonnull
    public String view() {
        return this.content.toString();
    }

}
