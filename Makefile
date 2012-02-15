VERSION=0.0.1
BINDIR=bin
JAVAC=/usr/bin/javac
JAVALIB=/usr/share/java/lib
GSONJAR=/usr/share/java/google-gson-2.1/gson-2.1.jar
SRCFILES=`find ./src -name *.java`

all:
	@mkdir -p $(BINDIR);
	@$(JAVAC) -cp $(GSONJAR) $(SRCFILES) -d $(BINDIR)

install:
	@echo

clean:
	@rm -rf $(BINDIR);
