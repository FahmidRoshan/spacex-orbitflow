# 🚀 SpaceX OrbitFlow

A modular ETL pipeline project that fetches SpaceX launch data using Java, transforms it using dbt, and orchestrates it with Apache Airflow. This project demonstrates a complete modern data engineering workflow.

&nbsp;
## 📁 Project Structure

```bash
spacex-orbitflow/
├── etl-java/ # Java-based ETL pipeline using Hikari + PostgreSQL
├── transform-dbt/ # dbt transformation layer
└── orchestrate-airflow/ # Airflow orchestration using Docker
```

&nbsp;
## ⚙️ Technologies Used

| Layer         | Stack                                |
|--------------|---------------------------------------|
| ETL          | Java, HikariCP, PostgreSQL, Maven     |
| Transform    | dbt (Data Build Tool)                 |
| Orchestration| Apache Airflow (Docker)               |
| Database     | PostgreSQL                            |
| Deployment   | Docker, GitHub                        |

---

## 🎯 Features

- ✅ Java-based ETL for fetching and persisting SpaceX API data
- ✅ Transformations using dbt models with version control
- ✅ Airflow DAGs to automate and monitor end-to-end flow
- ✅ Environment-based configuration using `.env`
- ✅ Modular, scalable, and production-ready setup

---

## 🚀 How It Works

1. **ETL (Java)**  
   Fetches data from [SpaceX Launch API](https://api.spacexdata.com/v4/launches) and stores it into PostgreSQL.

2. **Transform (dbt)**  
   Transforms raw data into clean, analysis-ready tables and views.

3. **Orchestrate (Airflow)**  
   Airflow DAG schedules and manages each task in the pipeline — ETL → Transform → Validate.

---

## 🛠️ Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/FahmidRoshan/spacex-orbitflow.git
cd spacex-orbitflow
```
### 2. Clone the Repository

#### Requirements:
- Java 17+
- Maven
- PostgreSQL running

#### Steps:
1. Navigate to `etl-java`
2. Create `.env` file with DB credentials:

    ```bash
    STORAGE_DB_URL=jdbc:postgresql://localhost:5432/spacex # named the database spacex 
    STORAGE_DB_USER=your_username
    STORAGE_DB_PASSWORD=your_password
    ```
3. Build & run:
    ```bash
    mvn clean install
    java -jar target/etl-java.jar
    ```
### 3. Setup dbt Transformations (transform-dbt)

#### Requirements:
- Python 3.8+
- dbt-postgres

#### Steps: 


```bash
cd transform-dbt
python3 -m venv venv # python/python3
source venv/bin/activate
pip3 install -r requirements.txt # pip/pip3
```

#### Configure `profiles.yml` : 

```bash
# ~/.dbt/profiles.yml
spacex:
  target: dev
  outputs:
    dev:
      type: postgres
      host: localhost
      user: your_username
      password: your_password
      port: 5432
      dbname: spacex
      schema: analytics
```

#### Run dbt:
```bash
dbt run
````

### 4. Setup Airflow Orchestration (orchestrate-airflow)

#### Requirements:
- Docker & Docker Compose

#### Steps:
```bash
cd orchestrate-airflow

# Create folders
mkdir -p dags logs plugins

# Initialize Airflow
echo -e "AIRFLOW_UID=$(id -u)" > .env
docker-compose up airflow-init

# Start Airflow
docker-compose up
```

Access Airflow UI: http://localhost:8080

Login: airflow / airflow

Place your DAG in `dags/orchestrate.py` and monitor it through the UI.

&nbsp; 
##  Next Steps 
 - [ ] Add CI/CD via GitHub Actions for testing Java + dbt builds
 - [ ] Resolve final Airflow DAG errors and test end-to-end orchestration
 - [ ] Integrate basic data validation and alerting in DAGs

&nbsp;
## Sample DAG Outline (Airflow)

&nbsp;
```python
from airflow import DAG
from airflow.operators.bash import BashOperator
from datetime import datetime

with DAG('spacex_orbitflow_dag', start_date=datetime(2025, 1, 1), schedule_interval='@daily', catchup=False) as dag:
    extract = BashOperator(
        task_id='run_etl_java',
        bash_command='cd /opt/etl-java && mvn clean compile exec:java'
    )

    transform = BashOperator(
        task_id='run_dbt_transform',
        bash_command='cd /opt/transform-dbt && dbt run'
    )

    extract >> transform
```


## License
MIT License

## Author
Fahmid Roshan  
[GitHub](https://github.com/FahmidRoshan)
