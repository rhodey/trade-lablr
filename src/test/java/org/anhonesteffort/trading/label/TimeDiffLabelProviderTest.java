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
import org.junit.Test;

import java.util.stream.IntStream;

public class TimeDiffLabelProviderTest {

  @Test
  public void test() {
    final LabelProvider LABELER = new TimeDiffLabelProvider();
    LABELER.initEventCount(10);
    IntStream.range(0, 10).forEach(i -> LABELER.indexEvent(i, OrderEvent.syncStart(i * 100)));

    assert LABELER.labelFor(0).get().getValue() == 0l;
    IntStream.range(1, 10).forEach(i -> {
      assert LABELER.labelFor(i).get().getValue() == 100l;
    });
  }


}
