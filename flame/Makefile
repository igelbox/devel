all:	flame

flame:	main.cpp
	g++ -lGL -lglut -lGLU -ffast-math -O3 -funroll-loops -fopenmp -o $@ $<
clean:
	rm -f flame
