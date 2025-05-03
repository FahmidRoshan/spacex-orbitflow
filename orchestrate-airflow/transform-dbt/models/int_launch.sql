select
  *,
  case when success then 1 else 0 end as launch_status
from {{ ref('stg_launch') }}
