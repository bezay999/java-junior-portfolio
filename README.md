# Java projects, one problem at a time

This repository is my Java practice portfolio. The goal is to build ten small programs that solve recognizable problems, then make each one easy to run and easy to understand. I am starting with command-line tools because they let me focus on the logic before adding a user interface.

The projects are independent. You can open any folder without setting up the others. All ten are ready to try.

## Projects

| # | Project | What it does | Main Java practice | Status |
|---|---|---|---|---|
| 01 | [Help Desk Tracker](01-help-desk-tracker/README.md) | Create, close, and review support tickets saved between runs. | Classes, records, collections, file I/O | Done |
| 02 | [Pantry Check](02-pantry-check/README.md) | Track groceries and show what expires soon. | Dates, sorting, validation | Done |
| 03 | [Habit Calendar](03-habit-calendar/README.md) | Record daily habits and calculate streaks. | Sets, dates, small reports | Done |
| 04 | [Shared Bill Splitter](04-shared-bill-splitter/README.md) | Split a group bill fairly, including rounding. | Decimal arithmetic, edge cases | Done |
| 05 | [Event Check-In](05-event-check-in/README.md) | Import a guest list and record arrivals. | CSV files, searching, duplicate handling | Done |
| 06 | [Flashcard Desk](06-flashcard-desk/README.md) | Practice a question deck and revisit missed cards. | Collections, randomization, state | Done |
| 07 | [Log Lens](07-log-lens/README.md) | Read an application log and summarize errors. | Text parsing, streams, reporting | Done |
| 08 | [Route Planner](08-route-planner/README.md) | Find a route through a small network of stops. | Graphs, breadth-first search | Done |
| 09 | [Weather Notes](09-weather-notes/README.md) | Store daily observations and compare weeks. | Data modeling, aggregation | Done |
| 10 | [Job Search Board](10-job-search-board/README.md) | Track applications, interviews, and follow-ups. | Enums, dates, filtering | Done |

Each project has its own README and a small test. The programs are deliberately small: the point is to show the problem-solving and Java basics clearly, not to hide them behind a big framework.

## Try a project

You need JDK 17 or newer. From the repository root:

```bash
cd 01-help-desk-tracker
mkdir -p out
javac --release 17 -d out src/*.java
java -cp out Main add "Laptop won't start"
java -cp out Main list
```

Each project README has its own commands and explains where its data is stored.

## How I organize the code

Each folder has its own README, source code, and a small test that checks useful behavior. I keep dependencies light so a recruiter can run a project with a JDK and a few commands. When a project needs a library or an API, its README will say so clearly.

## About this portfolio

I built this repository to show steady progress in Java: reading input, modeling data, handling mistakes, saving state, and testing the parts that matter. It is a collection of small completed programs, rather than one large unfinished app.
