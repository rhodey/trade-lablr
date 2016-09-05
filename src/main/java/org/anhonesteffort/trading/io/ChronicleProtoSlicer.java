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

import org.anhonesteffort.trading.proto.OrderEvent;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.Callable;

public class ChronicleProtoSlicer implements Callable<List<File>>, Closeable {

  private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd.HH:mm");
  private final ChronicleReader input = new ChronicleReader();
  private final List<File> outputs = new LinkedList<>();

  private ProtoWriter writer;
  private long sliceStartMs;
  private long sliceEndMs;

  public ChronicleProtoSlicer() {
    dateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
  }

  private String fileNameFor(OrderEvent event) {
    return "persistence-out/events-" + dateFormat.format(new Date(event.getTimeMs()));
  }

  private File renameWithDuration(File file, long durationMs) throws IOException {
    File renamed = new File(file.getAbsolutePath() + "." + ((durationMs / 1000l) / 60l) + ".protos");
    if (!file.renameTo(renamed)) {
      throw new IOException("failed to add duration to filename");
    } else {
      return renamed;
    }
  }

  @Override
  public List<File> call() throws IOException {
    Optional<OrderEvent> event = input.readNext();

    while (event.isPresent()) {
      switch (event.get().getType()) {
        case SYNC_START:
          if (writer != null) {
            writer.close();
            outputs.add(renameWithDuration(writer.getFile(), sliceEndMs - sliceStartMs));
          }
          System.out.println("writing slice #" + (outputs.size() + 1) + "...");
          writer       = new ProtoWriter(new File(fileNameFor(event.get())));
          sliceStartMs = event.get().getTimeMs();

        default:
          writer.writeEvent(event.get());
          sliceEndMs = event.get().getTimeMs();
          event      = input.readNext();
      }
    }

    writer.close();
    outputs.add(renameWithDuration(writer.getFile(), sliceEndMs - sliceStartMs));
    return outputs;
  }

  @Override
  public void close() throws IOException {
    input.close();
    if (writer != null) { writer.close(); }
  }

}
