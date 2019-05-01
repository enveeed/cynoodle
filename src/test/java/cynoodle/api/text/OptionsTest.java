/*
 * cynoodle, a bot for the chat platform Discord
 *
 * Copyright (C) 2019 enveeed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * All trademarks are the property of their respective owners, including, but not limited to Discord Inc.
 */

package cynoodle.api.text;

import cynoodle.api.parser.ParsingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OptionsTest {

    @Test
    void testParsing() {

        String input = "param0 \"parameter 1\" -ab parameter\\ with\\ escaping\\ " +
                "2 --flag1 --flag2 -c param3 --flag-with-value value param4 \\-d param5 -e";

        // ===

        Options.Builder builder = Options.newBuilder();

        Options.Option a = Options.newFlagOption("option-a", 'a');
        Options.Option b = Options.newFlagOption("option-b", 'b');
        Options.Option c = Options.newFlagOption("option-c", 'c');
        Options.Option d = Options.newFlagOption("option-d", 'd');
        Options.Option e = Options.newFlagOption("option-e", 'e');
        Options.Option x = Options.newFlagOption("flag1", 'x');
        Options.Option y = Options.newFlagOption("flag2", 'y');
        Options.Option z = Options.newValueOption("flag-with-value", 'z');

        builder.add(a, b, c, d, e, x, y, z);

        Options options = builder.build();

        // ===

        Options.Result result = assertDoesNotThrow(() -> options.parse(input));

        assertTrue(result.hasOption(a));
        assertTrue(result.hasOption(b));
        assertTrue(result.hasOption(c));

        assertFalse(result.hasOption(d));

        assertTrue(result.hasOption(e));

        assertTrue(result.hasOption(x));
        assertTrue(result.hasOption(y));
        assertTrue(result.hasOption(z));

        assertEquals("value", result.getOptionValue(z));

        assertEquals("param0", result.getParameters().get(0).orElse(null));
        assertEquals("parameter 1", result.getParameters().get(1).orElse(null));
        assertEquals("parameter with escaping 2", result.getParameters().get(2).orElse(null));
        assertEquals("param3", result.getParameters().get(3).orElse(null));
        assertEquals("param4", result.getParameters().get(4).orElse(null));
        assertEquals("-d", result.getParameters().get(5).orElse(null));
        assertEquals("param5", result.getParameters().get(6).orElse(null));

    }

    @Test
    void testEndsWithSingleCharacterOption() {

        String input = "param0 param1 param2 param3 -a";

        // ===

        Options.Builder builder = Options.newBuilder();

        Options.Option a = Options.newFlagOption("option-a", 'a');

        builder.add(a);

        Options options = builder.build();

        // ===

        Options.Result result = assertDoesNotThrow(() -> options.parse(input));

        assertTrue(result.hasOption(a));
    }

    @Test
    void testEndsWithSingleCharacterParameter() {

        String input = "param0 param1 param2 param3 4";

        // ===

        Options.Builder builder = Options.newBuilder();

        Options options = builder.build();

        // ===

        Options.Result result = assertDoesNotThrow(() -> options.parse(input));

        assertEquals("4", result.getParameters().get(4).orElse(null));

    }

    @Test
    void testUnknownOptionShort() {

        String input = "-abcd";

        // ===

        Options.Builder builder = Options.newBuilder();

        Options.Option a = Options.newFlagOption("option-a", 'a');
        Options.Option b = Options.newFlagOption("option-b", 'b');
        Options.Option d = Options.newFlagOption("option-d", 'd');

        builder.add(a, b, d);

        Options options = builder.build();

        // ===

        assertThrows(ParsingException.class, () -> options.parse(input));

    }

    @Test
    void testUnknownOptionLong() {

        String input = "--option-a --option-b --option-c --option-d";

        // ===

        Options.Builder builder = Options.newBuilder();

        Options.Option a = Options.newFlagOption("option-a", 'a');
        Options.Option b = Options.newFlagOption("option-b", 'b');
        Options.Option d = Options.newFlagOption("option-d", 'd');

        builder.add(a, b, d);

        Options options = builder.build();

        // ===

        assertThrows(ParsingException.class, () -> options.parse(input));

    }

    @Test
    void testOptionShortValue() {

        String input = "-abvc value";

        // ===

        Options.Builder builder = Options.newBuilder();

        Options.Option a = Options.newFlagOption("option-a", 'a');
        Options.Option b = Options.newFlagOption("option-b", 'b');
        Options.Option c = Options.newFlagOption("option-c", 'c');

        Options.Option v = Options.newValueOption("option-v", 'v');

        builder.add(a, b, c, v);

        Options options = builder.build();

        // ===

        assertThrows(ParsingException.class, () -> options.parse(input));
    }

    @Test
    void testOptionValueMissing() {

        String input = "param0 param1 param2 param3 -v";

        // ===

        Options.Builder builder = Options.newBuilder();

        Options.Option v = Options.newValueOption("option-v", 'v');

        builder.add(v);

        Options options = builder.build();

        // ===

        assertThrows(ParsingException.class, () -> options.parse(input));
    }

}