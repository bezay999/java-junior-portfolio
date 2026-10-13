# Weather Notes

I wanted a small report from observations I wrote down myself, without a weather API. This program reads daily temperatures and rainfall, summarizes a date range, or compares two seven-day windows.

With JDK 17+, run from this folder:

```bash
mkdir -p out
javac --release 17 -d out src/*.java
java -cp out Main summary samples/weather.csv 2026-10-01 2026-10-07
java -cp out Main compare samples/weather.csv 2026-10-01 2026-10-08
```

The CSV has a `date,tempC,rainMm` header and one observation per date. Blank ranges are reported as having no data; comparisons require at least one observation in each window. These are sample notes, not a forecast.

To run the checks, compile `src/*.java test/*.java` into `out` and run `java -cp out WeatherTest`.
