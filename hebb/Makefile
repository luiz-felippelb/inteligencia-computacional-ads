TARGET=main
CC=gcc
DEBUG=-g
OPT=-O0
WARN=-Wall
CURSES=-lncurses
HEBB=hebb.c
TINFO=-ltinfo
CCFLAGS=$(DEBUG) $(OPT) $(WARN)
LD=gcc
OBJS=main.c
all: main.c
	$(LD) -o $(TARGET) $(OBJS) $(HEBB) $(DEBUG) $(OPT) $(WARN) $(CURSES) $(TINFO)
