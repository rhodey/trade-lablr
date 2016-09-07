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
import org.junit.Test;

public class PriceDiffLabelProviderTest {

  private OrderEvent mockTake(long timeMs, long price) {
    return new OrderEvent(OrderEvent.Type.TAKE, timeMs, -1l, "idk", Order.Side.ASK, price, -1l);
  }

  private OrderEvent mockOpen(long timeMs, long price) {
    return new OrderEvent(OrderEvent.Type.OPEN, timeMs, -1l, "idk", Order.Side.ASK, price, -1l);
  }

  @Test
  public void testFuture() {
    final LabelProvider LABELER = new PriceDiffLabelProvider(100l);

    LABELER.initEventCount(5);
    LABELER.indexEvent(0, mockTake(0l,       1l));
    LABELER.indexEvent(1, mockOpen(25l,     13l));
    LABELER.indexEvent(2, mockTake(50l,    133l));
    LABELER.indexEvent(3, mockTake(75l,   1337l));
    LABELER.indexEvent(4, mockTake(100l, 31337l));

    assert LABELER.labelFor(0).get().getValue() ==  1336l;
    assert !LABELER.labelFor(1).isPresent();
    assert LABELER.labelFor(2).get().getValue() == 31204l;
    assert LABELER.labelFor(3).get().getValue() == 30000l;
    assert !LABELER.labelFor(4).isPresent();
  }

  @Test
  public void testPast() {
    final LabelProvider LABELER = new PriceDiffLabelProvider(-10l);

    LABELER.initEventCount(5);
    LABELER.indexEvent(0, mockTake(0l,      1l));
    LABELER.indexEvent(1, mockOpen(25l,    13l));
    LABELER.indexEvent(2, mockTake(30l,   133l));
    LABELER.indexEvent(3, mockTake(39l,  1337l));
    LABELER.indexEvent(4, mockTake(49l, 31337l));

    assert !LABELER.labelFor(0).isPresent();
    assert !LABELER.labelFor(1).isPresent();
    assert !LABELER.labelFor(2).isPresent();
    assert LABELER.labelFor(3).get().getValue() == 1204l;
    assert !LABELER.labelFor(4).isPresent();
  }

}
