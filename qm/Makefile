SRCDIR  := .
BUILDIR := build
DISTDIR := dist
EXECUTABLE := $(DISTDIR)/qm
SOURCES := $(wildcard $(SRCDIR)/*.d*)
OBJECTS := $(addprefix $(BUILDIR)/, $(patsubst $(SRCDIR)/%, %, $(SOURCES:.d=.o)))
#.PHONY : $(EXECUTABLE)

all:	$(DISTDIR) $(EXECUTABLE)

run:	all
	exec ./$(EXECUTABLE)

$(EXECUTABLE): $(BUILDIR) $(OBJECTS)
	dmd $(OBJECTS) -of$@

$(BUILDIR)/%.o:	$(SRCDIR)/%.d
	dmd -c $< -I$(SRCDIR) -of$@

$(BUILDIR):
	mkdir $(BUILDIR)

$(DISTDIR):
	mkdir $(DISTDIR)

clean:
	rm -rf $(BUILDIR)
	rm -rf $(DISTDIR)
	rm -f $(EXECUTABLE)