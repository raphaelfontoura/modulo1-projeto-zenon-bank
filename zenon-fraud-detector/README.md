# Zenon Fraud Detector

A Java application that analyzes mobile payment transaction data (the [PaySim synthetic dataset](https://www.kaggle.com/datasets/ealaxi/paysim1)) to detect and report fraudulent transactions.

This is part of the **Zenon Bank** project — a university assignment exploring clean architecture, streams, records, and repository patterns in Java.

## Features

- **CSV ingestion** — reads the PaySim transaction log, tolerating malformed lines (they are skipped with an error message to stderr).
- **Fraud analysis** — computes total frauds, highest-value frauds, suspicious clients, total fraud loss, and fraud counts by transaction type.
- **Report generation** — a streaming aggregate report (total transactions, total frauds, total amount) with locale-aware number/currency formatting and i18n messages (pt-BR / en / default).
- **Repository layer** — `TransactionRepository` with two in-memory implementations (linear list lookup vs. `TreeMap` indexed lookup) for comparing lookup performance.

## Requirements

- Java 21+ (uses `Locale.of`, records, sealed-free modern syntax)
- Gradle (the wrapper `./gradlew` is included)

## Build & Run

```bash
./gradlew build
./gradlew run
```

## Dataset Setup

The project expects the PaySim CSV file at:

```text
data/PS_20174392719_1491204439457_log.csv
```

This file was obtained from the Kaggle PaySim dataset page and is used by the ingestion/learning flow:

- Kaggle source: https://www.kaggle.com/datasets/ealaxi/paysim1?resource=download
- Expected local path: `data/PS_20174392719_1491204439457_log.csv`
- Entry point that uses it: `src/main/java/br/com/zenom/cli/IngestionMain.java`

If you download the dataset yourself, place the CSV in the `data/` directory with that filename before running the ingestion example.

To run a specific entry point directly:

```bash
# Fraud analyzer CLI demo
java -cp build/classes/java/main br.com.zenom.cli.Main

# Dataset ingestion / repository loading example
java -cp build/classes/java/main br.com.zenom.cli.IngestionMain

# i18n report generator
java -cp build/classes/java/main br.com.zenom.report.ReportMain
```

> The entry points use `static void main()` (the implicitly-declared launcher method, Java 21+). Run them with `java` — no `public static void main(String[])` wrapper is required.

## Project Structure

```
src/main/java/br/com/zenom/
├── cli/                    # Main entry point (fraud analyzer demo)
├── fraud/                  # Domain model
│   ├── Transaction.java    # Record: step, type, amount, origin, recipient, fraud flags
│   ├── Currency.java       # Money value wrapper (BigDecimal, non-negative)
│   ├── Customer.java       # Client record with old/new balance
│   └── TransactionType.java# CASH_IN, CASH_OUT, DEBIT, PAYMENT, TRANSFER
├── ingestor/               # CSV parsing → Transaction records
├── report/                 # Streaming report + i18n statistics output
└── repository/             # TransactionRepository (List & Map implementations)

src/main/resources/
├── reportmessages.properties     # Default (pt-BR) report labels
├── reportmessages_pt.properties  # pt-BR labels
└── reportmessages_en.properties  # English labels

data/
├── PS_20174392719_1491204439457_log.csv   # Full PaySim dataset (~6.3M rows)
└── paysim_with_bad_data.csv               # Small file with malformed rows
```

## Data Format

The PaySim CSV header:

```
step,type,amount,nameOrig,oldbalanceOrg,newbalanceOrig,nameDest,oldbalanceDest,newbalanceDest,isFraud,isFlaggedFraud
```

| Column | Description |
|--------|-------------|
| `step` | Time step (1 unit = 1 hour) |
| `type` | Transaction type |
| `amount` | Transaction amount |
| `nameOrig` / `nameDest` | Origin / destination customer id |
| `oldbalanceOrg` / `newbalanceOrig` | Origin balance before / after |
| `oldbalanceDest` / `newbalanceDest` | Destination balance before / after |
| `isFraud` | `1` if fraudulent |
| `isFlaggedFraud` | `1` if flagged by the bank's system |

## Dataset License & Attribution

The PaySim dataset is an academic synthetic simulator (E. A. Lopez-Rojas, A. Elmir, and S. Axelsson, *"PaySim: A financial mobile money simulator for fraud detection"*, 2016) based on real transaction aggregates. See the [Kaggle page](https://www.kaggle.com/datasets/ealaxi/paysim1) for details.

## License

Academic project — for educational purposes only.
