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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

public class ProtoLabeler implements Callable<File> {

  private final File input;
  private final File output;
  private final List<LabelProvider> labelers;

  public ProtoLabeler(File input, List<LabelProvider> labelers) {
    this.input    = input;
    this.labelers = labelers;
    output        = new File(input.getAbsolutePath() + ".labeled");
  }

  private void count(ProtoReader reader) throws IOException {
    int eventCount = 0;
    Optional<OrderEvent> event = reader.readNext();

    while (event.isPresent()) {
      eventCount++;
      event = reader.readNext();
    }

    for (LabelProvider labeler : labelers) { labeler.initEventCount(eventCount); }
  }

  private void index(ProtoReader reader) throws IOException {
    int eventIndex = 0;
    Optional<OrderEvent> event = reader.readNext();

    while (event.isPresent()) {
      for (LabelProvider labeler : labelers) { labeler.indexEvent(eventIndex, event.get()); }
      eventIndex++;
      event = reader.readNext();
    }
  }

  private void label(ProtoReader reader, ProtoWriter writer) throws IOException {
    int eventIndex = 0;
    Optional<OrderEvent> event = reader.readNext();

    while (event.isPresent()) {
      Label[] labels = new Label[labelers.size()];
      for (int i = 0; i < labels.length; i++) { labels[i] = labelers.get(i).labelFor(eventIndex); }
      writer.writeLabeledEvent(event.get(), labels);
      eventIndex++;
      event = reader.readNext();
    }
  }

  @Override
  public File call() throws IOException {
    try (ProtoReader reader = new ProtoReader(new FileInputStream(input))) {
      count(reader);
    }

    try (ProtoReader reader = new ProtoReader(new FileInputStream(input))) {
      index(reader);
    }

    try (ProtoReader reader = new ProtoReader(new FileInputStream(input));
         ProtoWriter writer = new ProtoWriter(output))
    {
      label(reader, writer);
    }

    return output;
  }

}
