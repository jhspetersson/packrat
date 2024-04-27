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

**filterBy(_mapper_, _value_)** - filters mapped elements based on the equality to the value, stream continues with original elements

```java
  import static jhspetersson.packrat.Packrat.filterBy;
  var oneDigitNumbers = IntStream.range(0, 100).boxed().gather(filterBy(i -> i.toString().length(), 1)).toList();
  System.out.println(oneDigitNumbers);
```
> [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]

**filterBy(_mapper_, _value_, _predicate_)** - filters mapped elements based on the predicate test against the value, stream continues with original elements

```java
  var ffValue = IntStream.range(0, 1000).boxed().gather(filterBy(Integer::toHexString, "ff", String::equalsIgnoreCase)).toList();
  System.out.println(ffValue);
```
> [255]
        
**removeBy(_mapper_, _value_)** - removes mapped elements based on the equality to the value, stream continues with original elements

```java
  import static jhspetersson.packrat.Packrat.removeBy;
  var oneDigitNumbers = IntStream.range(0, 100).boxed().gather(removeBy(i -> i.toString().length(), 2)).toList();
  System.out.println(oneDigitNumbers);
```
> [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]

**removeBy(_mapper_, _value_, _predicate_)** - removes mapped elements based on the predicate test against the value, stream continues with original elements

```java
  import static jhspetersson.packrat.Packrat.removeBy;
  var ageDivisibleByThree = getEmployees().gather(removeBy(emp -> emp.age() % 3, 0, (i, value) -> !Objects.equals(i, value))).toList();
  System.out.println(ageDivisibleByThree);
```
> [Employee[name=Mark Bloom, age=21], Employee[name=Rebecca Schneider, age=24]]
  
**reverse()** - reverses the elements

```java
  import static jhspetersson.packrat.Packrat.reverse;
  var reverseOrdered = IntStream.range(0, 10).boxed().gather(reverse()).toList();
  System.out.println(reverseOrdered);
```
> [9, 8, 7, 6, 5, 4, 3, 2, 1, 0]


**shuffle()** - shuffle the elements

```java
  import static jhspetersson.packrat.Packrat.shuffle;
  var randomlyOrdered = IntStream.range(0, 10).boxed().gather(shuffle()).toList();
  System.out.println(randomlyOrdered);
```
> [2, 7, 6, 9, 8, 5, 1, 3, 0, 4]
