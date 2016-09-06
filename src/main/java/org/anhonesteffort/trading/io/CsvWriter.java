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

import org.anhonesteffort.trading.label.LabelProvider;
import org.anhonesteffort.trading.proto.Label;
import org.anhonesteffort.trading.proto.OrderEvent;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvWriter implements Closeable {

  private final FileWriter output;

  public CsvWriter(File file) throws IOException{
    if (file.exists()) {
      if (!file.delete() || !file.createNewFile()) {
        throw new IOException("failed to replace existing output file " + file.getName());
      }
    }
    output = new FileWriter(file);
  }

  public void writeHeader(List<LabelProvider> labelers) throws IOException {
    StringBuilder builder = new StringBuilder();

    builder.append("type,timeMs,timeNs,orderId,side,price,size");
    labelers.forEach(label -> {
      builder.append(",");
      builder.append(label.getName());
    });

    builder.append("\n");
    output.write(builder.toString());
  }

  public void writeLabeledEvent(OrderEvent event, List<Label> labels) throws IOException {
    StringBuilder builder = new StringBuilder();

    builder.append(event.getType().name());
    builder.append(",");
    builder.append(event.getTimeMs());
    builder.append(",");
    builder.append(event.getTimeNs());
    builder.append(",");
    builder.append(event.getOrderId());
    builder.append(",");
    builder.append(event.getSide().name());
    builder.append(",");
    builder.append(event.getPrice());
    builder.append(",");
    builder.append(event.getSize());

    labels.forEach(label -> {
      builder.append(",");
      builder.append(label.getValue());
    });

    builder.append("\n");
    output.write(builder.toString());
  }

  @Override
  public void close() throws IOException {
    output.close();
  }

}
