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

public class TakeVolumeLabelProviderTest {

  private OrderEvent mockTake(long timeMs, long size) {
    return new OrderEvent(OrderEvent.Type.TAKE, timeMs, -1l, "idk", Order.Side.ASK, -1, size);
  }

  private OrderEvent mockOpen(long timeMs, long size) {
    return new OrderEvent(OrderEvent.Type.OPEN, timeMs, -1l, "idk", Order.Side.ASK, -1, size);
  }

  @Test
  public void testFuture() {
    final LabelProvider LABELER = new TakeVolumeLabelProvider(Order.Side.ASK, 100l);

    LABELER.initEventCount(6);
    LABELER.indexEvent(0, mockTake(0l,       1l));
    LABELER.indexEvent(1, mockOpen(25l,     13l));
    LABELER.indexEvent(2, mockTake(50l,    133l));
    LABELER.indexEvent(3, mockTake(75l,   1337l));
    LABELER.indexEvent(4, mockTake(100l, 31337l));
    LABELER.indexEvent(5, mockOpen(25l,     13l));

    assert LABELER.labelFor(0).getValue() ==  1471l;
    assert LABELER.labelFor(1).getValue() == 32807l;
    assert LABELER.labelFor(2).getValue() == 32807l;
    assert LABELER.labelFor(3).getValue() == 32674l;
    assert LABELER.labelFor(4).getValue() == 31337l;
    assert LABELER.labelFor(5).getValue() ==     0l;
  }

  @Test
  public void testPast() {
    final LabelProvider LABELER = new TakeVolumeLabelProvider(Order.Side.ASK, -10l);

    LABELER.initEventCount(5);
    LABELER.indexEvent(0, mockTake(0l,      1l));
    LABELER.indexEvent(1, mockOpen(25l,    13l));
    LABELER.indexEvent(2, mockTake(30l,   133l));
    LABELER.indexEvent(3, mockTake(39l,  1337l));
    LABELER.indexEvent(4, mockTake(49l, 31337l));

    assert LABELER.labelFor(0).getValue() ==     1l;
    assert LABELER.labelFor(1).getValue() ==     0l;
    assert LABELER.labelFor(2).getValue() ==   133l;
    assert LABELER.labelFor(3).getValue() ==  1470l;
    assert LABELER.labelFor(4).getValue() == 31337l;
  }

}
