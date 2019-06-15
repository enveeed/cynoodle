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

package cynoodle.discord.format;

import cynoodle.util.Strings;

import javax.annotation.Nonnull;

/**
 * Formatter for a fixed-size table.
 */
public final class DFFixedTable implements DFormattable {

    private final int width;
    private final int height;

    private final int[] columnWidths;

    private final boolean freeLastColumn;

    private final String[][] cells;

    // ===

    private DFFixedTable(int width, int height, int[] columnWidths, boolean freeLastColumn, String[][] cells) {
        this.width = width;
        this.height = height;
        this.columnWidths = columnWidths;
        this.freeLastColumn = freeLastColumn;
        this.cells = cells;
    }

    // ===

    @Nonnull
    @Override
    public String format() {

        StringBuilder out = new StringBuilder();

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {

                String content = this.cells[x][y];
                int width = this.columnWidths[x];

                content = content == null ? " " : content;

                String cell;
                if(this.freeLastColumn && x == (this.width - 1))
                    cell = " " + content;
                else cell = "`\u200b" + Strings.box(content, width) + "\u200b`";

                out.append(cell).append(" ");
            }

            out.append("\n");
        }

        return out.toString();
    }

    // ===

    @Nonnull
    public static Builder newBuilder(int width, int height, int... columnWidths) {
        if(columnWidths.length != width) throw new IllegalArgumentException();
        return new Builder(width, height, columnWidths);
    }

    // ===

    public static class Builder {

        private final int width;
        private final int height;

        private final int[] columnWidths;

        private boolean freeLastColumn = false;

        private final String[][] cells;

        // ===

        private Builder(int width, int height, int[] columnWidths) {
            this.width = width;
            this.height = height;
            this.columnWidths = columnWidths;
            this.cells = new String[this.width][this.height];
        }

        // ===

        @Nonnull
        public Builder setFreeLastColumn(boolean freeLastColumn) {
            this.freeLastColumn = freeLastColumn;
            return this;
        }

        //

        public void set(int x, int y, @Nonnull String content) {
            this.cells[x][y] = content;
        }

        public void setRow(int y, @Nonnull String... content) {
            if(y >= this.height) ensureSize(y, this.height);
            for (int x = 0; x < this.width; x++) {
                set(x, y, content[x]);
            }
        }

        public void setColumn(int x, @Nonnull String... content) {
            if(x >= this.height) ensureSize(x, this.height);
            for (int y = 0; y < this.height; y++) {
                set(x, y, content[y]);
            }
        }

        //

        private void ensureSize(int width, int height) {

        }

        // ===

        @Nonnull
        public DFFixedTable build() {
            return new DFFixedTable(width, height, columnWidths, freeLastColumn, cells);
        }
    }
}
