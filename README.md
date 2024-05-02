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

**distinctBy(_mapper_)** - returns elements with distinct values that result from a mapping by the supplied function

```java
  import static jhspetersson.packrat.Packrat.distinctBy;
  var oneOddOneEven = IntStream.range(1, 10).boxed().gather(distinctBy(i -> i % 2)).toList();
  System.out.println(oneOddOneEven);
```
> [1, 2]

**filterBy(_mapper_, _value_)** - filters mapped elements based on the equality to the value, stream continues with original elements

```java
  import static jhspetersson.packrat.Packrat.filterBy;
  var oneDigitNumbers = IntStream.range(0, 100).boxed().gather(filterBy(i -> i.toString().length(), 1)).toList();
  System.out.println(oneDigitNumbers);
```
> [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]

**filterBy(_mapper_, _value_, _predicate_)** - filters mapped elements based on the predicate test against the value, stream continues with original elements

```java
  import static jhspetersson.packrat.Packrat.filterBy;
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

**rotate(_distance_)** - rotates the elements

```java
  import static jhspetersson.packrat.Packrat.rotate;
  var positiveRotation = IntStream.range(0, 10).boxed().gather(rotate(3)).toList();
  System.out.println(positiveRotation);
  var negativeRotation = IntStream.range(0, 10).boxed().gather(rotate(-4)).toList();
  System.out.println(negativeRotation);
```
> [7, 8, 9, 0, 1, 2, 3, 4, 5, 6]

> [4, 5, 6, 7, 8, 9, 0, 1, 2, 3]

**shuffle()** - shuffle the elements

```java
  import static jhspetersson.packrat.Packrat.shuffle;
  var randomlyOrdered = IntStream.range(0, 10).boxed().gather(shuffle()).toList();
  System.out.println(randomlyOrdered);
```
> [2, 7, 6, 9, 8, 5, 1, 3, 0, 4]

**chars()** - returns characters as strings parsed from the stream elements
  
```java
  import static jhspetersson.packrat.Packrat.chars;
  var charStrings = Stream.of("Hello, \uD83D\uDC22!").gather(chars()).toList();
  System.out.println(charStrings);
```

> [H, e, l, l, o, ,,  , 🐢, !]

**words()** - returns words as strings parsed from the stream elements

```java
  import static jhspetersson.packrat.Packrat.words;
  var wordStrings = Stream.of("Another test!").gather(words()).toList();
  System.out.println(wordStrings);
```

> [Another, test, !]

**sentences()** - returns sentences as strings parsed from the stream elements

```java
  import static jhspetersson.packrat.Packrat.sentences;
  var sentenceStrings = Stream.of("And another one. How many left?").gather(sentences()).toList();
  System.out.println(sentenceStrings);
```

> [And another one. , How many left?]

**nCopies(__n__)** - returns __n__ copies of every element, __n__ less than or equal to zero effectively empties the stream

```java
  import static jhspetersson.packrat.Packrat.nCopies;
  var numbers = IntStream.of(5).boxed().gather(nCopies(10)).toList();
  System.out.println(numbers);
```

> [5, 5, 5, 5, 5, 5, 5, 5, 5, 5]

### License
---

Apache-2.0

---
Supported by [JetBrains IDEA](https://jb.gg/OpenSourceSupport) open source license.
