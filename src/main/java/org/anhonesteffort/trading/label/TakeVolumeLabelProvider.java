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

import org.anhonesteffort.trading.book.Order;
import org.anhonesteffort.trading.proto.OrderEvent;

import java.util.Optional;

public class TakeVolumeLabelProvider extends LabelProvider {

  private final Order.Side side;
  private final long periodMs;

  public TakeVolumeLabelProvider(Order.Side side, long periodMs) {
    super("take_volume_" + side.name() + "_" + periodMs);
    this.side     = side;
    this.periodMs = periodMs;
  }

  @Override
  protected long historicValueFor(OrderEvent event) {
    return (event.getType().equals(OrderEvent.Type.TAKE) && side.equals(event.getSide())) ? event.getSize() : 0l;
  }

  @Override
  public Optional<Long> labelValueFor(int eventIndex) {
    long sum   = 0l;
    int  index = eventIndex;

    if (periodMs > 0) {
      while (index < times.length && (times[index] - times[eventIndex]) < periodMs) {
        sum += valueHistory[index++];
      }
    } else {
      while (index >= 0 && (times[eventIndex] - times[index]) < Math.abs(periodMs)) {
        sum += valueHistory[index--];
      }
    }

    return Optional.of(sum);
  }

}
