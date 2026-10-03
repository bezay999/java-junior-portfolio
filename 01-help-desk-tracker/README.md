# Help Desk Tracker

A small command-line tool for keeping track of support requests. Add a ticket when something breaks, list open or closed tickets, close one when the issue is resolved, and get a quick count. Tickets are saved to a local file, so they are still there the next time you run the program.

I chose this as the first project because the problem is simple to recognize, but it still needs more than a single loop: input validation, unique IDs, status changes, and file storage.

## Requirements

JDK 17 or newer. No external libraries.

## Build and run

Run these commands inside this folder:

```bash
mkdir -p out
javac --release 17 -d out src/*.java
java -cp out Main add "Printer on floor 2 is offline"
java -cp out Main list
java -cp out Main list open
java -cp out Main close 1
java -cp out Main list closed
java -cp out Main stats
```

The program writes tickets to `data/tickets.tsv`. This folder is ignored by Git. Ticket titles can be up to 100 characters and cannot contain tabs or line breaks.

To use another file, set `HELP_DESK_FILE` before running a command:

```bash
HELP_DESK_FILE=/tmp/my-tickets.tsv java -cp out Main list
```

## Run the checks

```bash
javac --release 17 -d out src/*.java test/*.java
java -cp out HelpDeskTest
```

The check covers adding tickets, keeping IDs unique after a restart, closing a ticket, and loading the saved data.

## Code map

- `Main` handles commands and prints results.
- `Ticket` holds one request and its status.
- `TicketService` applies the rules for adding and closing tickets.
- `TicketStore` reads and writes the data file.

This is a single-user local tool. It does not try to manage simultaneous edits to the same data file.

