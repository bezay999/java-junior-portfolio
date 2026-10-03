# Java projects, one problem at a time

This repository is my Java practice portfolio. The goal is to build ten small programs that solve recognizable problems, then make each one easy to run and easy to understand. I am starting with command-line tools because they let me focus on the logic before adding a user interface.

The projects are independent. You can open any folder without setting up the others. I will add them one at a time; the first one is ready now.

## Projects

| # | Project | What it does | Main Java practice | Status |
|---|---|---|---|---|
| 01 | [Help Desk Tracker](01-help-desk-tracker/README.md) | Create, close, and review support tickets saved between runs. | Classes, records, collections, file I/O | Done |
| 02 | Pantry Check | Track groceries and show what expires soon. | Dates, sorting, validation | Planned |
| 03 | Habit Calendar | Record daily habits and calculate streaks. | Maps, dates, small reports | Planned |
| 04 | Shared Bill Splitter | Split a group bill fairly, including rounding. | Decimal arithmetic, edge cases | Planned |
| 05 | Event Check-In | Import a guest list and record arrivals. | CSV files, searching, duplicate handling | Planned |
| 06 | Flashcard Desk | Practice a question deck and revisit missed cards. | Collections, randomization, state | Planned |
| 07 | Log Lens | Read an application log and summarize errors. | Text parsing, streams, reporting | Planned |
| 08 | Route Planner | Find a route through a small network of stops. | Graphs, breadth-first search | Planned |
| 09 | Weather Notes | Store daily observations and compare weeks. | Data modeling, aggregation | Planned |
| 10 | Job Search Board | Track applications, interviews, and follow-ups. | Enums, dates, filtering | Planned |

These are ideas for the portfolio, not ten finished apps. I may adjust a planned idea if I find a better problem to solve while building it.

## Start with project 01

You need JDK 17 or newer. From the repository root:

```bash
cd 01-help-desk-tracker
mkdir -p out
javac --release 17 -d out src/*.java
java -cp out Main add "Laptop won't start"
java -cp out Main list
```

The [project README](01-help-desk-tracker/README.md) has every command and explains where tickets are stored.

## How I organize the code

Each folder has its own README, source code, and a small test that checks useful behavior. I keep dependencies light so a recruiter can run a project with a JDK and a few commands. When a project needs a library or an API, its README will say so clearly.

## About this portfolio

I built this repository to show steady progress in Java: reading input, modeling data, handling mistakes, saving state, and testing the parts that matter. It is a collection of small completed programs, rather than one large unfinished app.
