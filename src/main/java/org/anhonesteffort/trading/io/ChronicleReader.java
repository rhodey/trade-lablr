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

package org.anhonesteffort.trading.io;

import net.openhft.chronicle.wire.DocumentContext;
import org.anhonesteffort.trading.proto.OrderEvent;
import org.anhonesteffort.trading.chronicle.ChronicleTailer;

import java.io.IOException;
import java.util.Optional;

public class ChronicleReader extends ChronicleTailer {

  public ChronicleReader() {
    super("persistence-in");
  }

  public Optional<OrderEvent> readNext() throws IOException {
    try (DocumentContext context = tailer.readingDocument()) {
      if (context.isPresent()) {
        OrderEvent event = new OrderEvent();
        event.readExternal(context.wire().objectInput());
        return Optional.of(event);
      } else {
        return Optional.empty();
      }
    }
  }

}
