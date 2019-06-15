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

package cynoodle.util.measurements;

public abstract class BaseUnit<U extends BaseUnit<U>> implements Unit<U> {

    private final String identifier;
    private final String symbol;
    private final String name;

    // ===

    BaseUnit(String identifier, String symbol, String name) {
        this.identifier = identifier;
        this.symbol = symbol;
        this.name = name;
    }

    // ===

    @Override
    public final String identifier() {
        return this.identifier;
    }

    @Override
    public final String symbol() {
        return this.symbol;
    }

    @Override
    public final String name() {
        return this.name;
    }
}
