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
import org.anhonesteffort.trading.proto.Label;
import org.anhonesteffort.trading.proto.OrderEvent;

import java.util.Optional;

public abstract class LabelProvider {

  private final String name;
  protected long[] times = null;
  protected long[] valueHistory = null;

  public LabelProvider(String name) {
    this.name = name;
  }

  public static Optional<LabelProvider> parseFrom(String string) {
    String[] parts = string.split(":");
    switch (parts[0]) {
      case "time_diff":
        return Optional.of(new TimeDiffLabelProvider());

      case "take_price":
        return Optional.of(new TakePriceLabelProvider(Long.parseLong(parts[1])));

      case "take_volume":
        if (parts[1].equals("ask")) {
          return Optional.of(new TakeVolumeLabelProvider(Order.Side.ASK, Long.parseLong(parts[2])));
        } else {
          return Optional.of(new TakeVolumeLabelProvider(Order.Side.BID, Long.parseLong(parts[2])));
        }

        default:
          return Optional.empty();
    }
  }

  public String getName() {
    return name;
  }

  public void initEventCount(int eventCount) {
    times        = new long[eventCount];
    valueHistory = new long[eventCount];
  }

  protected abstract long historicValueFor(OrderEvent event);

  public void indexEvent(int eventIndex, OrderEvent event) {
    times[eventIndex]        = event.getTimeMs();
    valueHistory[eventIndex] = historicValueFor(event);
  }

  protected abstract long labelValueFor(int eventIndex);

  public Label labelFor(int eventIndex) {
    return new Label(name, labelValueFor(eventIndex));
  }

}
