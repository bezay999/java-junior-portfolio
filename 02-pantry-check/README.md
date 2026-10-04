# Pantry Check

I kept forgetting what was already in the kitchen and buying another one. Pantry Check is a tiny command-line list for that problem: add an item with its expiry date, see what needs attention soon, and remove it when it is used.

It is intentionally local and single-user. No account, database, or external library is needed.

## Run it

JDK 17 or newer is enough. From this folder:

```bash
mkdir -p out
javac --release 17 -d out src/*.java
java -cp out Main add "Greek yogurt" 2 2026-10-12
java -cp out Main add "Rice" 1 2027-04-01
java -cp out Main list
java -cp out Main soon 7
java -cp out Main expired
java -cp out Main remove 1
```

`soon 7` shows items expiring from today through seven days from now. Already expired items appear under `expired`, not under `soon`. `list` sorts by expiry date, then item ID.

Items are saved in `data/pantry.tsv`, which Git ignores. Set `PANTRY_FILE` if you want another location. Names can be up to 80 characters and cannot contain tabs or line breaks.

## Check the behavior

```bash
javac --release 17 -d out src/*.java test/*.java
java -cp out PantryTest
```

The test checks saving and reloading, stable IDs, expiry filters, and removal. The app uses the machine's local date for `soon` and `expired`.
