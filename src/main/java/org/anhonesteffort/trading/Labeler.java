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

package org.anhonesteffort.trading;

import org.anhonesteffort.trading.io.CsvWriter;
import org.anhonesteffort.trading.io.ProtoReader;
import org.anhonesteffort.trading.io.ProtoWriter;
import org.anhonesteffort.trading.label.LabelProvider;
import org.anhonesteffort.trading.proto.Label;
import org.anhonesteffort.trading.proto.OrderEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

public class Labeler implements Callable<Integer> {

  private final File input;
  private final File outputProto;
  private final File outputCsv;
  private final List<LabelProvider> labelers;

  public Labeler(File input, List<LabelProvider> labelers) {
    this.input    = input;
    this.labelers = labelers;
    outputProto   = new File(input.getAbsolutePath() + ".labeled");
    outputCsv     = new File(input.getAbsolutePath() + ".labeled.csv");
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

  private int label(ProtoReader reader, ProtoWriter protoWriter, CsvWriter csvWriter) throws IOException {
    int eventIndex = 0;
    List<Label> labels = new LinkedList<>();
    Optional<OrderEvent> event = reader.readNext();

    csvWriter.writeHeader(labelers);

    while (event.isPresent()) {
      for (LabelProvider labeler : labelers) { labels.add(labeler.labelFor(eventIndex)); }
      protoWriter.writeLabeledEvent(event.get(), labels);
      csvWriter.writeLabeledEvent(event.get(), labels);
      eventIndex++;
      labels.clear();
      event = reader.readNext();
    }

    return eventIndex;
  }

  @Override
  public Integer call() throws IOException {
    try (ProtoReader reader = new ProtoReader(new FileInputStream(input))) {
      count(reader);
    }

    try (ProtoReader reader = new ProtoReader(new FileInputStream(input))) {
      index(reader);
    }

    try (ProtoReader reader      = new ProtoReader(new FileInputStream(input));
         ProtoWriter protoWriter = new ProtoWriter(outputProto);
         CsvWriter   csvWriter   = new CsvWriter(outputCsv))
    {
      return label(reader, protoWriter, csvWriter);
    }
  }

}
