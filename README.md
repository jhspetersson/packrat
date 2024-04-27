# Packrat

Packrat is a Java library that provides various [Gatherer](https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/util/stream/Gatherer.html) implementations for the Stream API. Gatherers can enhance streams with custom intermediate operations.

### Availability
---

> [!IMPORTANT]
> You will need a very fresh JDK version with preview features enabled to actually use Gatherers.

|JEP|JDK|Status|
|---|---|---|
|[461](https://openjdk.org/jeps/461)|22|Preview|
|[473](https://openjdk.org/jeps/473)|23|Second Preview|

### Gatherers
---

**ShuffleGatherer** - shuffle the elements

```java
  import static jhspetersson.packrat.Packrat.shuffle;
  var randomlyOrdered = IntStream.range(0, 10).boxed().gather(shuffle()).toList();
  System.out.println(randomlyOrdered);
```
> [2, 7, 6, 9, 8, 5, 1, 3, 0, 4]
