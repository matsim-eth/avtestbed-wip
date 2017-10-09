#!/usr/bin/awk -f

# usage:
# fromMergedRecordsToFakeActivities.awk [minNPickUps=0] [maxNPickUps=10] [minTime=0] [maxTime=24] mergedCounts > events.xml

function logmsg( m ) {
	print m >> "/dev/stderr"
}

BEGIN {
	minNPickUps = 0
	maxNPickUps = 10
	minTime=0
	maxTime=24
	nextprint = 1

	print "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
}

NR == 1 {
	logmsg( "using min pick ups count " minNPickUps )
	logmsg( "using max pick ups count " maxNPickUps )
	logmsg( "using min time " minTime )
	logmsg( "using max time " maxTime )

	# convert from hours to seconds
	minTime = minTime * 3600
	maxTime = maxTime * 3600

	print ""
	print "<!-- events generated by fromMergedRecordsToFakeActivities.awk -->"
	print "<!-- using min pick ups count " minNPickUps " -->"
	print "<!-- using max pick ups count " maxNPickUps " -->"
	print "<!-- using min time " minTime " -->"
	print "<!-- using max time " maxTime " -->"
	print "<!-- input file " FILENAME " -->"
	print ""

	print "<events version=\"1.0\">"
}

NR == nextprint {
	logmsg( "reading line # " NR )
	nextprint = nextprint * 2
}

(NR > 1) &&
	# count passengers
	($10 > minNPickUps) &&
	($10 <= maxNPickUps) &&
	# departure time
	($7 >= minTime) &&
	($7 <= maxTime) {
	print "<event time=\"" $7 "\" type=\"actstart\" person=\"" $2 "\" link=\"" $5 "\" actType=\"p\"  />"
	# do not close events
}

END {
	print "</events>"
}
