#!/bin/bash

# quick hack to simulate creation of many, many encounters through the webapp
# invoke with /create_encounter.sh 2>logging.txt and check the times for each create encounter request

# couldn't find a better way without Internet connection to manipulate time output
export TIMEFORMAT=$'%3R'
OUTPUTFILE=output.txt

source run_report.config


#  echo "  OpenMRS Login"
  wget --quiet --keep-session-cookies --save-cookies cookies.txt --post-data "uname=$USER&pw=$PW" $BASE_URL/loginServlet

# process these patient ids

LIMIT=7923
counter=7801

while [ "$counter" -le $LIMIT ]
do

  # create 5 encounters per patient

  # Encounter details
  DATA="namesByLocale%5Ben%5D.name=dummy+$counter&shortNamesByLocale%5Ben%5D.name=&descriptionsByLocale%5Ben%5D.description=to+be+automatically+deleted+after+latest+concept+server+sync&%5Bx%5D.name=&concept.conceptClass=1&_concept.set=&concept.datatype=1&hiAbsolute=&hiCritical=&hiNormal=&lowNormal=&lowCritical=&lowAbsolute=&units=&_precise=&%5Bx%5D.sourceCode=&%5Bx%5D.source=1&concept.version=&_concept.retired=&action=Save+Concept"
  
  time wget --keep-session-cookies --load-cookies cookies.txt --output-document=output.htm --post-data $DATA "$BASE_URL/dictionary/concept.form"

  echo -n "$counter "
  let "counter+=1"
done           # No surprises, so far.

echo "  Cleanup"
#rm -f cookies.txt index.* output.*
