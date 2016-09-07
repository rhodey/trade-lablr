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

public class PriceDiffLabelProvider extends LabelProvider {

  private final long periodMs;

  public PriceDiffLabelProvider(long periodMs) {
    super("price_diff_" + periodMs);
    this.periodMs = periodMs;
  }

  @Override
  protected long historicValueFor(OrderEvent event) {
    return event.getType().equals(OrderEvent.Type.TAKE) ? event.getPrice() : -1l;
  }

  @Override
  public long labelValueFor(int eventIndex) {
    if (valueHistory[eventIndex] <= 0l) { return -1l; }
    long last  = -1l;
    int  index = eventIndex;

    if (periodMs > 0l) {
      index++;
      while (index < times.length && (times[index] - times[eventIndex]) < periodMs) {
        last   = (valueHistory[index] > 0l) ? valueHistory[index] : last;
        index += 1;
      }
      return (last > 0l) ? (last - valueHistory[eventIndex]) : -1l;
    } else {
      index--;
      while (index >= 0 && (times[eventIndex] - times[index]) < Math.abs(periodMs)) {
        last   = (valueHistory[index] > 0l) ? valueHistory[index] : last;
        index -= 1;
      }
      return (last > 0l) ? (valueHistory[eventIndex] - last) : -1l;
    }
  }

}
