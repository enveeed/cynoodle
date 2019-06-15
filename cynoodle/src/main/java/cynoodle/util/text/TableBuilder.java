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

package cynoodle.util.text;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class TableBuilder {

    // ===

    private final int[] widths;

    private final List<String[]> rows = new ArrayList<>();

    // ===

    public TableBuilder(int... widths) {
        if(widths.length == 0) throw new IllegalArgumentException();
        this.widths = widths;
    }

    // ===

    public int getColumnCount() {
        return this.widths.length;
    }

    public int getRowCount() {
        return this.rows.size();
    }

    // ===

    public void add(@Nonnull String... cells) {
        if(cells.length != getColumnCount()) throw new IllegalArgumentException();
        this.rows.add(cells.clone());
    }

    public void add(int index, @Nonnull String... cells) {
        if(cells.length != getColumnCount()) throw new IllegalArgumentException();
        this.rows.add(index, cells.clone());
    }

    // ===

    /**
     * Build the table.
     * @return the string containing the table
     */
    @Nonnull
    public String build(@Nonnull Formatter formatter) {

        StringBuilder out = new StringBuilder();

        for (String[] row : this.rows) {
            out.append(formatter.formatRow(this.widths, row));
            out.append("\n");
        }

        return out.toString();
    }

    // ===

    public interface Formatter {

        @Nonnull
        String formatRow(int[] widths, @Nonnull String[] cells);

    }
}
