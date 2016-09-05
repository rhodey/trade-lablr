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

import org.anhonesteffort.trading.book.Order;
import org.anhonesteffort.trading.proto.OrderEvent;
import org.anhonesteffort.trading.proto.TradingProto;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static org.anhonesteffort.trading.proto.TradingProto.BaseMessage;

public class ProtoReader implements Closeable {

  private final InputStream input;

  public ProtoReader(InputStream input) {
    this.input = input;
  }

  private OrderEvent.Type typeFor(TradingProto.OrderEvent.Type type) {
    switch (type) {
      case OPEN:
        return OrderEvent.Type.OPEN;
      case TAKE:
        return OrderEvent.Type.TAKE;
      case REDUCE:
        return OrderEvent.Type.REDUCE;
      case SYNC_START:
        return OrderEvent.Type.SYNC_START;
      default:
        return OrderEvent.Type.SYNC_END;
    }
  }

  private Order.Side sideFor(TradingProto.OrderEvent.Side side) {
    if (side == TradingProto.OrderEvent.Side.ASK) {
      return Order.Side.ASK;
    } else {
      return Order.Side.BID;
    }
  }

  public Optional<OrderEvent> readNext() throws IOException {
    BaseMessage message = BaseMessage.parseDelimitedFrom(input);
    if (message == null) {
      return Optional.empty();
    } else {
      return Optional.of(new OrderEvent(
          typeFor(message.getOrderEvent().getType()),
          message.getOrderEvent().getTimeMs(),
          message.getOrderEvent().getTimeNs(),
          message.getOrderEvent().getOrderId(),
          sideFor(message.getOrderEvent().getSide()),
          message.getOrderEvent().getPrice(),
          message.getOrderEvent().getSize()
      ));
    }
  }

  @Override
  public void close() throws IOException {
    input.close();
  }

}
