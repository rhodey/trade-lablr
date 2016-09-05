/*
 * Copyright (C) 2016 An Honest Effort LLC.
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.anhonesteffort.trading.label;

import org.anhonesteffort.trading.proto.OrderEvent;

public class TimeDiffLabelProvider extends LabelProvider {

  public TimeDiffLabelProvider() {
    super("time-diff-ns");
  }

  @Override
  protected long historicValueFor(OrderEvent event) {
    return event.getTimeNs();
  }

  @Override
  public long labelValueFor(int eventIndex) {
    if (eventIndex == 0) {
      return 0l;
    } else {
      return valueHistory[eventIndex] - valueHistory[eventIndex - 1];
    }
  }

}
