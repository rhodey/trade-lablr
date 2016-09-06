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

import org.anhonesteffort.trading.io.ChronicleProtoSlicer;
import org.anhonesteffort.trading.label.LabelProvider;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Labels {

  private static final ExecutorService pool = Executors.newFixedThreadPool(1);
  private final String[] labelArgs;

  public Labels(String[] labelArgs) {
    this.labelArgs = labelArgs;
  }

  public void run() throws Exception {
    List<LabelProvider> labels =
        Arrays.stream(labelArgs)
        .map(LabelProvider::parseFrom)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());

    labels.forEach(label -> System.out.println("parsed label " + label.getName() + "."));

    try (ChronicleProtoSlicer slicer = new ChronicleProtoSlicer()) {
      System.out.println("slicing chronicle...");
      List<File> slices = pool.submit(slicer).get();

      System.out.println("labeling protobufs...");
      for (int i = 0; i < slices.size(); i++) {
        System.out.println("labeling slice #" + (i + 1) + "...");
        pool.submit(new ProtoLabeler(slices.get(i), labels)).get();
      }

    } finally {
      pool.shutdownNow();
    }
  }

  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.out.println("$ java -jar target/trade-labeler-0.1.jar <label> <label> <label>");
      System.exit(1);
    } else {
      new Labels(args).run();
    }
  }

}
