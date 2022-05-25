#!/bin/bash
##
# (C-Left) Piter.NL
#

#default header file:
headerfile=etc/header.txt

# Usage updateHeaders: <header file>
if [ -n "$1" ] ; then
    headerfile="$1"
fi

# replaceHeader()
replaceHeader()
{
 if [ -z "$1" ] ; then
    echo "Usage $0: "'<java file> <header file>'
    exit 1
 fi

 if [ -z "$2" ] ; then
    echo "Usage $0: "'<java file> <header file>'
    exit 1
 fi

 # print header
 cat $2

 # print java source without (old) header.
 # Use 'package' as code start

 cat "$1" | sed -n '
  # from start to "package"
  0,/package/ {
                # remove lines excluding 'package'
                /package/ !{
                             x
                             d
                }
                # strip bogus comments before package
                /package/ {
                             s/.*package/package/
                }
  }
  # print rest
  p
 '
}

for file in `find . -name "*.java"` ; do
  echo updating file: $file
  replaceHeader "$file" $headerfile  > "$file".new
  res=$?

  if [ $res != 0 ]  ; then
      echo "Error. replace script failed"
      exit 1
  fi

  diff "$file" "$file".new
  echo "proceed ? [Y/n/s/q] (Yes,No,Skip File, Quit)"
  read answer
  case $answer in
        ""|[yY])
              mv "$file" "$file".old
              mv "$file".new "$file"
              rm "$file".old
              ;;
        [nN])
              exit  2
              ;;
        [sS])
              echo "Skipping:" $file
	      rm "$file".new
              ;;
        [qQ])
              exit 2
              ;;
        *)
              echo "Error"
              ;;
  esac
done

