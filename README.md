# trade-lablr

Transform Chronicle-Queue logs into protobufs of labeled trade data that can be
use for training ML.

## Setup
```
$ git clone https://github.com/rhodey/trading-common
$ cd trading-common/
$ mvn install
```

## Build
```
$ gradle build
```

## Usage
Copy your Chronicle log file into the `persistence-in/` directory. The
Chronicle log will be split by `SYNC_START` events and written to files with
the naming convention of:
```
events-<year>-<month>-<day>.<hour>:<minute>.<duration-minutes>.protos
events-<year>-<month>-<day>.<hour>:<minute>.<duration-minutes>.protos.labeled
events-<year>-<month>-<day>.<hour>:<minute>.<duration-minutes>.protos.labeled.csv
```

Where `.protos` files contain length delimited `OrderEvent` protobuf messages
and `.protos.labeled` files contain length delimited  `LabeledOrderEvent`
protobuf messages. The labels written to `.protos.labeled` files are configured
via command line arguments in the form of:
```
$ java -jar build/libs/trade-lablr-0.1.jar <label> <label> <label>
```

### Label Time Difference
Use `<label>` argument `time-diff` to label each `OrderEvent` with the
nanosecond difference in time between this event and the event that preceded
it.

### Label Take Volume
Use `<label>` argument `take-volume:<side>:<periodMs>` to label each
`OrderEvent` with the total volume removed by `<side>` between the time of this
event and `<periodMs>` milliseconds in the future.

### Example
The following command will label each `OrderEvent` in the Chronicle log file
with total amount of volume bought 10 seconds in the future and total volume
sold 30 seconds in the future.
```
$ java -jar build/libs/trade-lablr-0.1.jar take-volume:bid:10000 take-volume:ask:30000
```

## License

Copyright 2016 An Honest Effort LLC

Licensed under the GPLv3: http://www.gnu.org/licenses/gpl-3.0.html
