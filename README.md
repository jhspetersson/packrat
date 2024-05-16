# Packrat

Packrat is a Java library that provides various [Gatherer](https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/util/stream/Gatherer.html) implementations for the Stream API. Gatherers can enhance streams with custom intermediate operations.

[Introduction to the Gatherers by Viktor Klang](https://www.youtube.com/watch?v=8fMFa6OqlY8)

### Availability

> [!IMPORTANT]
> You will need a very fresh JDK version with preview features enabled to actually use Gatherers.

|JEP|JDK|Status|
|---|---|---|
|[461](https://openjdk.org/jeps/461)|22|Preview|
|[473](https://openjdk.org/jeps/473)|23|Second Preview|

### Gatherers

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

**increasing()** - returns elements in an increasing sequence, elements out of the sequence, as well as repeating values, are dropped

```java
  import static jhspetersson.packrat.Packrat.increasing;
  var numbers = Stream.of(1, 2, 2, 5, 4, 2, 6, 9, 3, 11, 0, 1, 20);
  var increasingNumbers = numbers.gather(increasing()).toList();
  System.out.println(increasingNumbers);
```

> [1, 2, 5, 6, 9, 11, 20]

**increasingOrEqual()** - returns elements in an increasing sequence, repeating values are preserved, elements out of the sequence are dropped

**decreasing()** - returns elements in a decreasing sequence, elements out of the sequence, as well as repeating values, are dropped

**decreasingOrEqual()** - returns elements in a decreasing sequence, repeating values are preserved, elements out of the sequence are dropped

**increasingChunks()** - returns lists ("chunks") of elements, where each next element is greater than the previous one

```java
  import static jhspetersson.packrat.Packrat.increasingChunks;
  var numbers = Stream.of(1, 2, 2, 5, 4, 2, 6, 9, 3, 11, 0, 1, 20);
  var result = numbers.gather(increasingChunks()).toList();
  System.out.println(result);
```

> [[1, 2], [2, 5], [4], [2, 6, 9], [3, 11], [0, 1, 20]]

**increasingOrEqualChunks()** - returns lists ("chunks") of elements, where each next element is greater or equal than the previous one

```java
  import static jhspetersson.packrat.Packrat.increasingOrEqualChunks;
  var numbers = Stream.of(1, 2, 2, 5, 4, 2, 6, 9, 3, 11, 0, 1, 20);
  var result = numbers.gather(increasingOrEqualChunks()).toList();
  System.out.println(result);
```

> [[1, 2, 2, 5], [4], [2, 6, 9], [3, 11], [0, 1, 20]]


**decreasingChunks()** - returns lists ("chunks") of elements, where each next element is less than the previous one

**decreasingOrEqualChunks()** - returns lists ("chunks") of elements, where each next element is less or equal than the previous one

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

**atLeast**(__n__) - returns distinct elements that appear at least __n__ times in the stream

```java
  import static jhspetersson.packrat.Packrat.atLeast;
  var numbers = Stream.of(1, 2, 3, 3, 3, 4, 5, 5, 6, 7, 8, 8, 8, 8, 9, 10);
  var atLeastThree = numbers.gather(atLeast(3)).toList();
  System.out.println(atLeastThree);
```
> [3, 3, 3, 8, 8, 8, 8]

**mapFirst(__mapper__)** - returns all elements, the first element is mapped with the supplied mapping function

```java
  import static jhspetersson.packrat.Packrat.mapFirst;
  var mapped = IntStream.rangeClosed(1, 10).boxed().gather(mapFirst(n -> n * 10)).toList();
  System.out.println(mapped);
```

> [10, 2, 3, 4, 5, 6, 7, 8, 9, 10]

**mapN(__mapN__, __mapper__)** - returns all elements, the first __mapN__ elements are mapped with the supplied mapping function

```java
  import static jhspetersson.packrat.Packrat.mapN;
  var mapped = IntStream.rangeClosed(1, 10).boxed().gather(mapN(5, n -> n * 10)).toList();
  System.out.println(mapped);
```

> [10, 20, 30, 40, 50, 6, 7, 8, 9, 10]

**skipAndMap(__skipN__, __mapper__)** - returns all elements that after the first __skipN__ are mapped with the supplied mapping function

```java
  import static jhspetersson.packrat.Packrat.skipAndMap;
  var mapped = IntStream.rangeClosed(1, 10).boxed().gather(skipAndMap(3, n -> n * 10)).toList();
  System.out.println(mapped);
```

> [1, 2, 3, 40, 50, 60, 70, 80, 90, 100]

**skipAndMapN(__skipN__, __mapN__, __mapper__)** - returns all elements, after skipN elements the first mapN elements are mapped with the supplied mapping function

```java
  import static jhspetersson.packrat.Packrat.skipAndMapN;
  var mapped = IntStream.rangeClosed(1, 10).boxed().gather(skipAndMapN(3, 5, n -> n * 10)).toList();
  System.out.println(mapped);
```

> [1, 2, 3, 40, 50, 60, 70, 80, 9, 10]

**flatMapIf(__mapper__, __predicate__)** - optionally flattens elements mapped to streams depending on the supplied predicate

```java
  import static jhspetersson.packrat.Packrat.flatMapIf;
  var strings = Stream.of("A", "B", "CDE");
  var result = strings.gather(flatMapIf(s -> Arrays.stream(s.split("")), s -> s.length() > 1)).toList();
  System.out.println(result);
```

> [A, B, C, D, E]

**zip(__input__, __mapper__)** - returns elements mapped ("zipped") with the values from some other stream or iterable

```java
  import static jhspetersson.packrat.Packrat.zip;
  var names = List.of("Anna", "Mike", "Sandra");
  var ages = Stream.of(20, 30, 40, 50, 60, 70, 80, 90);
  var users = names.stream().gather(zip(ages, User::new)).toList();
  System.out.println(users);
```

> [User[name=Anna, age=20], User[name=Mike, age=30], User[name=Sandra, age=40]]

**asGatherer(__collector__)** - provides the result of the supplied collector as a single element into the stream, effectively converts any Collector into a Gatherer

```java
  import static jhspetersson.packrat.Packrat.asGatherer;
  var numbers = Stream.of(1, 2, 3, 4, 5);
  var listOfCollectedList = numbers.gather(asGatherer(Collectors.toList())).toList();
  System.out.println(listOfCollectedList);
```

> [[1, 2, 3, 4, 5]]

### License

Apache-2.0

---
Supported by [JetBrains IDEA](https://jb.gg/OpenSourceSupport) open source license.
