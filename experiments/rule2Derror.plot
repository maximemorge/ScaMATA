set terminal pdf
set datafile separator ","
set style fill solid border rgb "black"
set auto x
set auto y
set grid
set ticslevel 0
set style data lines
set xlabel "m workers (10m tasks)"
set ylabel "Cmax"
set output 'rule2DerrorCmax.pdf'
plot  "data/cmax.csv" using 1:4:6 with filledcurves lc "light-blue" notitle,\
      "data/cmax.csv" using 1:5 with lines lc "dark-blue" title 'Gift',\
      "data/cmax.csv" using 1:9:11 with filledcurves lc "light-green" notitle,\
      "data/cmax.csv" using 1:10 with lines lc "dark-green" title 'Dis. Gift',\
      "data/cmax.csv" using 1:19:21 with filledcurves lc "light-red" notitle,\
      "data/cmax.csv" using 1:20 with lines lc "dark-red" title 'LP',\
      "data/cmax.csv" using 1:14:16 with filledcurves lc "plum" notitle,\
      "data/cmax.csv" using 1:15 with lines lc "dark-plum" title 'Exhau.'
set ylabel "Flowtime"
set output 'rule2DerrorFlowtime.pdf'
plot  "data/flowtime.csv" using 1:4:6 with filledcurves lc "light-blue" notitle,\
       "data/flowtime.csv" using 1:5 with lines lc "dark-blue" title 'Gift',\
       "data/flowtime.csv" using 1:9:11 with filledcurves lc "light-green" notitle,\
       "data/flowtime.csv" using 1:10 with lines lc "dark-green" title 'Dis. Gift'\
       "data/flowtime.csv" using 1:19:21 with filledcurves lc "light-red" notitle,\
       "data/flowtime.csv" using 1:20 with lines lc "dark-red" title 'LP',\
       "data/flowtime.csv" using 1:14:16 with filledcurves lc "plum" notitle,\
       "data/flowtime.csv" using 1:15 with lines lc "dark-plum" title 'Exhau.'