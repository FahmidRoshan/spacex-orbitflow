from airflow import DAG # type: ignore
from airflow.operators.bash import BashOperator # type: ignore
from airflow.utils.dates import days_ago # type: ignore
from datetime import timedelta # type: ignore

default_args = {
    'owner': 'orbitflow',
    'depends_on_past': False,
    'email_on_failure': False,
    'retries': 1,
    'retry_delay': timedelta(minutes=2),
}

with DAG(
    'spacex_orbitflow_etl_dbt',
    default_args=default_args,
    description='Orchestrates Java ETL and dbt transforms for SpaceX data',
    schedule_interval='@daily',
    start_date=days_ago(1),
    catchup=False,
    tags=['spacex', 'orbitflow'],
) as dag:

    # 1. Run the Java ETL process
    run_java_etl = BashOperator(
        task_id='run_java_etl',
        bash_command='cd /opt/etl-java && ./run.sh ',
    )

    # 2. Run dbt transformation
    run_dbt_transform = BashOperator(
        task_id='run_dbt_transform',
        bash_command='cd /opt/transform-dbt && dbt run',
    )

    run_java_etl >> run_dbt_transform
