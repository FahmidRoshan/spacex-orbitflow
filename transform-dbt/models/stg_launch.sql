{{
  (config(materialized = 'view'))
}}

SELECT 
  id,
  name,
  date_utc::timestamp as launch_time_utc,
  success,
  rocket
FROM 
    {{source('public', 'spacex_launches')}}