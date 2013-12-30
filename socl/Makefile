SCALAH=/home/igel/.scala
FSC=$(SCALAH)/bin/fsc
SCALA=$(SCALAH)/bin/scala

all:	init scala h lib
init:
	mkdir -p build
scala:
	$(FSC) -d build src/ccs/socl/*.scala & /bin/true
h:
	javah -classpath build -o src/jocl.h ccs.socl.CL$$
lib:
	g++ src/jocl.cpp -o build/libsocl.so -shared -fPIC -I/usr/lib/jvm/default-java/include -lOpenCL
run:
	$(SCALA) -classpath build -Djava.library.path=build ccs.socl.Main

clean:
	rm -rf build
