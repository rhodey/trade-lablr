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

import org.anhonesteffort.trading.proto.Label;
import org.anhonesteffort.trading.proto.OrderEvent;
import org.anhonesteffort.trading.proto.TradingProtoFactory;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ProtoWriter implements Closeable {

  private final TradingProtoFactory proto = new TradingProtoFactory();
  private final File file;
  private final OutputStream output;

  public ProtoWriter(File file) throws IOException{
    this.file = file;
    if (file.exists()) {
      if (!file.delete() || !file.createNewFile()) {
        throw new IOException("failed to replace existing output file " + file.getName());
      }
    }
    output = new FileOutputStream(file);
  }

  public File getFile() {
    return file;
  }

  public void writeEvent(OrderEvent event) throws IOException {
    proto.orderEvent(event).writeDelimitedTo(output);
  }

  public void writeLabeledEvent(OrderEvent event, Label... labels) throws IOException {
    proto.labeledOrderEvent(event, labels).writeDelimitedTo(output);
  }

  @Override
  public void close() throws IOException {
    output.close();
  }

}
