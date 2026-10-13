# Flashcard Desk

A quick practice session without an account or a browser tab. The cards come from a small tab-separated deck. Questions appear in a shuffled order; any missed card returns in the next round until you get it right.

With JDK 17+, run from this folder:

```bash
mkdir -p out
javac --release 17 -d out src/*.java
java -cp out Main samples/java-basics.tsv
```

Answers ignore capitalization and surrounding spaces. A deck has one `question<TAB>answer` pair per line. Tabs and line breaks cannot be part of a question or answer. Use Ctrl-D (or Ctrl-Z then Enter on Windows) to end early.

To run the checks, compile `src/*.java test/*.java` into `out` and run `java -cp out FlashcardTest`.
