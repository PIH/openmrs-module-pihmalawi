select person_id, gender, birthdate
from person p
where gender = @gender
and voided = 0
limit 10