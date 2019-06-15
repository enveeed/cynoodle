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

package cynoodle.util.options;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OptionsTest {

    @Test
    void testParsing() {

        String input = "param0 \"parameter 1\" -ab parameter\\ with\\ escaping\\ " +
                "2 --flag1 --flag2 -c param3 --flag-with-value value param4 \\-d param5 -e";

        // ===

        Options.Builder builder = Options.newBuilder();

        Option a = Option.newFlagOption("option-a", 'a');
        Option b = Option.newFlagOption("option-b", 'b');
        Option c = Option.newFlagOption("option-c", 'c');
        Option d = Option.newFlagOption("option-d", 'd');
        Option e = Option.newFlagOption("option-e", 'e');
        Option x = Option.newFlagOption("flag1", 'x');
        Option y = Option.newFlagOption("flag2", 'y');
        Option z = Option.newValueOption("flag-with-value", 'z');

        builder.add(a, b, c, d, e, x, y, z);

        Options options = builder.build();

        // ===

        OptionsResult result = assertDoesNotThrow(() -> options.parse(input));

        assertTrue(result.hasOption(a));
        assertTrue(result.hasOption(b));
        assertTrue(result.hasOption(c));

        assertFalse(result.hasOption(d));

        assertTrue(result.hasOption(e));

        assertTrue(result.hasOption(x));
        assertTrue(result.hasOption(y));
        assertTrue(result.hasOption(z));

        assertEquals("value", result.getOptionValue(z));

        assertEquals("param0", result.getParametersRaw().get(0));
        assertEquals("parameter 1", result.getParametersRaw().get(1));
        assertEquals("parameter with escaping 2", result.getParametersRaw().get(2));
        assertEquals("param3", result.getParametersRaw().get(3));
        assertEquals("param4", result.getParametersRaw().get(4));
        assertEquals("-d", result.getParametersRaw().get(5));
        assertEquals("param5", result.getParametersRaw().get(6));

    }

    @Test
    void testEndsWithSingleCharacterOption() {

        String input = "param0 param1 param2 param3 -a";

        // ===

        Options.Builder builder = Options.newBuilder();

        Option a = Option.newFlagOption("option-a", 'a');

        builder.add(a);

        Options options = builder.build();

        // ===

        OptionsResult result = assertDoesNotThrow(() -> options.parse(input));

        assertTrue(result.hasOption(a));
    }

    @Test
    void testEndsWithSingleCharacterParameter() {

        String input = "param0 param1 param2 param3 4";

        // ===

        Options.Builder builder = Options.newBuilder();

        Options options = builder.build();

        // ===

        OptionsResult result = assertDoesNotThrow(() -> options.parse(input));

        assertEquals("4", result.getParametersRaw().get(4));

    }

    @Test
    void testUnknownOptionShort() {

        String input = "-abcd";

        // ===

        Options.Builder builder = Options.newBuilder();

        Option a = Option.newFlagOption("option-a", 'a');
        Option b = Option.newFlagOption("option-b", 'b');
        Option d = Option.newFlagOption("option-d", 'd');

        builder.add(a, b, d);

        Options options = builder.build();

        // ===

        assertThrows(OptionUnknownException.class, () -> options.parse(input));

    }

    @Test
    void testUnknownOptionLong() {

        String input = "--option-a --option-b --option-c --option-d";

        // ===

        Options.Builder builder = Options.newBuilder();

        Option a = Option.newFlagOption("option-a", 'a');
        Option b = Option.newFlagOption("option-b", 'b');
        Option d = Option.newFlagOption("option-d", 'd');

        builder.add(a, b, d);

        Options options = builder.build();

        // ===

        assertThrows(OptionUnknownException.class, () -> options.parse(input));

    }

    @Test
    void testOptionShortValue() {

        String input = "-abvc value";

        // ===

        Options.Builder builder = Options.newBuilder();

        Option a = Option.newFlagOption("option-a", 'a');
        Option b = Option.newFlagOption("option-b", 'b');
        Option c = Option.newFlagOption("option-c", 'c');

        Option v = Option.newValueOption("option-v", 'v');

        builder.add(a, b, c, v);

        Options options = builder.build();

        // ===

        assertThrows(OptionsSyntaxException.class, () -> options.parse(input));
    }

    @Test
    void testOptionValueMissing() {

        String input = "param0 param1 param2 param3 -v";

        // ===

        Options.Builder builder = Options.newBuilder();

        Option v = Option.newValueOption("option-v", 'v');

        builder.add(v);

        Options options = builder.build();

        // ===

        assertThrows(OptionValueMissingException.class, () -> options.parse(input));
    }

}