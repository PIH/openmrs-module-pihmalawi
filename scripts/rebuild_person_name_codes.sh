#!/bin/bash
date >>/home/emradmin/rebuid.log
/home/emradmin/bart2/script/console <<EOF
PersonNameCode.rebuild_person_name_codes
exit
EOF
date >>/home/emradmin/rebuid.log
echo "DONE" >>/home/emradmin/rebuid.log

