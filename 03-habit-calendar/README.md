# Habit Calendar

This is a small habit log for days when a full productivity app feels like too much. Add a habit, mark a day as done, and check whether you are keeping a streak. It works offline and keeps everything in one local file.

## Run it

You need JDK 17 or newer. From this folder:

```bash
mkdir -p out
javac --release 17 -d out src/*.java
java -cp out Main add "Read 20 minutes"
java -cp out Main mark 1
java -cp out Main mark 1 2026-10-03
java -cp out Main list
java -cp out Main history 1
```

`mark ID` uses today's date. Add `YYYY-MM-DD` to mark an earlier day; future dates and duplicate check-ins are rejected. In `list`, a streak counts consecutive days ending today or yesterday. That way, it does not disappear first thing in the morning before you have had a chance to do the habit.

Habits are saved in `data/habits.tsv`, which Git ignores. Set `HABIT_FILE` to store them elsewhere. Habit names can be up to 80 characters and cannot contain tabs or line breaks.

## Run the checks

```bash
javac --release 17 -d out src/*.java test/*.java
java -cp out HabitTest
```

The test checks saving and reloading, duplicate dates, future dates, and streaks. This is a single-user tool; it is not designed for two processes editing the same file at once.
