EXEC := myqt
SRCS := $(wildcard \
	src/*.cpp \
    )
HPPS := $(wildcard \
	src/*.hpp \
    )
MOCS := $(HPPS:.hpp=.moc)
INCS := -I /usr/include/qt
LIBS := -lQt5Core -lQt5Gui -lQt5Widgets
OBJS := $(SRCS:=.o)
DEPS := $(SRCS:=.d)

$(EXEC): $(OBJS) $(OBJS:.cpp.o=.moc.cpp.o)
	g++ $(OBJS) $(OBJS:.cpp.o=.moc.cpp.o) $(LIBS) -o $@

clean:
	rm -f $(OBJS) $(DEPS)
	rm -f src/*.ui.h

-include $(DEPS)

%.ui.h: %.ui
	uic $< -o $@

%.moc.cpp: %.hpp
	moc $< -o $@

%.cpp.o: %.cpp
	g++ -fPIC -c -MMD -MF $<.d $< $(INCS) -o $@
