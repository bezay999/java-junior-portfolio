# Job Search Board

Job applications are easy to lose between inbox messages and browser tabs. This local board keeps the company, role, current stage, and next follow-up date in one place. `due` shows the conversations that need attention today or earlier.

With JDK 17+, run from this folder:

```bash
mkdir -p out
javac --release 17 -d out src/*.java
java -cp out Main add "Northstar" "Junior Java Developer" 2026-10-20
java -cp out Main list
java -cp out Main status 1 interview
java -cp out Main followup 1 2026-10-25
java -cp out Main due
```

Stages are `applied`, `interview`, `offer`, and `rejected`. `due` only includes active applications (`applied` and `interview`), not offers or rejections. Records are saved in `data/applications.tsv`, ignored by Git. Set `JOB_BOARD_FILE` to use another location. This is a private local tracker; do not commit real application details to a public repository.

To run the checks, compile `src/*.java test/*.java` into `out` and run `java -cp out JobBoardTest`.
